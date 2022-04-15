package es.codeurjc.mca.tfm.purchases.application.dtos.requests;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
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
  @DecimalMin(value = "0.0", inclusive = false, message = "unitPrice must be greater than 0.0")
  private Double unitPrice;

  /**
   * Quantity.
   */
  @Min(value = 1, message = "quantity must be greater than 0")
  private Integer quantity;

}
