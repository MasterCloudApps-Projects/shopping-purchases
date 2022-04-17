package es.codeurjc.mca.tfm.purchases.domain.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shopping cart domain entity.
 */
public class ShoppingCart {

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
  private List<Item> items;

  /**
   * Total price.
   */
  private Double totalPrice;

  /**
   * Constructor.
   *
   * @param userId user identifier.
   */
  public ShoppingCart(Integer userId) {
    this.id = System.currentTimeMillis();
    this.userId = userId;
    this.items = new ArrayList<>();
    this.totalPrice = 0d;
  }

  /**
   * All args constructor.
   *
   * @param id         identifier.
   * @param userId     user identifier.
   * @param completed  indicates if shopping cart is completed.
   * @param items      list of items.
   * @param totalPrice total price.
   */
  public ShoppingCart(Long id, Integer userId, boolean completed,
      List<Item> items, Double totalPrice) {
    this.id = id;
    this.userId = userId;
    this.completed = completed;
    this.items = items;
    this.totalPrice = totalPrice;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }

  public Double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Double totalPrice) {
    this.totalPrice = totalPrice;
  }

  /**
   * Indicates if shopping cart is deletable.
   *
   * @return true if is deletable, else false.
   */
  public boolean isDeletable() {
    return !this.completed;
  }

  /**
   * Indicates if shopping cart is completable.
   *
   * @return true if is completable, else false.
   */
  public boolean isCompletable() {
    return !this.completed && this.items != null && this.items.size() > 0 && this.totalPrice > 0;
  }

  /**
   * Completes shopping cart setting completed attribute to true.
   */
  public void complete() {
    this.completed = true;
  }

  /**
   * Set item into shopping cart.
   *
   * @param productId product identifier.
   * @param unitPrice item unit price.
   * @param quantity  item quantity.
   * @return true if item can be set, else false.
   */
  public boolean setItem(Integer productId, Double unitPrice, Integer quantity) {
    if (unitPrice > 0 && quantity > 0) {
      Optional<Item> itemOptional = this.items.stream()
          .filter(item -> productId.equals(item.getProductId()))
          .findAny();
      itemOptional.ifPresentOrElse(
          item -> item.update(unitPrice, quantity),
          () -> this.items.add(new Item(productId, unitPrice, quantity))
      );
      this.totalPrice = this.items.stream().map(Item::getTotalPrice).reduce(0.0, Double::sum);
      return true;
    }
    return false;
  }

  /**
   * Delete item from shopping cart.
   *
   * @param productId product identifier.
   * @return true if item was deleted, else false.
   */
  public boolean deleteItem(Integer productId) {
    if (this.items.removeIf(item -> productId.equals(item.getProductId()))) {
      this.totalPrice = this.items.stream().map(Item::getTotalPrice).reduce(0.0, Double::sum);
      return true;
    }
    return false;
  }

}
