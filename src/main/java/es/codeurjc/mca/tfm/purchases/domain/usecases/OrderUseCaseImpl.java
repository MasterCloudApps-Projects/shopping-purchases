package es.codeurjc.mca.tfm.purchases.domain.usecases;

import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.mappers.DomainMapper;
import es.codeurjc.mca.tfm.purchases.domain.models.Order;
import es.codeurjc.mca.tfm.purchases.domain.models.ShoppingCart;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.OrderUseCase;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;

/**
 * Order use case implementation.
 */
public class OrderUseCaseImpl implements OrderUseCase {

  /**
   * Order repository.
   */
  private final OrderRepository orderRepository;

  /**
   * Constructor.
   *
   * @param orderRepository order repository.
   */
  public OrderUseCaseImpl(final OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  /**
   * Create an order.
   *
   * @param shoppingCartDto shopping cart associated with the order.
   * @return created order DTO.
   */
  @Override
  public OrderDto create(ShoppingCartDto shoppingCartDto) {
    ShoppingCart shoppingCart = DomainMapper.map(shoppingCartDto);
    Order order = new Order(shoppingCart);
    OrderDto orderDto = DomainMapper.map(order);
    this.orderRepository.create(orderDto);
    return orderDto;
  }

}
