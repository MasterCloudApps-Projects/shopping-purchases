package es.codeurjc.mca.tfm.purchases.domain.services.impl;

import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;

/**
 * Done order state service implementation.
 */
public class DoneOrderStateServiceImpl extends FinalAbstractBaseOrderState {

  /**
   * Constructor.
   *
   * @param orderRepository order repository.
   */
  public DoneOrderStateServiceImpl(OrderRepository orderRepository) {
    super(orderRepository);
  }

  /**
   * Get service state.
   *
   * @return service state.
   */
  @Override
  public String getState() {
    return OrderState.DONE.name();
  }

}
