package es.codeurjc.mca.tfm.purchases.domain.usecases;

import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IllegalShoppingCartStateException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IncompleteShoppingCartAlreadyExistsException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.InvalidItemException;
import es.codeurjc.mca.tfm.purchases.domain.mappers.DomainMapper;
import es.codeurjc.mca.tfm.purchases.domain.models.ShoppingCart;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.ShoppingCartUseCase;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.ShoppingCartRepository;
import java.util.Optional;

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
  public ShoppingCartUseCaseImpl(final ShoppingCartRepository shoppingCartRepository) {
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
    ShoppingCartDto shoppingCartDto = DomainMapper.map(shoppingCart);
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
      ShoppingCart shoppingCart = DomainMapper.map(shoppingCartDtoOptional.get());
      if (!shoppingCart.isDeletable()) {
        throw new IllegalShoppingCartStateException("Can't delete completed cart");
      }
      this.shoppingCartRepository.delete(id);
    }
    return shoppingCartDtoOptional;
  }

  /**
   * Complete a shopping cart with passed id and user.
   *
   * @param id     shopping cart identifier.
   * @param userId user identifier.
   * @return an optional of completed shopping cart DTO.
   */
  @Override
  public Optional<ShoppingCartDto> complete(Long id, Integer userId) {
    Optional<ShoppingCartDto> shoppingCartDtoOptional = this.shoppingCartRepository.getByIdAndUser(
        id, userId);
    if (shoppingCartDtoOptional.isPresent()) {
      ShoppingCart shoppingCart = DomainMapper.map(shoppingCartDtoOptional.get());
      if (!shoppingCart.isCompletable()) {
        throw new IllegalShoppingCartStateException("Shopping cart can't be completed.");
      }
      shoppingCart.complete();
      ShoppingCartDto shoppingCartDto = DomainMapper.map(shoppingCart);
      this.shoppingCartRepository.complete(shoppingCartDto);
      shoppingCartDtoOptional = Optional.of(shoppingCartDto);
    }
    return shoppingCartDtoOptional;
  }

  /**
   * Set item into shopping cart with passed id and user.
   *
   * @param id        shopping cart identifier.
   * @param userId    user identifier.
   * @param productId product identifier.
   * @param unitPrice item unit price.
   * @param quantity  item quantity.
   * @return an optional of shopping cart DTO with item set.
   */
  @Override
  public Optional<ShoppingCartDto> setItem(Long id, Integer userId, Integer productId,
      Double unitPrice, Integer quantity) {
    Optional<ShoppingCartDto> shoppingCartDtoOptional = this.shoppingCartRepository.getByIdAndUser(
        id, userId);
    if (shoppingCartDtoOptional.isPresent()) {
      ShoppingCart shoppingCart = DomainMapper.map(shoppingCartDtoOptional.get());
      if (shoppingCart.isCompleted()) {
        throw new IllegalShoppingCartStateException("Can't set item to completed cart");
      }
      if (!shoppingCart.setItem(productId, unitPrice, quantity)) {
        throw new InvalidItemException(
            "Can't set item, check item unit price and quantity to be greater than 0");
      }
      ShoppingCartDto shoppingCartDto = DomainMapper.map(shoppingCart);
      this.shoppingCartRepository.updateItems(shoppingCartDto);
      shoppingCartDtoOptional = Optional.of(shoppingCartDto);
    }
    return shoppingCartDtoOptional;
  }

  /**
   * Delete item from shopping cart with passed id and user.
   *
   * @param id        shopping cart identifier.
   * @param userId    user identifier.
   * @param productId product identifier.
   * @return an optional of shopping cart DTO with item deleted.
   */
  @Override
  public Optional<ShoppingCartDto> deleteItem(Long id, Integer userId, Integer productId) {
    Optional<ShoppingCartDto> shoppingCartDtoOptional = this.shoppingCartRepository.getByIdAndUser(
        id, userId);
    if (shoppingCartDtoOptional.isPresent()) {
      ShoppingCart shoppingCart = DomainMapper.map(shoppingCartDtoOptional.get());
      if (shoppingCart.isCompleted()) {
        throw new IllegalShoppingCartStateException("Can't delete item from completed cart");
      }
      if (shoppingCart.deleteItem(productId)) {
        ShoppingCartDto shoppingCartDto = DomainMapper.map(shoppingCart);
        this.shoppingCartRepository.updateItems(shoppingCartDto);
        shoppingCartDtoOptional = Optional.of(shoppingCartDto);
      }
    }
    return shoppingCartDtoOptional;
  }

}
