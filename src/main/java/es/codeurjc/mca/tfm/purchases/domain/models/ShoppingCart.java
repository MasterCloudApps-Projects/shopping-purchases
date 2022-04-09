package es.codeurjc.mca.tfm.purchases.domain.models;

import java.util.ArrayList;
import java.util.List;

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
}
