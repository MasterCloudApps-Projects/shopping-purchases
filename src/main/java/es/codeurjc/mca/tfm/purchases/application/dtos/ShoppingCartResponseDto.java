package es.codeurjc.mca.tfm.purchases.application.dtos;

import es.codeurjc.mca.tfm.purchases.domain.models.ShoppingCart;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Shopping cart response DTO.
 */
public class ShoppingCartResponseDto {

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
  private List<ItemResponseDto> items;

  /**
   * Total price.
   */
  private Double totalPrice;

  /**
   * Constructor.
   */
  public ShoppingCartResponseDto() {
    super();
  }

  /**
   * Constructor.
   *
   * @param shoppingCart domain entity with necessary info.
   */
  public ShoppingCartResponseDto(ShoppingCart shoppingCart) {
    this.id = shoppingCart.getId();
    this.userId = shoppingCart.getUserId();
    this.completed = shoppingCart.isCompleted();
    this.items = shoppingCart.getItems().stream()
        .map(item -> new ItemResponseDto(item))
        .collect(Collectors.toList());
    this.totalPrice = shoppingCart.getTotalPrice();
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

  public List<ItemResponseDto> getItems() {
    return items;
  }

  public void setItems(List<ItemResponseDto> items) {
    this.items = items;
  }

  public Double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Double totalPrice) {
    this.totalPrice = totalPrice;
  }
}
