package es.codeurjc.mca.tfm.purchases.application.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shopping cart response DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartResponseDto {

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
  private List<ItemResponseDto> items;

  /**
   * Total price.
   */
  private Double totalPrice;

}
