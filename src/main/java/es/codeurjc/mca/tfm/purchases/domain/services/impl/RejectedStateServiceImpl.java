package es.codeurjc.mca.tfm.purchases.domain.services.impl;

import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;

/**
 * Rejected order state service implementation.
 */
public class RejectedStateServiceImpl extends FinalAbstractBaseOrderState {

  /**
   * Constructor.
   *
   * @param orderRepository order repository.
   */
  public RejectedStateServiceImpl(OrderRepository orderRepository) {
    super(orderRepository);
  }

  /**
   * Get service state.
   *
   * @return service state.
   */
  @Override
  public String getState() {
    return OrderState.REJECTED.name();
  }

}
