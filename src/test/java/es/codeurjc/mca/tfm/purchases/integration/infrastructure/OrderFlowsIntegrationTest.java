package es.codeurjc.mca.tfm.purchases.integration.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.purchases.PurchasesApplication;
import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.OrderUseCase;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.OrderEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.mappers.InfraMapper;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaOrderRepository;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaShoppingCartRepository;
import es.codeurjc.mca.tfm.purchases.testcontainers.TestContainersBase;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PurchasesApplication.class)
@Import(OrderFlowsIntegrationTest.OrderFlowsIntegrationTestContextConfiguration.class)
@ActiveProfiles("test")
@Tag("IntegrationTest")
@DisplayName("Order flow integration tests")
@DirtiesContext
public class OrderFlowsIntegrationTest extends TestContainersBase {

  private static final long WAIT_TIME = 5000L;

  static final Long ORDER_DONE_SC_ID = 1L;

  static final Long ORDER_INVALID_ITEMS_SC_ID = 2L;

  static final String INVALID_ITEMS_MSG = "Required 1 units of product 1, but only 0 available";

  static final String INVALID_ITEMS_ERRORS = "[\"" + INVALID_ITEMS_MSG + "\"]";

  static final Long ORDER_INVALID_BALANCE_SC_ID = 3L;

  static final String INVALID_BALANCE_MSG = "User 1 hasn't enough balance";

  static final String INVALID_BALANCE_ERRORS = "[\"" + INVALID_BALANCE_MSG + "\"]";

  @TestConfiguration
  static class OrderFlowsIntegrationTestContextConfiguration {

    @Bean
    public TestEventsListener testEventsListener(KafkaTemplate<String, String> kafkaTemplate,
        ObjectMapper objectMapper) {
      return new TestEventsListener(kafkaTemplate, objectMapper);
    }

  }

  @Autowired
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  @Autowired
  private JpaShoppingCartRepository jpaShoppingCartRepository;

  @Autowired
  private InfraMapper infraMapper;

  @Autowired
  private OrderUseCase orderUseCase;

  @SpyBean
  private JpaOrderRepository jpaOrderRepository;

  @Captor
  private ArgumentCaptor<OrderEntity> orderEntityArgumentCaptor;

  @SpyBean
  private OrderRepository orderRepository;

  @Captor
  private ArgumentCaptor<OrderDto> orderDtoArgumentCaptor;

