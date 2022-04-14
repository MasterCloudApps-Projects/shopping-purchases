package es.codeurjc.mca.tfm.purchases.infrastructure.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.OrderUseCase;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartDeletionRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.mappers.InfraMapper;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaShoppingCartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Shopping cart events listener.
 */
@Service
@Slf4j
public class ShoppingCartEventsListener {

  private static final String CREATE_SHOPPING_CARTS_TOPIC = "create-shopping-carts";

  private static final String DELETE_SHOPPING_CARTS_TOPIC = "delete-shopping-carts";

  private static final String COMPLETE_SHOPPING_CARTS_TOPIC = "complete-shopping-carts";

  /**
   * Mapper.
   */
  private InfraMapper mapper;

  /**
   * Shopping cart repository.
   */
  private JpaShoppingCartRepository jpaShoppingCartRepository;

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
   * @param mapper                    mapper.
   * @param jpaShoppingCartRepository shopping cart repository.
   * @param orderUseCase              order use case.
   */
  public ShoppingCartEventsListener(InfraMapper mapper,
      JpaShoppingCartRepository jpaShoppingCartRepository,
      OrderUseCase orderUseCase) {
    this.mapper = mapper;
    this.jpaShoppingCartRepository = jpaShoppingCartRepository;
    this.orderUseCase = orderUseCase;
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Listener to process created shopping cart events and save them in database.
   *
   * @param shoppingCartCreationRequestedEvent with shopping cart to save info.
   */
  @KafkaListener(topics = CREATE_SHOPPING_CARTS_TOPIC)
  public void onCreatedShoppingCart(String shoppingCartCreationRequestedEvent) throws Exception {
    try {
      log.info("Received shoppingCartCreationRequestedEvent {}",
          shoppingCartCreationRequestedEvent);
      ShoppingCartEntity shoppingCartEntity = this.mapper.map(
          this.objectMapper.readValue(shoppingCartCreationRequestedEvent,
              ShoppingCartCreationRequestedEvent.class));
      this.jpaShoppingCartRepository.save(shoppingCartEntity);
      log.info("Shopping cart {} saved", shoppingCartEntity);
    } catch (Exception e) {
      log.error("Error processing event {}: {}", shoppingCartCreationRequestedEvent,
          e.getMessage());
      throw e;
    }
  }

  /**
   * Listener to process delete shopping cart events.
   *
   * @param shoppingCartDeletionRequestedEvent with shopping cart to delete id.
   */
  @KafkaListener(topics = DELETE_SHOPPING_CARTS_TOPIC)
  public void onDeletedShoppingCart(String shoppingCartDeletionRequestedEvent) throws Exception {
    try {
      log.info("Received shoppingCartDeletionRequestedEvent {}",
          shoppingCartDeletionRequestedEvent);
      Long id =
          this.objectMapper.readValue(shoppingCartDeletionRequestedEvent,
              ShoppingCartDeletionRequestedEvent.class).getId();
      this.jpaShoppingCartRepository.deleteById(id);
      log.info("Shopping cart with id {} deleted", id);
    } catch (Exception e) {
      log.error("Error processing event {}: {}", shoppingCartDeletionRequestedEvent,
          e.getMessage());
      throw e;
    }
  }

  /**
   * Listener to process completed shopping cart events and save them in database.
   *
   * @param shoppingCartCompletionRequestedEvent with completed shopping cart to save info.
   */
  @KafkaListener(topics = COMPLETE_SHOPPING_CARTS_TOPIC)
  public void onCompletedShoppingCart(String shoppingCartCompletionRequestedEvent)
      throws Exception {
    try {
      log.info("Received shoppingCartCompletionRequestedEvent {}",
          shoppingCartCompletionRequestedEvent);
      ShoppingCartEntity shoppingCartEntity = this.mapper.map(
          this.objectMapper.readValue(shoppingCartCompletionRequestedEvent,
              ShoppingCartCreationRequestedEvent.class));
      this.jpaShoppingCartRepository.save(shoppingCartEntity);
      log.info("Shopping cart {} saved", shoppingCartEntity);

      this.orderUseCase.create(this.mapper.map(shoppingCartEntity));
      log.info("Requested order creation for shopping cart {}", shoppingCartEntity);
    } catch (Exception e) {
      log.error("Error processing event {}: {}", shoppingCartCompletionRequestedEvent,
          e.getMessage());
      throw e;
    }
  }

}
