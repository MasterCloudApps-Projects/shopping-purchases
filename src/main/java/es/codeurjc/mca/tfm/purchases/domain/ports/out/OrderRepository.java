package es.codeurjc.mca.tfm.purchases.domain.ports.out;

import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;

/**
 * Order repository interface.
 */
public interface OrderRepository {

  /**
   * Create an order.
   *
   * @param orderDto DTO with order info.
   */
  void create(OrderDto orderDto);

}
