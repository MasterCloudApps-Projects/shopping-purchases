package es.codeurjc.mca.tfm.purchases.infrastructure.configs;

import es.codeurjc.mca.tfm.purchases.domain.ports.in.OrderUseCase;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.ShoppingCartUseCase;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.ShoppingCartRepository;
import es.codeurjc.mca.tfm.purchases.domain.usecases.OrderUseCaseImpl;
import es.codeurjc.mca.tfm.purchases.domain.usecases.ShoppingCartUseCaseImpl;
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
   * Order use case bean.
   *
   * @param orderRepository order repository.
   * @return OrderUseCase instance.
   */
  @Bean
  public OrderUseCase orderUseCase(
      OrderRepository orderRepository) {
    return new OrderUseCaseImpl(orderRepository);
  }

}
