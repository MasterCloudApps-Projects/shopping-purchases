package es.codeurjc.mca.tfm.purchases.domain.ports.in;

import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;

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

}
