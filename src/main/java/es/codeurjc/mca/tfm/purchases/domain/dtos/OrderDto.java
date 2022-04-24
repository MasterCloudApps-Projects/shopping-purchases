package es.codeurjc.mca.tfm.purchases.domain.dtos;

import java.util.List;
import java.util.Optional;

/**
 * Order DTO.
 */
public class OrderDto {

  /**
   * Order identifier.
   */
  private Long id;

  /**
   * Shopping cart.
   */
  private ShoppingCartDto shoppingCart;

  /**
   * Order state.
   */
  private String state;

  /**
   * List of errors in case of exist any.
   */
  private Optional<List<String>> errors;

  /**
   * Constructor.
   */
  public OrderDto() {
    super();
  }

  /**
   * Constructor.
   *
   * @param id           identifier.
   * @param shoppingCart associated shopping cart.
   * @param state        state.
   * @param errors       an optional with list of errors if any.
   */
  public OrderDto(Long id, ShoppingCartDto shoppingCart, String state,
      Optional<List<String>> errors) {
    this.id = id;
    this.shoppingCart = shoppingCart;
    this.state = state;
    this.errors = errors;
  }

  public Long getId() {
    return id;
  }

  public ShoppingCartDto getShoppingCart() {
    return shoppingCart;
  }

  public String getState() {
    return state;
  }

  public Optional<List<String>> getErrors() {
    return errors;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setShoppingCart(ShoppingCartDto shoppingCart) {
    this.shoppingCart = shoppingCart;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setErrors(Optional<List<String>> errors) {
    this.errors = errors;
  }
}
