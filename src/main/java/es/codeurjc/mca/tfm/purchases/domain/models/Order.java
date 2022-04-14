package es.codeurjc.mca.tfm.purchases.domain.models;

/**
 * Order domain entity.
 */
public class Order {

  /**
   * Order identifier.
   */
  private Long id;

  /**
   * Shopping cart.
   */
  private ShoppingCart shoppingCart;

  /**
   * Order state.
   */
  private OrderState state;

  /**
   * Constructor.
   */
  public Order(ShoppingCart shoppingCart) {
    super();
    this.id = System.currentTimeMillis();
    this.shoppingCart = shoppingCart;
    this.state = OrderState.VALIDATING_ITEMS;
  }

  public Long getId() {
    return id;
  }

  public ShoppingCart getShoppingCart() {
    return shoppingCart;
  }

  public OrderState getState() {
    return state;
  }
}
