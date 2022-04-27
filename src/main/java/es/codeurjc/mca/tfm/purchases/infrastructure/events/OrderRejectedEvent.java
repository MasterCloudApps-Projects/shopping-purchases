package es.codeurjc.mca.tfm.purchases.infrastructure.events;

import java.util.List;
import java.util.Optional;
import lombok.Data;

/**
 * Order rejected event.
 */
@Data
public class OrderRejectedEvent {

  /**
   * Order identifier.
   */
  private Long id;

  /**
   * Shopping cart.
   */
  private OrderShoppingCart shoppingCart;

  /**
   * List of errors if exists.
   */
  private Optional<List<String>> errors;

}
