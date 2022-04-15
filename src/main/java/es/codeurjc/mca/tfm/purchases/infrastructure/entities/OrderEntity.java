package es.codeurjc.mca.tfm.purchases.infrastructure.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order database entity.
 */
@Entity(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

  /**
   * Order identifier.
   */
  @Id
  @NotNull
  private Long id;

  /**
   * Shopping cart.
   */
  @OneToOne
  @NotNull
  private ShoppingCartEntity shoppingCart;

  /**
   * State.
   */
  @NotBlank
  private String state;

}
