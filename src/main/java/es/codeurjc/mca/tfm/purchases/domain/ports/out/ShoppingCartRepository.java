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

  /**
   * Get shopping cart by identifier and user.
   *
   * @param id     shopping cart identifier.
   * @param userId user identifier.
   * @return optional of shopping cart with id and user.
   */
  Optional<ShoppingCartDto> getByIdAndUser(Long id, Integer userId);

  /**
   * Delete a shopping cart by id.
   *
   * @param id shopping cart identifer.
   */
  void delete(Long id);

  /**
   * Complete a shopping cart by id.
   *
   * @param shoppingCartDto DTO with shopping cart info.
   */
  void complete(ShoppingCartDto shoppingCartDto);

}
