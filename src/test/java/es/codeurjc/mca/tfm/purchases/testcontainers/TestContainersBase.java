package es.codeurjc.mca.tfm.purchases.testcontainers;

import java.io.File;
import java.time.Duration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class TestContainersBase {

  protected static final String MYSQL_SERVICE_NAME = "mysql_1";

  protected static final int MYSQL_PORT = 3306;

  protected static final String ZOOKEEPER_SERVICE_NAME = "zookeeper_1";

  protected static final int ZOOKEEPER_PORT = 2181;

  protected static final String KAFKA_SERVICE_NAME = "kafka_1";

  protected static final int KAFKA_PORT = 9092;

  protected static final DockerComposeContainer ENVIRONMENT;

  static {
    ENVIRONMENT =
        new DockerComposeContainer(new File("src/test/resources/docker-compose-test.yml"))
            .withExposedService(MYSQL_SERVICE_NAME, MYSQL_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
            .withExposedService(ZOOKEEPER_SERVICE_NAME, ZOOKEEPER_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
            .withExposedService(KAFKA_SERVICE_NAME, KAFKA_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));
    ENVIRONMENT.start();
  }

}
