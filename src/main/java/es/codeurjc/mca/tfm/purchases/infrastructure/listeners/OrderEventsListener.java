package es.codeurjc.mca.tfm.purchases.infrastructure.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IllegalOrderStateException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.PreviousOrderStateUpdateException;
import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.OrderUseCase;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.OrderEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.OrderUpdateRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.mappers.InfraMapper;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaOrderRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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
   * Order use case.
   */
  private OrderUseCase orderUseCase;


  /**
   * Object mapper.
   */
  private ObjectMapper objectMapper;

  /**
   * Constructor.
   *
   * @param infraMapper        mapper.
   * @param jpaOrderRepository order repository.
   * @param orderUseCase       order use case.
   */
  public OrderEventsListener(InfraMapper infraMapper,
      JpaOrderRepository jpaOrderRepository,
      OrderUseCase orderUseCase) {
    this.infraMapper = infraMapper;
    this.jpaOrderRepository = jpaOrderRepository;
    this.orderUseCase = orderUseCase;
    this.objectMapper = new ObjectMapper();
    objectMapper.registerModule(new Jdk8Module());
  }

  /**
   * Listener to process created order events and save them in database.
   *
   * @param orderCreationRequestedEvent with order info to save.
   */
  @KafkaListener(topics = "${kafka.topics.createOrder}", groupId = "${kafka.groupId}")
  public void onCreatedOrder(String orderCreationRequestedEvent) throws Exception {
    try {
      log.info("Received orderCreationRequestedEvent {}", orderCreationRequestedEvent);
      OrderEntity orderEntity = this.infraMapper.mapToOrderEntity(
          this.objectMapper.readValue(orderCreationRequestedEvent,
              OrderCreationRequestedEvent.class));
      this.jpaOrderRepository.save(orderEntity);
      log.info("Order {} saved", orderEntity);

      this.orderUseCase.update(orderEntity.getId(), OrderState.VALIDATING_ITEMS.name(),
          Optional.empty());
    } catch (IllegalOrderStateException illegalOrderStateException) {
      log.error(illegalOrderStateException.getMessage());
    } catch (PreviousOrderStateUpdateException previousOrderStateUpdateException) {
      log.error(previousOrderStateUpdateException.getMessage());
    } catch (Exception e) {
      log.error("Error processing event {}: {}", orderCreationRequestedEvent, e.getMessage());
      throw e;
    }
  }

  /**
   * Listener to process updated order events and save them in database.
   *
   * @param orderUpdateRequestedEvent with order info to update.
   */
  @KafkaListener(topics = "${kafka.topics.updateOrder}", groupId = "${kafka.groupId}")
  public void onUpdatedOrder(String orderUpdateRequestedEvent) throws Exception {
    try {
      log.info("Received orderUpdateRequestedEvent {}", orderUpdateRequestedEvent);
      OrderUpdateRequestedEvent orderUpdateEvent = this.objectMapper.readValue(
          orderUpdateRequestedEvent, OrderUpdateRequestedEvent.class);
      String errors = this.infraMapper.map(orderUpdateEvent.getErrors());
      this.jpaOrderRepository.findById(orderUpdateEvent.getId()).ifPresentOrElse(
          orderEntity -> {
            orderEntity.setState(orderUpdateEvent.getState());
            if (errors != null) {
              orderEntity.setErrors(errors);
            }
            this.jpaOrderRepository.save(orderEntity);
            log.info("order {} updated", orderEntity);
          },
          () -> log.error("Not order found with id {}", orderUpdateEvent.getId()));
    } catch (Exception e) {
      log.error("Error processing event {}: {}", orderUpdateRequestedEvent, e.getMessage());
      throw e;
    }
  }

  /**
   * Listener to process state order changed events and save them in database.
   *
   * @param orderChangeRequestedEvent with order info to update.
   */
  @KafkaListener(topics = "${kafka.topics.changeState}", groupId = "${kafka.groupId}")
  public void onOrderStateChanged(String orderChangeRequestedEvent) throws Exception {
    try {
      log.info("Received orderChangeRequestedEvent {}", orderChangeRequestedEvent);
      OrderUpdateRequestedEvent orderUpdateEvent = this.objectMapper.readValue(
          orderChangeRequestedEvent, OrderUpdateRequestedEvent.class);
      this.orderUseCase.update(orderUpdateEvent.getId(), orderUpdateEvent.getState(),
          orderUpdateEvent.getErrors());
    } catch (IllegalOrderStateException illegalOrderStateException) {
      log.error(illegalOrderStateException.getMessage());
    } catch (PreviousOrderStateUpdateException previousOrderStateUpdateException) {
      log.error(previousOrderStateUpdateException.getMessage());
    } catch (Exception e) {
      log.error("Error processing event {}: {}", orderChangeRequestedEvent, e.getMessage());
      throw e;
    }
  }

}
