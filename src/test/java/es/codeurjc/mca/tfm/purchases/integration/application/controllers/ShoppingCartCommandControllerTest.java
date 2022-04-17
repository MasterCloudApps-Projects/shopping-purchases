package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import es.codeurjc.mca.tfm.purchases.application.dtos.requests.SetItemRequest;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaShoppingCartRepository;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class ShoppingCartCommandControllerTest extends AuthenticatedBaseController {

  protected static final Integer PRODUCT_ID = 1;

  protected static final Double PRODUCT_UNIT_PRICE = 3.3;

  protected static final Integer PRODUCT_QUANTITY = 2;

  protected static final Double PRODUCT_TOTAL_PRICE = 6.6;

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

  protected static SetItemRequest buildSetItemRequest() {
    return SetItemRequest.builder()
        .unitPrice(PRODUCT_UNIT_PRICE)
        .quantity(PRODUCT_QUANTITY)
        .build();
  }

  protected static SetItemRequest buildInvalidSetItemRequest() {
    return SetItemRequest.builder()
        .unitPrice(PRODUCT_UNIT_PRICE)
        .quantity(0)
        .build();
  }

}
