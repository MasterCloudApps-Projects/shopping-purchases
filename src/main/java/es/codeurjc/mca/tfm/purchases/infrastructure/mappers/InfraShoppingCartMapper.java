package es.codeurjc.mca.tfm.purchases.infrastructure.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ItemDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartItem;
import java.util.List;
import org.mapstruct.Mapper;

/**
 * Infrastructure shopping cart mapper.
 */
@Mapper(componentModel = "spring")
public interface InfraShoppingCartMapper {

  /**
   * Maps shopping cart DTO to shopping cart creation requested event.
   *
   * @param shoppingCartDto shopping cart DTO to map.
   * @return CreatedShoppingCartEvent instance.
   */
  ShoppingCartCreationRequestedEvent map(ShoppingCartDto shoppingCartDto);

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
  default List<ItemDto> map(String items) throws JsonProcessingException {
    if (items == null) {
      return List.of();
    }
    final ObjectMapper objectMapper = new ObjectMapper();
    return List.of(objectMapper.readValue(items, ItemDto[].class));
  }

}
