package es.codeurjc.mca.tfm.purchases.infrastructure.entities;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * Shopping cart database entity.
 */
@Entity(name = "shopping_carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "json", typeClass = JsonStringType.class)
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
  @Type(type = "json")
  @Column(columnDefinition = "json")
  private String items;

  /**
   * Total price.
   */
  @NotNull
  private Double totalPrice;

}
