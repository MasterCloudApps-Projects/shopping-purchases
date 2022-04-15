package es.codeurjc.mca.tfm.purchases.infrastructure.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.mappers.InfraMapper;
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
   * Kafka create order topic.
   */
  @Value("${kafka.topics.createOrder}")
  private String createOrderTopic;

  /**
   * Object mapper.
   */
  private ObjectMapper objectMapper;

  /**
   * Constructor.
   *
   * @param infraMapper   mapper.
   * @param kafkaTemplate kafka template.
   */
  public KafkaOrderRepositoryAdapter(InfraMapper infraMapper,
      KafkaTemplate<String, String> kafkaTemplate) {
    this.infraMapper = infraMapper;
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = new ObjectMapper();
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

}
