package es.codeurjc.mca.tfm.purchases.infrastructure.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartDeletionRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.mappers.InfraShoppingCartMapper;
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

  /**
   * Shopping cart mapper.
   */
  private InfraShoppingCartMapper mapper;

  /**
   * Shopping cart repository.
   */
  private JpaShoppingCartRepository jpaShoppingCartRepository;

  public ShoppingCartEventsListener(InfraShoppingCartMapper mapper,
      JpaShoppingCartRepository jpaShoppingCartRepository) {
    this.mapper = mapper;
    this.jpaShoppingCartRepository = jpaShoppingCartRepository;
  }

  /**
   * Listener to process created shopping cart events and save them in database.
   *
   * @param shoppingCartCreationRequestedEvent with shopping cart to save info.
   */
  @KafkaListener(topics = CREATE_SHOPPING_CARTS_TOPIC)
  public void onCreatedShoppingCart(String shoppingCartCreationRequestedEvent) throws Exception {
    try {
      log.info("Received {}", shoppingCartCreationRequestedEvent);
      ObjectMapper objectMapper = new ObjectMapper();
      ShoppingCartEntity shoppingCartEntity = this.mapper.map(
          objectMapper.readValue(shoppingCartCreationRequestedEvent,
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
      ObjectMapper objectMapper = new ObjectMapper();
      Long id =
          objectMapper.readValue(shoppingCartDeletionRequestedEvent,
              ShoppingCartDeletionRequestedEvent.class).getId();
      this.jpaShoppingCartRepository.deleteById(id);
      log.info("Shopping cart with id {} deleted", id);
    } catch (Exception e) {
      log.error("Error processing event {}: {}", shoppingCartDeletionRequestedEvent,
          e.getMessage());
      throw e;
    }
  }

}
