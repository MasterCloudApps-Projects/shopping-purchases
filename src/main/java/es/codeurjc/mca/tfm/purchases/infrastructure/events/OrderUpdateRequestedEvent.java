package es.codeurjc.mca.tfm.purchases.infrastructure.events;

import java.util.List;
import java.util.Optional;
import lombok.Data;

/**
 * Order update requested event.
 */
@Data
public class OrderUpdateRequestedEvent {

  /**
   * Order identifier.
   */
  private Long id;

  /**
   * State.
   */
  private String state;

  /**
   * List of errors if exists.
   */
  private Optional<List<String>> errors;

}
