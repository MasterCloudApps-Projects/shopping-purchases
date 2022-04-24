package es.codeurjc.mca.tfm.purchases.domain.services.impl;

import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;
import es.codeurjc.mca.tfm.purchases.domain.services.OrderStateService;

/**
 * Final state abstract base class.
 */
public abstract class FinalAbstractBaseOrderState implements OrderStateService {

  /**
   * Order repository.
   */
  private final OrderRepository orderRepository;

  /**
   * Constructor.
   *
   * @param orderRepository order repository.
   */
  public FinalAbstractBaseOrderState(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  /**
   * Perform action for current state keeping in mind previous state.
   *
   * @param previousState previous state.
   * @param currentState  current state.
   * @param orderDto      order DTO.
   */
  @Override
  public void performAction(OrderState previousState, OrderState currentState, OrderDto orderDto) {
    this.orderRepository.finish(orderDto);
  }

}
