package es.codeurjc.mca.tfm.purchases.infrastructure.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartCreationRequestedEvent;
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

  private static final String SHOPPING_CARTS_TOPIC = "shopping-carts";

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
  @KafkaListener(topics = SHOPPING_CARTS_TOPIC, containerFactory = "kafkaListenerContainerFactory")
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

}
