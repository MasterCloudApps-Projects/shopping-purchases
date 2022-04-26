package es.codeurjc.mca.tfm.purchases.infrastructure.configs.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Kafka configuration class.
 */
@Configuration
public class KafkaTopicConfig {

  /**
   * Kafka bootstrap server address.
   */
  @Value(value = "${kafka.bootstrapAddress}")
  private String bootstrapAddress;

  /**
   * Kafka create shopping cart topic.
   */
  @Value("${kafka.topics.createShoppingCart}")
  private String createShoppingCartTopic;

  /**
   * Kafka delete shopping cart topic.
   */
  @Value("${kafka.topics.deleteShoppingCart}")
  private String deleteShoppingCartTopic;

  /**
   * Kafka complete shopping cart topic.
   */
  @Value("${kafka.topics.completeShoppingCart}")
  private String completeShoppingCartTopic;

  /**
   * Kafka create order topic.
   */
  @Value("${kafka.topics.createOrder}")
  private String createOrderTopic;

  /**
   * Kafka validate items topic.
   */
  @Value("${kafka.topics.validateItems}")
  private String validateItemsTopic;

  /**
   * Kafka set item to shopping cart topic.
   */
  @Value("${kafka.topics.updateItems}")
  private String updateItemsTopic;

  /**
   * Kafka update order topic.
   */
  @Value("${kafka.topics.updateOrder}")
  private String updateOrderTopic;

  /**
   * Kafka restore items stock topic.
   */
  @Value("${kafka.topics.restoreStock}")
  private String restoreStockTopic;

  /**
   * Kafka validate user balance topic.
   */
  @Value("${kafka.topics.validateBalance}")
  private String validateBalanceTopic;

  /**
   * Kafka validate user balance topic.
   */
  @Value("${kafka.topics.changeState}")
  private String changeOrderStateTopic;


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
    return new NewTopic(this.createShoppingCartTopic, 1, (short) 1);
  }

  /**
   * Delete shopping carts topic bean.
   *
   * @return NewTopic instance for delete shopping carts topic.
   */
  @Bean
  public NewTopic deleteShoppingCartsTopic() {
    return new NewTopic(this.deleteShoppingCartTopic, 1, (short) 1);
  }

  /**
   * Complete shopping carts topic bean.
   *
   * @return NewTopic instance for complete shopping carts topic.
   */
  @Bean
  public NewTopic completeShoppingCartsTopic() {
    return new NewTopic(this.completeShoppingCartTopic, 1, (short) 1);
  }

  /**
   * Create orders topic bean.
   *
   * @return NewTopic instance for create orders topic.
   */
  @Bean
  public NewTopic createOrdersTopic() {
    return new NewTopic(this.createOrderTopic, 1, (short) 1);
  }

  /**
   * Validate order items topic bean.
   *
   * @return NewTopic instance for validate order items topic.
   */
  @Bean
  public NewTopic validateOrderItemsTopic() {
    return new NewTopic(this.validateItemsTopic, 1, (short) 1);
  }

  /**
   * Set item to shopping cart topic bean.
   *
   * @return NewTopic instance for set item to shopping cart topic.
   */
  @Bean
  public NewTopic setItemToShoppingCartTopic() {
    return new NewTopic(this.updateItemsTopic, 1, (short) 1);
  }

  /**
   * Update order topic bean.
   *
   * @return NewTopic instance for update order topic.
   */
  @Bean
  public NewTopic updateOrderTopic() {
    return new NewTopic(this.updateOrderTopic, 1, (short) 1);
  }

  /**
   * Restore stock topic bean.
   *
   * @return NewTopic instance for restore stock topic.
   */
  @Bean
  public NewTopic restoreStockTopic() {
    return new NewTopic(this.restoreStockTopic, 1, (short) 1);
  }

  /**
   * Validate user balance topic bean.
   *
   * @return NewTopic instance for validate balance topic.
   */
  @Bean
  public NewTopic validateBalanceTopic() {
    return new NewTopic(this.validateBalanceTopic, 1, (short) 1);
  }

  /**
   * Change order state topic bean.
   *
   * @return NewTopic instance for change order state topic.
   */
  @Bean
  public NewTopic changeOrderStateTopic() {
    return new NewTopic(this.changeOrderStateTopic, 1, (short) 1);
  }

}