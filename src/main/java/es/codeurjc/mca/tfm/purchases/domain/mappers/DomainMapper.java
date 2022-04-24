package es.codeurjc.mca.tfm.purchases.domain.mappers;

import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.models.Item;
import es.codeurjc.mca.tfm.purchases.domain.models.Order;
import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;
import es.codeurjc.mca.tfm.purchases.domain.models.ShoppingCart;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Domain layer mapper.
 */
public class DomainMapper {

  /**
   * Maps shopping cart domain entity to DTO.
   *
   * @param shoppingCart domain entity to map.
   * @return mapped DTO.
   */
  public static ShoppingCartDto map(ShoppingCart shoppingCart) {
    return new ShoppingCartDto(shoppingCart);
  }

  /**
   * Maps shopping cart DTO to domain entity.
   *
   * @param shoppingCartDto DTO to map.
   * @return mapped domain entity.
   */
  public static ShoppingCart map(ShoppingCartDto shoppingCartDto) {
    List<Item> items = shoppingCartDto.getItems().stream()
        .map(itemDto -> new Item(itemDto.getProductId(), itemDto.getUnitPrice(),
            itemDto.getQuantity()))
        .collect(Collectors.toList());
    return new ShoppingCart(shoppingCartDto.getId(), shoppingCartDto.getUserId(),
        shoppingCartDto.isCompleted(), items, shoppingCartDto.getTotalPrice());
  }

  /**
   * Maps order domain entity to DTO.
   *
   * @param order domain entity to map.
   * @return mapped DTO.
   */
  public static OrderDto map(Order order) {
    ShoppingCartDto shoppingCartDto = map(order.getShoppingCart());
    return new OrderDto(order.getId(), shoppingCartDto, order.getState().name(), order.getErrors());
  }

  /**
   * Maps order DTO to domain entity.
   *
   * @param orderDto DTO to map.
   * @return mapped order domain.
   */
  public static Order map(OrderDto orderDto) {
    return new Order(orderDto.getId(), map(orderDto.getShoppingCart()),
        OrderState.valueOf(orderDto.getState()), orderDto.getErrors());
  }

}
