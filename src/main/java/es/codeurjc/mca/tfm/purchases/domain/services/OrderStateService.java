package es.codeurjc.mca.tfm.purchases.domain.services;

import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;

/**
 * Order state service interface.
 */
public interface OrderStateService {

  /**
   * Get service state.
   *
   * @return service state.
   */
  String getState();

  /**
   * Perform action for current state keeping in mind previous state.
   *
   * @param previousState previous state.
   * @param currentState  current state.
   * @param orderDto      order DTO.
   */
  void performAction(OrderState previousState, OrderState currentState, OrderDto orderDto);

}
