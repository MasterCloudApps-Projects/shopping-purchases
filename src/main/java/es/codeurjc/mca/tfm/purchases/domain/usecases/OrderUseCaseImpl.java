package es.codeurjc.mca.tfm.purchases.domain.usecases;

import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IllegalOrderStateException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.PreviousOrderStateUpdateException;
import es.codeurjc.mca.tfm.purchases.domain.mappers.DomainMapper;
import es.codeurjc.mca.tfm.purchases.domain.models.Order;
import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;
import es.codeurjc.mca.tfm.purchases.domain.models.ShoppingCart;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.OrderUseCase;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;
import es.codeurjc.mca.tfm.purchases.domain.services.OrderStateService;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Order use case implementation.
 */
public class OrderUseCaseImpl implements OrderUseCase {

  /**
   * Order repository.
   */
  private final OrderRepository orderRepository;

  /**
   * Map with distinct order state service strategies.
   */
  private final Map<String, OrderStateService> orderStateServiceMap;

  /**
   * Constructor.
   *
   * @param orderRepository      order repository.
   * @param orderStateServiceMap order state services map.
   */
  public OrderUseCaseImpl(final OrderRepository orderRepository,
      Map<String, OrderStateService> orderStateServiceMap) {
    this.orderRepository = orderRepository;
    this.orderStateServiceMap = orderStateServiceMap;
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

  /**
   * Update order state.
   *
   * @param id     order identifier.
   * @param state  state to update.
   * @param errors optional with errors if any.
   * @return updated order DTO.
   */
  @Override
  public Optional<OrderDto> update(Long id, String state, Optional<List<String>> errors) {
    Optional<OrderDto> orderDtoOptional = this.orderRepository.findById(id);
    if (orderDtoOptional.isPresent()) {
      Order order = DomainMapper.map(orderDtoOptional.get());
      if (order.hasFinalState()) {
        throw new IllegalOrderStateException("Can't change state of order in final state");
      }
      final OrderState previousState = order.getState();
      if (OrderState.REJECTED.name().equals(state)) {
        order.rejectOrder(errors);
      } else if (!order.updateState(OrderState.valueOf(state))) {
        throw new PreviousOrderStateUpdateException(
            "Can't change state of order by other previous");
      }
      OrderDto orderDto = DomainMapper.map(order);
      this.orderRepository.update(orderDto);
      this.orderStateServiceMap.get(order.getState().name())
          .performAction(previousState, order.getState(), orderDto);
      orderDtoOptional = Optional.of(orderDto);
    }
    return orderDtoOptional;
  }

}
