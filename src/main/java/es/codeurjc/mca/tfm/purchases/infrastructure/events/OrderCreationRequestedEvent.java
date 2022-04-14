package es.codeurjc.mca.tfm.purchases.infrastructure.events;

import lombok.Data;

/**
 * Requested order creation event.
 */
@Data
public class OrderCreationRequestedEvent {

  /**
   * Order identifier.
   */
  private Long id;

  /**
   * Shopping cart.
   */
  private OrderShoppingCart shoppingCart;

  /**
   * State.
   */
  private String state;

}
