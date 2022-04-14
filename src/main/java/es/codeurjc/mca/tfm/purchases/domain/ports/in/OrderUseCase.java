package es.codeurjc.mca.tfm.purchases.domain.ports.in;

import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;

/**
 * Order use case interface.
 */
public interface OrderUseCase {

  /**
   * Create an order.
   *
   * @param shoppingCartDto shopping cart associated with the order.
   * @return created order DTO.
   */
  OrderDto create(ShoppingCartDto shoppingCartDto);

}
