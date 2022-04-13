package es.codeurjc.mca.tfm.purchases.domain.ports.in;

import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import java.util.Optional;

/**
 * Shopping cart use case interface.
 */
public interface ShoppingCartUseCase {

  /**
   * Create a shopping cart for passed user.
   *
   * @param userId user identifier.
   * @return created shopping cart DTO.
   */
  ShoppingCartDto create(Integer userId);

  /**
   * Gets a shopping cart with passed id and user.
   *
   * @param id     shopping cart identifier.
   * @param userId user identifier.
   * @return an optional of shopping cart DTO.
   */
  Optional<ShoppingCartDto> get(Long id, Integer userId);

  /**
   * Deletes a shopping cart with passed id and user.
   *
   * @param id     shopping cart identifier.
   * @param userId user identifier.
   * @return an optional of deleted shopping cart DTO.
   */
  Optional<ShoppingCartDto> delete(Long id, Integer userId);

}
