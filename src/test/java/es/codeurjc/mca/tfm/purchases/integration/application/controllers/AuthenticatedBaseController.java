package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import es.codeurjc.mca.tfm.purchases.config.KafkaTestConfiguration;
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
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.netty.http.client.HttpClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(KafkaTestConfiguration.class)
@ActiveProfiles("test")
@Tag("IntegrationTest")
public abstract class AuthenticatedBaseController extends TestContainersBase {

  protected static final long TOKEN_EXPIRATION_IN_MILIS = 300000;

  protected static final String SHOPPING_CART_BASE_URL = "/api/v1/shopping-carts";

  protected static final int USER_ID = 1;

  protected static final String LOCATION_HEADER = "Location";

  protected static final long KAFKA_TIMEOUT = 15000L;

  protected static final Long SHOPPING_CART_ID = 1L;

  protected static final String NOT_NUMERIC_ID = "Nan";

  @Value("${security.jwt.token.secret-key}")
  private String secretKey;

  protected WebTestClient webClient;

  @LocalServerPort
  protected int port;

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

  protected String generateValidToken() {
    return this.generateToken(String.valueOf(USER_ID), TOKEN_EXPIRATION_IN_MILIS);
  }

  protected String generateExpiredToken() {
    return this.generateToken(String.valueOf(USER_ID), 0L);
  }

  protected String generateTokenWithNotNumericUserId() {
    return this.generateToken("Nan", TOKEN_EXPIRATION_IN_MILIS);
  }

  protected String generateToken(String userId, Long validityInMilliseconds) {
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

}
