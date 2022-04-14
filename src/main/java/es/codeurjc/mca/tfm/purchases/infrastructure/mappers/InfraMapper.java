package es.codeurjc.mca.tfm.purchases.infrastructure.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ItemDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.OrderEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderCreatedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderShoppingCart;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartCompletionRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartItem;
import java.util.List;
import org.mapstruct.Mapper;

/**
 * Infrastructure mapper.
 */
@Mapper(componentModel = "spring")
public interface InfraMapper {

  /**
   * Maps shopping cart DTO to shopping cart creation requested event.
   *
   * @param shoppingCartDto shopping cart DTO to map.
   * @return CreatedShoppingCartEvent instance.
   */
  ShoppingCartCreationRequestedEvent mapToShoppingCartCreationRequestedEvent(
      ShoppingCartDto shoppingCartDto);

  /**
   * Maps shopping cart creation requested event to shopping cart entity.
   *
   * @param shoppingCartCreationRequestedEvent created shopping cart event to map.
   * @return ShoppingCartEntity instance.
   */
  ShoppingCartEntity map(ShoppingCartCreationRequestedEvent shoppingCartCreationRequestedEvent);

  /**
   * Map list of shopping cart items to json array as string.
   *
   * @param items list of items to map.
   * @return json array as string.
   * @throws JsonProcessingException if an error mapping list to json happens.
   */
  default String map(List<ShoppingCartItem> items) throws JsonProcessingException {
    if (items == null) {
      return "[]";
    }
    final ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(items);
  }

  /**
   * Maps shopping cart entity to shopping cart DTO.
   *
   * @param shoppingCartEntity entity to map.
   * @return ShoppingCartEntity instance.
   */
  ShoppingCartDto map(ShoppingCartEntity shoppingCartEntity);

  /**
   * Map a string to a list of items DTOs.
   *
   * @param items string to map.
   * @return a list of items DTOs.
   * @throws JsonProcessingException if an error happens.
   */
  default List<ItemDto> mapToItemDtoList(String items) throws JsonProcessingException {
    if (items == null) {
      return List.of();
    }
    final ObjectMapper objectMapper = new ObjectMapper();
    return List.of(objectMapper.readValue(items, ItemDto[].class));
  }

  /**
   * Map a string to a list of shopping cart items.
   *
   * @param items string to map.
   * @return a list of shopping cart items.
   * @throws JsonProcessingException if an error happens.
   */
  default List<ShoppingCartItem> mapToShoppingCartItemList(String items)
      throws JsonProcessingException {
    if (items == null) {
      return List.of();
    }
    final ObjectMapper objectMapper = new ObjectMapper();
    return List.of(objectMapper.readValue(items, ShoppingCartItem[].class));
  }

  /**
   * Maps shopping cart DTO to shopping cart completion requested event.
   *
   * @param shoppingCartDto shopping cart DTO to map.
   * @return ShoppingCartCompletionRequestedEvent instance.
   */
  ShoppingCartCompletionRequestedEvent mapToShoppingCartCompletionRequestedEvent(
      ShoppingCartDto shoppingCartDto);

  /**
   * Maps order DTO to order creation requested event.
   *
   * @param orderDto order DTO to map.
   * @return OrderCreationRequestedEvent instance.
   */
  OrderCreationRequestedEvent mapToOrderCreationRequestedEvent(OrderDto orderDto);

  /**
   * Maps order creation requested event to order entity.
   *
   * @param orderCreationRequestedEvent created order event to map.
   * @return OrderEntity instance.
   */
  OrderEntity mapToOrderEntity(OrderCreationRequestedEvent orderCreationRequestedEvent);

  /**
   * Maps order entity to order created event.
   *
   * @param orderEntity order entity to map.
   * @return OrderCreatedEvent instance.
   */
  OrderCreatedEvent mapToOrderCreatedEvent(OrderEntity orderEntity);

  /**
   * Maps event order shopping cart to a shopping cart entity.
   *
   * @param orderShoppingCart event order shopping cart.
   * @return mapped shopping cart entity.
   */
  ShoppingCartEntity mapToShoppingCartEntity(OrderShoppingCart orderShoppingCart);

}
