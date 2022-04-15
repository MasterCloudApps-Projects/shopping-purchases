package es.codeurjc.mca.tfm.purchases.infrastructure.events;

import java.util.List;
import lombok.Data;

/**
 * Set item to shopping cart requested event.
 */
@Data
public class SetItemToShoppingCartRequestedEvent {

  /**
   * Shopping cart identifier.
   */
  private Long id;

  /**
   * List of items of the shopping cart.
   */
  private List<ShoppingCartItem> items;

  /**
   * Total price.
   */
  private Double totalPrice;

}
