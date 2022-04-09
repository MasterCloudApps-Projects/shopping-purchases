package es.codeurjc.mca.tfm.purchases.domain.ports.out;

import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import java.util.Optional;

/**
 * Shopping cart repository interface.
 */
public interface ShoppingCartRepository {

  /**
   * Create a shopping cart.
   *
   * @param shoppingCartDto DTO with shopping cart info.
   */
  void create(ShoppingCartDto shoppingCartDto);

  /**
   * Get the current only incomplete shopping cart for passed user.
   *
   * @param userId user identifier.
   * @return optional of incomplete shopping cart for user if exists, else empty.
   */
  Optional<ShoppingCartDto> getIncompleteByUser(Integer userId);

}
