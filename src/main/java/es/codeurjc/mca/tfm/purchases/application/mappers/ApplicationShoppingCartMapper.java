package es.codeurjc.mca.tfm.purchases.application.mappers;

import es.codeurjc.mca.tfm.purchases.application.dtos.responses.ShoppingCartResponseDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import org.mapstruct.Mapper;

/**
 * Application layer shopping cart mapper.
 */
@Mapper(componentModel = "spring")
public interface ApplicationShoppingCartMapper {

  /**
   * Maps a shopping cart DTO to a shopping cart response DTO.
   *
   * @param shoppingCartDto shopping cart DTO to map.
   * @return mapped shopping cart response DTO.
   */
  ShoppingCartResponseDto map(ShoppingCartDto shoppingCartDto);

}
