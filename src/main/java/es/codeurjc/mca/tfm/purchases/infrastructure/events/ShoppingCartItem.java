package es.codeurjc.mca.tfm.purchases.infrastructure.events;

import lombok.Data;

/**
 * Shopping cart items.
 */
@Data
public class ShoppingCartItem {

  /**
   * Product identifier.
   */
  private Integer productId;

  /**
   * Unit price.
   */
  private Double unitPrice;

  /**
   * Quantity.
   */
  private Integer quantity;

  /**
   * Total price.
   */
  private Double totalPrice;

}
