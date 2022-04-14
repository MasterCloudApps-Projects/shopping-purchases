package es.codeurjc.mca.tfm.purchases.infrastructure.events;

import lombok.Data;

/**
 * Created order event.
 */
@Data
public class OrderCreatedEvent {

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
