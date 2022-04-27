package es.codeurjc.mca.tfm.purchases.domain.ports.out;

import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import java.util.Optional;

/**
 * Order repository interface.
 */
public interface OrderRepository {

  /**
   * Create an order.
   *
   * @param orderDto DTO with order info.
   */
  void create(OrderDto orderDto);

  /**
   * Find order by identifier.
   *
   * @param id order identifier.
   * @return an optional with found order, or empty if not found.
   */
  Optional<OrderDto> findById(Long id);

  /**
   * Update an order.
   *
   * @param orderDto DTO with order info to update.
   */
  void update(OrderDto orderDto);

  /**
   * Validates order items.
   *
   * @param orderDto order DTO.
   */
  void validateItems(OrderDto orderDto);

  /**
   * Validates user balance.
   *
   * @param orderDto order DTO.
   */
  void validateBalance(OrderDto orderDto);

  /**
   * Restores items stock.
   *
   * @param orderDto order DTO.
   */
  void restoreItemsStock(OrderDto orderDto);

  /**
   * Finish order.
   *
   * @param orderDto order DTO.
   */
  void finish(OrderDto orderDto);

}
