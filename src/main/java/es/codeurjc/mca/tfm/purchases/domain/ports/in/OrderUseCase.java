package es.codeurjc.mca.tfm.purchases.domain.ports.in;

import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import java.util.List;
import java.util.Optional;

/**
 * Order use case interface.
 */
public interface OrderUseCase {

  /**
   * Create an order.
   *
   * @param shoppingCartDto shopping cart associated with the order.
   * @return created order DTO.
   */
  OrderDto create(ShoppingCartDto shoppingCartDto);

  /**
   * Update order state.
   *
   * @param id     order identifier.
   * @param state  state to update.
   * @param errors optional with errors if any.
   * @return updated order DTO.
   */
  Optional<OrderDto> update(Long id, String state, Optional<List<String>> errors);

}