  @BeforeEach
  public void setup() {
    for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
        .getListenerContainers()) {
      if (messageListenerContainer.getAssignedPartitions().isEmpty()) {
        ContainerTestUtils.waitForAssignment(messageListenerContainer, 1);
      }
    }
  }

  @Test
  @DisplayName("Test create order when all validations are success")
  @DirtiesContext
  public void givenAnOrderCreationWhenValidationsAreSuccessThenShouldSetOrderDone() {

    ShoppingCartEntity completedShoppingCartEntity = buildCompletedShoppingCart(ORDER_DONE_SC_ID);
    jpaShoppingCartRepository.save(completedShoppingCartEntity);

    this.orderUseCase.create(this.infraMapper.map(completedShoppingCartEntity));

    verify(this.jpaOrderRepository, timeout(WAIT_TIME).times(4)).save(
        this.orderEntityArgumentCaptor.capture());
    OrderEntity orderEntity = this.orderEntityArgumentCaptor.getAllValues()
        .get(this.orderEntityArgumentCaptor.getAllValues().size() - 1);
    assertNotNull(orderEntity.getId());
    assertEquals(OrderState.DONE.name(), orderEntity.getState());
    assertNull(orderEntity.getErrors());

    verify(this.orderRepository, timeout(WAIT_TIME).times(1)).finish(
        orderDtoArgumentCaptor.capture());

  }

  @Test
  @DisplayName("Test create order when items validation fails")
  @DirtiesContext
  public void givenAnOrderCreationWhenItemsAreInvalidThenShouldRejectOrder() {

    ShoppingCartEntity completedShoppingCartEntity = buildCompletedShoppingCart(
        ORDER_INVALID_ITEMS_SC_ID);
    jpaShoppingCartRepository.save(completedShoppingCartEntity);

    this.orderUseCase.create(this.infraMapper.map(completedShoppingCartEntity));

    verify(this.jpaOrderRepository, timeout(WAIT_TIME).times(3)).save(
        this.orderEntityArgumentCaptor.capture());
    OrderEntity orderEntity = this.orderEntityArgumentCaptor.getAllValues()
        .get(this.orderEntityArgumentCaptor.getAllValues().size() - 1);
    assertNotNull(orderEntity.getId());
    assertEquals(OrderState.REJECTED.name(), orderEntity.getState());
    assertEquals(INVALID_ITEMS_ERRORS, orderEntity.getErrors());

  }

  @Test
  @DisplayName("Test create order when user balance validation fails")
  @DirtiesContext
  public void givenAnOrderCreationWhenUserBalanceIsInvalidThenShouldRejectOrder()
      throws JsonProcessingException {

    ShoppingCartEntity completedShoppingCartEntity = buildCompletedShoppingCart(
        ORDER_INVALID_BALANCE_SC_ID);
    jpaShoppingCartRepository.save(completedShoppingCartEntity);

    this.orderUseCase.create(this.infraMapper.map(completedShoppingCartEntity));

    verify(this.jpaOrderRepository, timeout(WAIT_TIME).times(4)).save(
        this.orderEntityArgumentCaptor.capture());
    OrderEntity orderEntity = this.orderEntityArgumentCaptor.getAllValues()
        .get(this.orderEntityArgumentCaptor.getAllValues().size() - 1);
    assertNotNull(orderEntity.getId());
    assertEquals(OrderState.REJECTED.name(), orderEntity.getState());
    assertEquals(INVALID_BALANCE_ERRORS, orderEntity.getErrors());

//    verify(this.orderRepository, timeout(WAIT_TIME).times(1)).restoreItemsStock(
//        orderDtoArgumentCaptor.capture());
//    verify(this.orderRepository, timeout(WAIT_TIME).times(1)).finish(
//        orderDtoArgumentCaptor.capture());
//    OrderDto orderDto = orderDtoArgumentCaptor.getAllValues().get(0);
//    assertEquals(orderEntity.getId(), orderDto.getId());
//    assertEquals(orderEntity.getState(), orderDto.getState());
//    assertEquals(orderEntity.getShoppingCart().getId(), orderDto.getShoppingCart().getId());
//    assertEquals(orderEntity.getShoppingCart().getUserId(), orderDto.getShoppingCart().getUserId());
//    assertEquals(orderEntity.getShoppingCart().isCompleted(),
//        orderDto.getShoppingCart().isCompleted());
//    assertIterableEquals(
//        this.infraMapper.mapToItemDtoList(orderEntity.getShoppingCart().getItems()),
//        orderDto.getShoppingCart().getItems());
//    assertEquals(orderEntity.getShoppingCart().getTotalPrice(),
//        orderDto.getShoppingCart().getTotalPrice());
//    assertEquals(List.of(INVALID_BALANCE_MSG), orderDto.getErrors().get());
  }

  private ShoppingCartEntity buildCompletedShoppingCart(Long shoppingCartId) {
    ShoppingCartEntity shoppingCartEntity = new ShoppingCartEntity();
    shoppingCartEntity.setId(shoppingCartId);
    shoppingCartEntity.setUserId(1);
    shoppingCartEntity.setCompleted(true);
    shoppingCartEntity.setItems("[{"
        + "\"productId\": 1, "
        + "\"unitPrice\": 1.0, "
        + "\"quantity\": 1, "
        + "\"totalPrice\": 1.0"
        + "}]");
    shoppingCartEntity.setTotalPrice(1.0);

    return shoppingCartEntity;
  }

}
