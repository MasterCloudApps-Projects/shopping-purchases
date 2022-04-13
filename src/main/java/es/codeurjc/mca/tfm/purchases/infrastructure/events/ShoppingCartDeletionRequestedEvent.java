package es.codeurjc.mca.tfm.purchases.infrastructure.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Delete shopping cart event.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDeletionRequestedEvent {

  /**
   * Shopping cart identifier.
   */
  private Long id;

}
