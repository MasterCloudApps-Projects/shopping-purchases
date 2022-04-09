package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import es.codeurjc.mca.tfm.purchases.config.KafkaTestConfiguration;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaShoppingCartRepository;
import es.codeurjc.mca.tfm.purchases.testcontainers.TestContainersBase;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.netty.http.client.HttpClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(KafkaTestConfiguration.class)
@ActiveProfiles("test")
@Tag("IntegrationTest")
@DisplayName("ShoppingCartCommandController integration tests")
public class ShoppingCartCommandControllerTest extends TestContainersBase {

  private static final String SHOPPING_CART_BASE_URL = "/api/v1/shopping-carts";

  private static final String LOCATION_HEADER = "Location";

  private static final long TOKEN_EXPIRATION_IN_MILIS = 300000;

  private static final int USER_ID = 1;

  private static final long KAFKA_TIMEOUT = 10000L;

  private WebTestClient webClient;

  @LocalServerPort
  protected int port;

  @Value("${security.jwt.token.secret-key}")
  private String secretKey;

  @SpyBean
  private JpaShoppingCartRepository jpaShoppingCartRepository;

  @BeforeEach
  public void setup() throws SSLException {
    SslContext sslContext = SslContextBuilder
        .forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .build();

    HttpClient httpClient = HttpClient.create()
        .secure(sslSpec -> sslSpec.sslContext(sslContext))
        .baseUrl("https://localhost:" + this.port);

    ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

    this.webClient = WebTestClient
        .bindToServer(connector)
        .responseTimeout(Duration.ofMillis(60000))
        .build();
  }

  @Test
  @DisplayName("Test shopping cart creation successfully")
  @DirtiesContext
  public void givenShoppingCartCreationRequestWhenCreateThenShouldCreateAndReturnOkResponseAndId() {
    HttpHeaders headers = this.webClient
        .post()
        .uri(SHOPPING_CART_BASE_URL)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .exchange()
        .expectStatus()
        .isAccepted()
        .expectHeader()
        .value(LOCATION_HEADER,
            startsWith("https://localhost:" + this.port + SHOPPING_CART_BASE_URL + "/"))
        .returnResult(Map.class)
        .getResponseHeaders();

    String[] locationUrlParts = headers.get(LOCATION_HEADER).get(0).split("/");
    Long shoppingCartId = Long.valueOf(locationUrlParts[locationUrlParts.length - 1]);
    assertNotNull(shoppingCartId);

    verify(this.jpaShoppingCartRepository, timeout(KAFKA_TIMEOUT).times(1)).save(
        this.buildShoppingCart(shoppingCartId));

  }

  @Test
  @DisplayName("Test shopping cart creation without token")
  public void givenShoppingCartCreationRequestWithoutTokenWhenCreateThenShouldReturnUnauthorizedResponse() {
    this.webClient
        .post()
        .uri(SHOPPING_CART_BASE_URL)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("Test shopping cart creation with invalid token")
  public void givenShoppingCartCreationRequestWithInvalidTokenWhenCreateThenShouldReturnForbiddenResponse() {

    this.webClient
        .post()
        .uri(SHOPPING_CART_BASE_URL)
        .headers(http -> http.setBearerAuth(this.generateExpiredToken()))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DisplayName("Test shopping cart creation when already exist an incomplete shopping cart")
  @DirtiesContext
  public void givenShoppingCartCreationRequestWhenAlreadyExistAnIncompleteShoppingCartThenShouldReturnConflictResponse()
      throws InterruptedException {
    String token = this.generateValidToken();

    this.webClient
        .post()
        .uri(SHOPPING_CART_BASE_URL)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isAccepted();

    Thread.sleep(KAFKA_TIMEOUT);

    this.webClient
        .post()
        .uri(SHOPPING_CART_BASE_URL)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT);

  }

  @Test
  @DisplayName("Test shopping cart creation when there is an internal error")
  public void givenShoppingCartCreationRequestWhenInternalErrorHappensThenShouldReturnInternalServerErrorResponse() {

    this.webClient
        .post()
        .uri(SHOPPING_CART_BASE_URL)
        .headers(http -> http.setBearerAuth(this.generateTokenWithNotNumericUserId()))
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String generateValidToken() {
    return this.generateToken(String.valueOf(USER_ID), TOKEN_EXPIRATION_IN_MILIS);
  }

  private String generateExpiredToken() {
    return this.generateToken(String.valueOf(USER_ID), 0L);
  }

  private String generateTokenWithNotNumericUserId() {
    return this.generateToken("Nan", TOKEN_EXPIRATION_IN_MILIS);
  }

  private String generateToken(String userId, Long validityInMilliseconds) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("id", userId);
    claims.put("role", "USER_ROLE");
    return Jwts.builder()
        .setIssuedAt(new Date())
        .setClaims(claims)
        .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
        .signWith(SignatureAlgorithm.HS512,
            Base64.getEncoder().encodeToString(this.secretKey.getBytes())).compact();
  }

  private ShoppingCartEntity buildShoppingCart(Long id) {
    ShoppingCartEntity shoppingCartEntity = new ShoppingCartEntity();
    shoppingCartEntity.setId(id);
    shoppingCartEntity.setUserId(USER_ID);
    shoppingCartEntity.setItems("[]");
    shoppingCartEntity.setTotalPrice(0.0);

    return shoppingCartEntity;
  }

}
