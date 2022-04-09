package es.codeurjc.mca.tfm.purchases.infrastructure.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.ShoppingCartRepository;
import es.codeurjc.mca.tfm.purchases.infrastructure.events.ShoppingCartCreationRequestedEvent;
import es.codeurjc.mca.tfm.purchases.infrastructure.mappers.InfraShoppingCartMapper;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaShoppingCartRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Shopping cart repository adapter Kafka implementation.
 */
@Service
@Slf4j
public class KafkaShoppingCartRepositoryAdapter implements ShoppingCartRepository {

  private static final String SHOPPING_CARTS_TOPIC = "shopping-carts";

  /**
   * Shopping cart mapper.
   */
  private InfraShoppingCartMapper shoppingCartMapper;

  /**
   * Kafka template.
   */
  private KafkaTemplate<String, String> kafkaTemplate;

  /**
   * Shopping cart repository.
   */
  private JpaShoppingCartRepository jpaShoppingCartRepository;

  /**
   * Constructor.
   *
   * @param kafkaTemplate kafka template.
   */
  public KafkaShoppingCartRepositoryAdapter(InfraShoppingCartMapper shoppingCartMapper,
      KafkaTemplate<String, String> kafkaTemplate,
      JpaShoppingCartRepository jpaShoppingCartRepository) {
    this.shoppingCartMapper = shoppingCartMapper;
    this.kafkaTemplate = kafkaTemplate;
    this.jpaShoppingCartRepository = jpaShoppingCartRepository;
  }

  /**
   * Send a created shopping cart event to save it in database.
   *
   * @param shoppingCartDto DTO with shopping cart info.
   */
  @Override
  public void create(ShoppingCartDto shoppingCartDto) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      ShoppingCartCreationRequestedEvent shoppingCartCreationRequestedEvent = this.shoppingCartMapper.map(
          shoppingCartDto);
      this.kafkaTemplate.send(SHOPPING_CARTS_TOPIC,
          objectMapper.writeValueAsString(shoppingCartCreationRequestedEvent));
      log.info("Sent shopping cart creation requested event {}",
          shoppingCartCreationRequestedEvent);
    } catch (JsonProcessingException e) {
      log.error("Error sending shopping cart creation requested event");
      e.printStackTrace();
    }
  }

  /**
   * Get the current only incomplete shopping cart for passed user.
   *
   * @param userId user identifier.
   * @return optional of incomplete shopping cart for user if exists, else empty.
   */
  @Override
  public Optional<ShoppingCartDto> getIncompleteByUser(Integer userId) {
    return this.jpaShoppingCartRepository.findByUserIdAndCompletedIsFalse(userId)
        .map(this.shoppingCartMapper::map);
  }

}
