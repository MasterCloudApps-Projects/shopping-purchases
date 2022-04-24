package es.codeurjc.mca.tfm.purchases.infrastructure.configs;

import es.codeurjc.mca.tfm.purchases.domain.ports.in.OrderUseCase;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.ShoppingCartUseCase;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.ShoppingCartRepository;
import es.codeurjc.mca.tfm.purchases.domain.services.OrderStateService;
import es.codeurjc.mca.tfm.purchases.domain.services.impl.DoneOrderStateServiceImpl;
import es.codeurjc.mca.tfm.purchases.domain.services.impl.RejectedStateServiceImpl;
import es.codeurjc.mca.tfm.purchases.domain.services.impl.ValidatingBalanceOrderStateServiceImpl;
import es.codeurjc.mca.tfm.purchases.domain.services.impl.ValidatingItemsOrderStateServiceImpl;
import es.codeurjc.mca.tfm.purchases.domain.usecases.OrderUseCaseImpl;
import es.codeurjc.mca.tfm.purchases.domain.usecases.ShoppingCartUseCaseImpl;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Beans configuration class.
 */
@Configuration
public class Config {

  /**
   * Shopping cart use case bean.
   *
   * @param shoppingCartRepository shopping cart repository.
   * @return ShoppingCartUseCase instance.
   */
  @Bean
  public ShoppingCartUseCase shoppingCartUseCase(
      ShoppingCartRepository shoppingCartRepository) {
    return new ShoppingCartUseCaseImpl(shoppingCartRepository);
  }

  /**
   * ValidatingItemsOrderStateService bean.
   *
   * @param orderRepository order repository.
   */
  @Bean
  public OrderStateService validatingItemsOrderStateService(OrderRepository orderRepository) {
    return new ValidatingItemsOrderStateServiceImpl(orderRepository);
  }

  /**
   * ValidatingBalanceOrderStateService bean.
   *
   * @param orderRepository order repository.
   */
  @Bean
  public OrderStateService validatingBalanceOrderStateService(OrderRepository orderRepository) {
    return new ValidatingBalanceOrderStateServiceImpl(orderRepository);
  }

  /**
   * DoneOrderStateService bean.
   *
   * @param orderRepository order repository.
   */
  @Bean
  public OrderStateService doneOrderStateService(OrderRepository orderRepository) {
    return new DoneOrderStateServiceImpl(orderRepository);
  }

  /**
   * RejectedStateService bean.
   *
   * @param orderRepository order repository.
   */
  @Bean
  public OrderStateService rejectedStateService(OrderRepository orderRepository) {
    return new RejectedStateServiceImpl(orderRepository);
  }

  /**
   * Map with distinct order state service strategies.
   *
   * @param orderStateServices set of order state services.
   * @return order state service strategies map.
   */
  @Bean
  public Map<String, OrderStateService> orderStateServiceMap(
      Set<OrderStateService> orderStateServices) {
    return orderStateServices.stream()
        .collect(Collectors.toMap(
            OrderStateService::getState,
            Function.identity()
        ));
  }

  /**
   * Order use case bean.
   *
   * @param orderRepository    order repository.
   * @param orderStateServices set of order state services.
   * @return OrderUseCase instance.
   */
  @Bean
  public OrderUseCase orderUseCase(
      OrderRepository orderRepository,
      Set<OrderStateService> orderStateServices) {
    return new OrderUseCaseImpl(orderRepository, orderStateServiceMap(orderStateServices));
  }

}
