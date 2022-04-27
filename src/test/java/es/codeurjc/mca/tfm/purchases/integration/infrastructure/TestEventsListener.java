package es.codeurjc.mca.tfm.purchases.integration.infrastructure;

import static es.codeurjc.mca.tfm.purchases.integration.infrastructure.OrderFlowsIntegrationTest.INVALID_BALANCE_MSG;
import static es.codeurjc.mca.tfm.purchases.integration.infrastructure.OrderFlowsIntegrationTest.INVALID_ITEMS_MSG;
import static es.codeurjc.mca.tfm.purchases.integration.infrastructure.OrderFlowsIntegrationTest.ORDER_INVALID_BALANCE_SC_ID;
import static es.codeurjc.mca.tfm.purchases.integration.infrastructure.OrderFlowsIntegrationTest.ORDER_INVALID_ITEMS_SC_ID;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderUpdateRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderValidationRequestedEvent;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
public class TestEventsListener {

  private KafkaTemplate<String, String> kafkaTemplate;

  private ObjectMapper objectMapper;

  public TestEventsListener(KafkaTemplate<String, String> kafkaTemplate,
      ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  /**
   * Kafka validate user balance topic.
   */
  @Value("${kafka.topics.changeState}")
  private String changeOrderStateTopic;

  @KafkaListener(topics = "${kafka.topics.validateItems}", groupId = "${kafka.groupId}")
  public void onValidateItems(String validateItemsEvent) throws Exception {
    try {
      log.info("Received {}", validateItemsEvent);
      OrderValidationRequestedEvent orderValidationRequestedEvent = this.objectMapper.readValue(
          validateItemsEvent, OrderValidationRequestedEvent.class);
      OrderUpdateRequestedEvent orderUpdateRequestedEvent = new OrderUpdateRequestedEvent();
      orderUpdateRequestedEvent.setId(orderValidationRequestedEvent.getId());
      orderUpdateRequestedEvent.setState(orderValidationRequestedEvent.getSuccessState());
      if (ORDER_INVALID_ITEMS_SC_ID.equals(
          orderValidationRequestedEvent.getShoppingCart().getId())) {
        orderUpdateRequestedEvent.setState(orderValidationRequestedEvent.getFailureState());
        orderUpdateRequestedEvent.setErrors(Optional.of(List.of(INVALID_ITEMS_MSG)));
      }
      this.kafkaTemplate.send(this.changeOrderStateTopic,
          this.objectMapper.writeValueAsString(orderUpdateRequestedEvent));
      log.info("Sent {}", this.objectMapper.writeValueAsString(orderUpdateRequestedEvent));
    } catch (Exception e) {
      log.error("Error processing event {}: {}", validateItemsEvent, e.getMessage());
      throw e;
    }
  }

  @KafkaListener(topics = "${kafka.topics.validateBalance}", groupId = "${kafka.groupId}")
  public void onValidateBalance(String validateBalanceEvent) throws Exception {
    try {
      log.info("Received {}", validateBalanceEvent);
      OrderValidationRequestedEvent orderValidationRequestedEvent = this.objectMapper.readValue(
          validateBalanceEvent, OrderValidationRequestedEvent.class);
      OrderUpdateRequestedEvent orderUpdateRequestedEvent = new OrderUpdateRequestedEvent();
      orderUpdateRequestedEvent.setId(orderValidationRequestedEvent.getId());
      orderUpdateRequestedEvent.setState(orderValidationRequestedEvent.getSuccessState());
      if (ORDER_INVALID_BALANCE_SC_ID.equals(
          orderValidationRequestedEvent.getShoppingCart().getId())) {
        orderUpdateRequestedEvent.setState(orderValidationRequestedEvent.getFailureState());
        orderUpdateRequestedEvent.setErrors(Optional.of(List.of(INVALID_BALANCE_MSG)));
      }
      this.kafkaTemplate.send(this.changeOrderStateTopic,
          this.objectMapper.writeValueAsString(orderUpdateRequestedEvent));
      log.info("Sent {}", this.objectMapper.writeValueAsString(orderUpdateRequestedEvent));
    } catch (Exception e) {
      log.error("Error processing event {}: {}", validateBalanceEvent, e.getMessage());
      throw e;
    }
  }

}
