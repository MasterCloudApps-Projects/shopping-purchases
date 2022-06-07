package es.codeurjc.mca.tfm.purchases.application.dtos.requests;

import lombok.Builder;
import lombok.Data;

/**
 * Set item in shopping cart request DTO.
 */
@Data
@Builder
public class SetItemRequest {

  /**
   * Unit price.
   */
  private Double unitPrice;

  /**
   * Quantity.
   */
  private Integer quantity;

}
