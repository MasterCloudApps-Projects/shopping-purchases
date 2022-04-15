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

  /**
   * Complete a shopping cart with passed id and user.
   *
   * @param id     shopping cart identifier.
   * @param userId user identifier.
   * @return an optional of completed shopping cart DTO.
   */
  Optional<ShoppingCartDto> complete(Long id, Integer userId);

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
  Optional<ShoppingCartDto> setItem(Long id, Integer userId, Integer productId, Double unitPrice,
      Integer quantity);

}
