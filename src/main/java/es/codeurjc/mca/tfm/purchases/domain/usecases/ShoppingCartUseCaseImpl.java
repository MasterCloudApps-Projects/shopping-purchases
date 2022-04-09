package es.codeurjc.mca.tfm.purchases.domain.usecases;

import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IncompleteShoppingCartAlreadyExistsException;
import es.codeurjc.mca.tfm.purchases.domain.models.ShoppingCart;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.ShoppingCartUseCase;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.ShoppingCartRepository;

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
   * Maps shopping cart domain entity to DTO.
   *
   * @param shoppingCart domain entity to map.
   * @return mapped DTO.
   */
  private ShoppingCartDto map(ShoppingCart shoppingCart) {
    return new ShoppingCartDto(shoppingCart);
  }

}
