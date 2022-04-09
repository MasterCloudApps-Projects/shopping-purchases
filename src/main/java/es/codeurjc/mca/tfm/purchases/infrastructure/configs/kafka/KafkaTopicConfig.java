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

  private static final String SHOPPING_CARTS_TOPIC = "shopping-carts";

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
   * Shopping carts topic bean.
   *
   * @return NewTopic instance for shopping carts topic.
   */
  @Bean
  public NewTopic shoppingCartsTopic() {
    return new NewTopic(SHOPPING_CARTS_TOPIC, 1, (short) 1);
  }

}
