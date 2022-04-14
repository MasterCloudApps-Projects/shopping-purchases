package es.codeurjc.mca.tfm.purchases.infrastructure.configs.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Kafka configuration class.
 */
@Configuration
@Profile("!test")
public class KafkaTopicConfig {

  private static final String CREATE_SHOPPING_CARTS_TOPIC = "create-shopping-carts";

  private static final String DELETE_SHOPPING_CARTS_TOPIC = "delete-shopping-carts";

  private static final String COMPLETE_SHOPPING_CARTS_TOPIC = "complete-shopping-carts";

  private static final String CREATE_ORDERS_TOPIC = "create-orders";

  private static final String VALIDATE_ITEMS_TOPIC = "validate-items";

  /**
   * Kafka bootstrap server address.
   */
  @Value(value = "${kafka.bootstrapAddress}")
  private String bootstrapAddress;

  /**
   * KafkaAdmin Spring bean, which will automatically add topics for all beans of type NewTopic.
   *
   * @return KafkaAdmin instance.
   */
  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress);
    return new KafkaAdmin(configs);
  }

  /**
   * Create shopping carts topic bean.
   *
   * @return NewTopic instance for create shopping carts topic.
   */
  @Bean
  public NewTopic createShoppingCartsTopic() {
    return new NewTopic(CREATE_SHOPPING_CARTS_TOPIC, 1, (short) 1);
  }

  /**
   * Delete shopping carts topic bean.
   *
   * @return NewTopic instance for delete shopping carts topic.
   */
  @Bean
  public NewTopic deleteShoppingCartsTopic() {
    return new NewTopic(DELETE_SHOPPING_CARTS_TOPIC, 1, (short) 1);
  }

  /**
   * Complete shopping carts topic bean.
   *
   * @return NewTopic instance for complete shopping carts topic.
   */
  @Bean
  public NewTopic completeShoppingCartsTopic() {
    return new NewTopic(COMPLETE_SHOPPING_CARTS_TOPIC, 1, (short) 1);
  }

  /**
   * Create orders topic bean.
   *
   * @return NewTopic instance for create orders topic.
   */
  @Bean
  public NewTopic createOrdersTopic() {
    return new NewTopic(CREATE_ORDERS_TOPIC, 1, (short) 1);
  }

  /**
   * Validate order items topic bean.
   *
   * @return NewTopic instance for validate order items topic.
   */
  @Bean
  public NewTopic validateOrderItemsTopic() {
    return new NewTopic(VALIDATE_ITEMS_TOPIC, 1, (short) 1);
  }

}
