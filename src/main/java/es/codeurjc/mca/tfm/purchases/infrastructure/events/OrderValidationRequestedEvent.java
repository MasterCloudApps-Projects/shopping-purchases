package es.codeurjc.mca.tfm.purchases.infrastructure.events;

import lombok.Data;

/**
 * Requested order validation event.
 */
@Data
public class OrderValidationRequestedEvent {

  /**
   * Order identifier.
   */
  private Long id;

  /**
   * Shopping cart.
   */
  private OrderShoppingCart shoppingCart;

  /**
   * Success state.
   */
  private String successState;

  /**
   * Success state.
   */
  private String failureState;

}
