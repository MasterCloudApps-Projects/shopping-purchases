package es.codeurjc.mca.tfm.purchases.domain.services.impl;

import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;
import es.codeurjc.mca.tfm.purchases.domain.services.OrderStateService;

/**
 * Validating items order state service implementation.
 */
public class ValidatingItemsOrderStateServiceImpl implements OrderStateService {

  /**
   * Order repository.
   */
  private final OrderRepository orderRepository;

  /**
   * Constructor.
   *
   * @param orderRepository order repository.
   */
  public ValidatingItemsOrderStateServiceImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  /**
   * Get service state.
   *
   * @return service state.
   */
  @Override
  public String getState() {
    return OrderState.VALIDATING_ITEMS.name();
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
    this.orderRepository.validateItems(orderDto);
  }

}
