package es.codeurjc.mca.tfm.purchases.domain.usecases;

import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IllegalShoppingCartStateException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IncompleteShoppingCartAlreadyExistsException;
import es.codeurjc.mca.tfm.purchases.domain.models.Item;
import es.codeurjc.mca.tfm.purchases.domain.models.ShoppingCart;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.ShoppingCartUseCase;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.ShoppingCartRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Shopping cart use case implementation.
 */
public class ShoppingCartUseCaseImpl implements ShoppingCartUseCase {

  /**
   * Shopping cart repository.
   */
  private final ShoppingCartRepository shoppingCartRepository;

  /**
   * Constructor.
   *
   * @param shoppingCartRepository shopping cart repository.
   */
  public ShoppingCartUseCaseImpl(ShoppingCartRepository shoppingCartRepository) {
    this.shoppingCartRepository = shoppingCartRepository;
  }

  /**
   * Create a shopping cart for passed user.
   *
   * @param userId user identifier.
   * @return created shopping cart DTO.
   */
  @Override
  public ShoppingCartDto create(Integer userId) {
    this.shoppingCartRepository.getIncompleteByUser(userId).ifPresent(shoppingCart -> {
      throw new IncompleteShoppingCartAlreadyExistsException(
          "Already exists incomplete shopping cart with id=" + shoppingCart.getId());
    });
    ShoppingCart shoppingCart = new ShoppingCart(userId);
    ShoppingCartDto shoppingCartDto = this.map(shoppingCart);
    this.shoppingCartRepository.create(shoppingCartDto);

    return shoppingCartDto;
  }

  /**
   * Gets a shopping cart with passed id and user.
   *
   * @param id     shopping cart identifier.
   * @param userId user identifier.
   * @return an optional of shopping cart DTO.
   */
  @Override
  public Optional<ShoppingCartDto> get(Long id, Integer userId) {
    return this.shoppingCartRepository.getByIdAndUser(id, userId);
  }

  /**
   * Deletes a shopping cart with passed id and user.
   *
   * @param id     shopping cart identifier.
   * @param userId user identifier.
   * @return an optional of deleted shopping cart DTO.
   */
  @Override
  public Optional<ShoppingCartDto> delete(Long id, Integer userId) {
    Optional<ShoppingCartDto> shoppingCartDtoOptional = this.shoppingCartRepository.getByIdAndUser(
        id, userId);
    if (shoppingCartDtoOptional.isPresent()) {
      ShoppingCart shoppingCart = this.map(shoppingCartDtoOptional.get());
      if (!shoppingCart.isDeletable()) {
        throw new IllegalShoppingCartStateException("Can't delete completed cart");
      }
      this.shoppingCartRepository.delete(id);
    }
    return shoppingCartDtoOptional;
  }

  /**
   * Maps shopping cart domain entity to DTO.
   *
   * @param shoppingCart domain entity to map.
   * @return mapped DTO.
   */
  private ShoppingCartDto map(ShoppingCart shoppingCart) {
    return new ShoppingCartDto(shoppingCart);
  }

  /**
   * Maps shopping cart DTO to domain entity.
   *
   * @param shoppingCartDto DTO to map.
   * @return mapped domain entity.
   */
  private ShoppingCart map(ShoppingCartDto shoppingCartDto) {
    List<Item> items = shoppingCartDto.getItems().stream()
        .map(itemDto -> new Item(itemDto.getProductId(), itemDto.getUnitPrice(),
            itemDto.getQuantity(), itemDto.getTotalPrice()))
        .collect(Collectors.toList());
    return new ShoppingCart(shoppingCartDto.getId(), shoppingCartDto.getUserId(),
        shoppingCartDto.isCompleted(), items, shoppingCartDto.getTotalPrice());
  }

}
