package es.codeurjc.mca.tfm.purchases.infrastructure.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shopping cart database entity.
 */
@Entity(name = "shopping_carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartEntity {

  /**
   * Shopping cart identifier.
   */
  @Id
  @NotNull
  private Long id;

  /**
   * Identifier of the user owner of the shopping cart.
   */
  @NotNull
  private Integer userId;

  /**
   * Indicates if the shopping cart is completed.
   */
  @NotNull
  private boolean completed;

  /**
   * List of items of the shopping cart.
   */
  @NotBlank
  private String items;

  /**
   * Total price.
   */
  @NotNull
  private Double totalPrice;

}
