package es.codeurjc.mca.tfm.purchases.infrastructure.events;

import java.util.List;
import lombok.Data;

/**
 * Order shopping cart.
 */
@Data
public class OrderShoppingCart {

  /**
   * Shopping cart identifier.
   */
  private Long id;

  /**
   * Identifier of the user owner of the shopping cart.
   */
  private Integer userId;

  /**
   * Indicates if the shopping cart is completed.
   */
  private boolean completed;

  /**
   * List of items of the shopping cart.
   */
  private List<ShoppingCartItem> items;

  /**
   * Total price.
   */
  private Double totalPrice;

}
