package es.codeurjc.mca.tfm.purchases.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Item response DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {

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
