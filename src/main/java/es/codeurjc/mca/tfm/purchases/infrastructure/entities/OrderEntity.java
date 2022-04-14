package es.codeurjc.mca.tfm.purchases.infrastructure.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Data;

/**
 * Order database entity.
 */
@Entity
@Data
public class OrderEntity {

  /**
   * Order identifier.
   */
  @Id
  private Long id;

  /**
   * Shopping cart.
   */
  @OneToOne
  private ShoppingCartEntity shoppingCart;

  /**
   * State.
   */
  private String state;

}
