package es.codeurjc.mca.tfm.purchases.infrastructure.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderRejectedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderUpdateRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderValidationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.mappers.InfraMapper;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaOrderRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Order repository adapter Kafka implementation.
 */
@Service
@Slf4j
public class KafkaOrderRepositoryAdapter implements OrderRepository {

  /**
   * Mapper.
   */
  private InfraMapper infraMapper;

  /**
   * Kafka template.
   */
  private KafkaTemplate<String, String> kafkaTemplate;

  /**
   * Order repository.
   */
  private JpaOrderRepository jpaOrderRepository;

  /**
   * Kafka create order topic.
   */
  @Value("${kafka.topics.createOrder}")
  private String createOrderTopic;

  /**
   * Kafka update order topic.
   */
  @Value("${kafka.topics.updateOrder}")
  private String updateOrderTopic;

  /**
   * Kafka validate items topic.
   */
  @Value("${kafka.topics.validateItems}")
  private String validateItemsTopic;

  /**
   * Kafka validate user balance topic.
   */
  @Value("${kafka.topics.validateBalance}")
  private String validateBalanceTopic;

  /**
   * Kafka restore items stock topic.
   */
  @Value("${kafka.topics.restoreStock}")
  private String restoreStockTopic;

  /**
   * Object mapper.
   */
  private ObjectMapper objectMapper;

  /**
   * Constructor.
   *
   * @param infraMapper        mapper.
   * @param kafkaTemplate      kafka template.
   * @param jpaOrderRepository jpa order repository.
   */
  public KafkaOrderRepositoryAdapter(InfraMapper infraMapper,
      KafkaTemplate<String, String> kafkaTemplate,
      JpaOrderRepository jpaOrderRepository) {
    this.infraMapper = infraMapper;
    this.kafkaTemplate = kafkaTemplate;
    this.jpaOrderRepository = jpaOrderRepository;
    this.objectMapper = new ObjectMapper();
    objectMapper.registerModule(new Jdk8Module());
  }

  /**
   * Create an order.
   *
   * @param orderDto DTO with order info.
   */
  @Override
  public void create(OrderDto orderDto) {
    try {
      OrderCreationRequestedEvent orderCreationRequestedEvent =
          this.infraMapper.mapToOrderCreationRequestedEvent(orderDto);
      this.kafkaTemplate.send(this.createOrderTopic,
          this.objectMapper.writeValueAsString(orderCreationRequestedEvent));
      log.info("Sent order creation requested event {}",
          orderCreationRequestedEvent);
    } catch (JsonProcessingException e) {
      log.error("Error sending order creation requested event");
      e.printStackTrace();
    }
  }

  /**
   * Find order by identifier.
   *
   * @param id order identifier.
   * @return an optional with found order, or empty if not found.
   */
  @Override
  public Optional<OrderDto> findById(Long id) {
    return this.jpaOrderRepository.findById(id).map(this.infraMapper::mapToOrderDto);
  }

  /**
   * Update an order.
   *
   * @param orderDto DTO with order info to update.
   */
  @Override
  public void update(OrderDto orderDto) {
    try {
      OrderUpdateRequestedEvent orderUpdateRequestedEvent =
          this.infraMapper.mapToOrderUpdateRequestedEvent(orderDto);
      this.kafkaTemplate.send(this.updateOrderTopic,
          this.objectMapper.writeValueAsString(orderUpdateRequestedEvent));
      log.info("Sent order update requested event {}", orderUpdateRequestedEvent);
    } catch (JsonProcessingException e) {
      log.error("Error sending order update requested event");
      e.printStackTrace();
    }
  }

  /**
   * Validates order items.
   *
   * @param orderDto order DTO.
   */
  @Override
  public void validateItems(OrderDto orderDto) {
    try {
      OrderValidationRequestedEvent orderValidationRequestedEvent =
          this.infraMapper.mapToOrderValidationRequestedEvent(orderDto);
      orderValidationRequestedEvent.setFailureState(OrderState.REJECTED.name());
      orderValidationRequestedEvent.setSuccessState(OrderState.VALIDATING_BALANCE.name());
      this.kafkaTemplate.send(this.validateItemsTopic,
          this.objectMapper.writeValueAsString(orderValidationRequestedEvent));
      log.info("Sent items validation requested event {}", orderValidationRequestedEvent);
    } catch (JsonProcessingException e) {
      log.error("Error sending items validation requested event");
      e.printStackTrace();
    }
  }

  /**
   * Validates user balance.
   *
   * @param orderDto order DTO.
   */
  @Override
  public void validateBalance(OrderDto orderDto) {
    try {
      OrderValidationRequestedEvent orderValidationRequestedEvent =
          this.infraMapper.mapToOrderValidationRequestedEvent(orderDto);
      orderValidationRequestedEvent.setFailureState(OrderState.REJECTED.name());
      orderValidationRequestedEvent.setSuccessState(OrderState.DONE.name());
      this.kafkaTemplate.send(this.validateBalanceTopic,
          this.objectMapper.writeValueAsString(orderValidationRequestedEvent));
      log.info("Sent user balance validation requested event {}", orderValidationRequestedEvent);
    } catch (JsonProcessingException e) {
      log.error("Error sending user balance validation requested event");
      e.printStackTrace();
    }
  }

  /**
   * Restores items stock.
   *
   * @param orderDto order DTO.
   */
  @Override
  public void restoreItemsStock(OrderDto orderDto) {
    try {
      OrderRejectedEvent orderRejectedEvent =
          this.infraMapper.mapToOrderRejectedEvent(orderDto);
      this.kafkaTemplate.send(this.restoreStockTopic,
          this.objectMapper.writeValueAsString(orderRejectedEvent));
      log.info("Sent items restore stock requested event {}", orderRejectedEvent);
    } catch (JsonProcessingException e) {
      log.error("Error sending items restore stock requested event");
      e.printStackTrace();
    }
  }

  /**
   * Finish order.
   *
   * @param orderDto order DTO.
   */
  @Override
  public void finish(OrderDto orderDto) {
    // TODO: send mail
  }

}
