package es.codeurjc.mca.tfm.purchases.domain.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
   * List of errors in case of exist any.
   */
  private Optional<List<String>> errors;

  /**
   * Constructor.
   */
  public Order(ShoppingCart shoppingCart) {
    super();
    this.id = System.currentTimeMillis();
    this.shoppingCart = shoppingCart;
    this.state = OrderState.CREATED;
    this.errors = Optional.empty();
  }

  /**
   * Constructor.
   *
   * @param id           identifier.
   * @param shoppingCart associated shopping cart.
   * @param state        order state.
   * @param errors       optional with list of errors if any.
   */
  public Order(Long id, ShoppingCart shoppingCart,
      OrderState state, Optional<List<String>> errors) {
    this.id = id;
    this.shoppingCart = shoppingCart;
    this.state = state;
    this.errors = errors;
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

  public Optional<List<String>> getErrors() {
    return errors;
  }

  /**
   * Indicates if order has final state.
   *
   * @return true if it has final state, else false.
   */
  public boolean hasFinalState() {
    return OrderState.DONE.equals(this.state) || OrderState.REJECTED.equals(this.state);
  }

  /**
   * Update order state.
   *
   * @param state state to update.
   * @return true if state was updated, else false.
   */
  public boolean updateState(OrderState state) {
    if (state.weight > this.state.weight) {
      this.state = state;
      return true;
    }
    return false;
  }

  /**
   * Set order as rejected, and add errors if any.
   *
   * @param errors an optional with errors to reject the order if any.
   * @return true if order could be rejected, else false.
   */
  public boolean rejectOrder(Optional<List<String>> errors) {
    if (!OrderState.DONE.equals(this.state)) {
      this.state = OrderState.REJECTED;
      if (errors.isPresent()) {
        List<String> modifiedErrors = this.errors.orElseGet(() -> new ArrayList<>());
        modifiedErrors.addAll(errors.get());
        this.errors = Optional.of(modifiedErrors);
      }
      return true;
    }
    return false;
  }

}
