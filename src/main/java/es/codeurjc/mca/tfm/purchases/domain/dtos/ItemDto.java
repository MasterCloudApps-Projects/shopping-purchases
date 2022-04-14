package es.codeurjc.mca.tfm.purchases.domain.dtos;

import es.codeurjc.mca.tfm.purchases.domain.models.Item;

/**
 * Item domain DTO.
 */
public class ItemDto {

  /**
   * Product identifier.
   */
  private Integer productId;

  /**
   * Unit price.
   */
  private Double unitPrice;

  /**
   * Quantity.
   */
  private Integer quantity;

  /**
   * Total price.
   */
  private Double totalPrice;

  /**
   * Constructor.
   */
  public ItemDto() {
    super();
  }

  /**
   * Constructor.
   *
   * @param item domain entity with necessary info.
   */
  public ItemDto(Item item) {
    this.productId = item.getProductId();
    this.unitPrice = item.getUnitPrice();
    this.quantity = item.getQuantity();
    this.totalPrice = item.getTotalPrice();
  }

  public Integer getProductId() {
    return productId;
  }

  public void setProductId(Integer productId) {
    this.productId = productId;
  }

  public Double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(Double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Double totalPrice) {
    this.totalPrice = totalPrice;
  }
}
