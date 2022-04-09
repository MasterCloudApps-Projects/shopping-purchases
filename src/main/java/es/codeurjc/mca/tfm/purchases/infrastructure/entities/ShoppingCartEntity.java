package es.codeurjc.mca.tfm.purchases.infrastructure.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

/**
 * Shopping cart database entity.
 */
@Entity
@Data
public class ShoppingCartEntity {

  /**
   * Shopping cart identifier.
   */
  @Id
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
  private String items;

  /**
   * Total price.
   */
  private Double totalPrice;

}
