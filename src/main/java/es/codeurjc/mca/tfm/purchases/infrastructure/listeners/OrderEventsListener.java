package es.codeurjc.mca.tfm.purchases.infrastructure.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.OrderEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderCreatedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.mappers.InfraMapper;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orders events listener.
 */
@Service
@Slf4j
public class OrderEventsListener {

  /**
   * Mapper.
   */
  private InfraMapper infraMapper;

  /**
   * Order repository.
   */
  private JpaOrderRepository jpaOrderRepository;

  /**
   * Kafka template.
   */
  private KafkaTemplate<String, String> kafkaTemplate;

  /**
   * Kafka validate items topic.
   */
  @Value("${kafka.topics.validateItems}")
  private String validateItemsTopic;

  /**
   * Object mapper.
   */
  private ObjectMapper objectMapper;

  /**
   * Constructor.
   *
   * @param infraMapper        mapper.
   * @param jpaOrderRepository order repository.
   * @param kafkaTemplate      kafka template.
   */
  public OrderEventsListener(InfraMapper infraMapper,
      JpaOrderRepository jpaOrderRepository,
      KafkaTemplate<String, String> kafkaTemplate) {
    this.infraMapper = infraMapper;
    this.jpaOrderRepository = jpaOrderRepository;
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Listener to process created order events and save them in database.
   *
   * @param orderCreationRequestedEvent with shopping cart to save info.
   */
  @Transactional
  @KafkaListener(topics = "${kafka.topics.createOrder}", groupId = "${kafka.groupId}")
  public void onCreatedShoppingCart(String orderCreationRequestedEvent) throws Exception {
    try {
      log.info("Received orderCreationRequestedEvent {}",
          orderCreationRequestedEvent);
      OrderEntity orderEntity = this.infraMapper.mapToOrderEntity(
          this.objectMapper.readValue(orderCreationRequestedEvent,
              OrderCreationRequestedEvent.class));
      this.jpaOrderRepository.save(orderEntity);
      log.info("Order {} saved", orderEntity);

      OrderCreatedEvent orderCreatedEvent =
          this.infraMapper.mapToOrderCreatedEvent(orderEntity);
      this.kafkaTemplate.send(this.validateItemsTopic,
          this.objectMapper.writeValueAsString(orderCreatedEvent));
      log.info("Sent order created event {}", orderCreatedEvent);
    } catch (Exception e) {
      log.error("Error processing event {}: {}", orderCreationRequestedEvent, e.getMessage());
      throw e;
    }
  }

}
