package es.codeurjc.mca.tfm.purchases.domain.dtos;

import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;

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
  private OrderState state;

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
   */
  public OrderDto(Long id, ShoppingCartDto shoppingCart,
      OrderState state) {
    this.id = id;
    this.shoppingCart = shoppingCart;
    this.state = state;
  }

  public Long getId() {
    return id;
  }

  public ShoppingCartDto getShoppingCart() {
    return shoppingCart;
  }

  public OrderState getState() {
    return state;
  }
}
