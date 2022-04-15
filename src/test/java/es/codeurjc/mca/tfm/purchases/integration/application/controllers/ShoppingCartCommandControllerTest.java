package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaShoppingCartRepository;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class ShoppingCartCommandControllerTest extends AuthenticatedBaseController {

  @SpyBean
  protected JpaShoppingCartRepository jpaShoppingCartRepository;

  @SpyBean
  protected KafkaTemplate<String, String> kafkaTemplate;

  protected static ShoppingCartEntity buildShoppingCart(Long id) {
    ShoppingCartEntity shoppingCartEntity = new ShoppingCartEntity();
    shoppingCartEntity.setId(id);
    shoppingCartEntity.setUserId(USER_ID);
    shoppingCartEntity.setItems("[]");
    shoppingCartEntity.setTotalPrice(0.0);

    return shoppingCartEntity;
  }

}
