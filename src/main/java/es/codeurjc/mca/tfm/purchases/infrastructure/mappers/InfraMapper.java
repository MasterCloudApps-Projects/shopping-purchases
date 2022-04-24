package es.codeurjc.mca.tfm.purchases.infrastructure.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ItemDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.OrderEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderShoppingCart;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderUpdateRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderValidationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartCompletionRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartItem;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartItemsUpdateRequestedEvent;
import java.util.List;
import java.util.Optional;
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
   * Map an optional list of string to json array as string.
   *
   * @param stringList list of strings to map.
   * @return json array as string.
   * @throws JsonProcessingException if an error mapping list to json happens.
   */
  default String map(Optional<List<String>> stringList) throws JsonProcessingException {
    if (stringList == null || stringList.isEmpty()) {
      return null;
    }
    final ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(stringList.get());
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
   * Maps order update requested event to order entity.
   *
   * @param orderUpdateRequestedEvent created order event to map.
   * @return OrderEntity instance.
   */
  OrderEntity mapToOrderEntity(OrderUpdateRequestedEvent orderUpdateRequestedEvent);

  /**
   * Maps order DTO to order update requested event.
   *
   * @param orderDto order DTO to map.
   * @return OrderUpdateRequestedEvent instance.
   */
  OrderUpdateRequestedEvent mapToOrderUpdateRequestedEvent(OrderDto orderDto);

  /**
   * Maps event order shopping cart to a shopping cart entity.
   *
   * @param orderShoppingCart event order shopping cart.
   * @return mapped shopping cart entity.
   */
  ShoppingCartEntity mapToShoppingCartEntity(OrderShoppingCart orderShoppingCart);

  /**
   * Maps shopping cart DTO to shopping cart items update requested event.
   *
   * @param shoppingCartDto shopping cart DTO to map.
   * @return ShoppingCartItemsUpdateRequestedEvent instance.
   */
  ShoppingCartItemsUpdateRequestedEvent mapToShoppingCartItemsUpdateRequestedEvent(
      ShoppingCartDto shoppingCartDto);

  /**
   * Maps order entity to order DTO.
   *
   * @param orderEntity order entity to map.
   * @return mapped order DTO.
   */
  OrderDto mapToOrderDto(OrderEntity orderEntity);

  /**
   * Maps order DTO to order validation requested event.
   *
   * @param orderDto shopping cart DTO to map.
   * @return OrderValidationRequestedEvent instance.
   */
  OrderValidationRequestedEvent mapToOrderValidationRequestedEvent(OrderDto orderDto);

  /**
   * Map a string to a optional list of strings.
   *
   * @param errors string to map.
   * @return an optinal with a list of string.
   * @throws JsonProcessingException if an error happens.
   */
  default Optional<List<String>> mapToOptionalListOfString(String errors)
      throws JsonProcessingException {
    if (errors == null) {
      return Optional.empty();
    }
    final ObjectMapper objectMapper = new ObjectMapper();
    return Optional.ofNullable(List.of(objectMapper.readValue(errors, String[].class)));
  }

}
