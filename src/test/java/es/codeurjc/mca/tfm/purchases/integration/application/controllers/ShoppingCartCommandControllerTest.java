package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import es.codeurjc.mca.tfm.purchases.application.dtos.ShoppingCartResponseDto;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaShoppingCartRepository;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("ShoppingCartCommandController integration tests")
public class ShoppingCartCommandControllerTest extends AuthenticatedBaseController {

  private static final String VALIDATE_ITEMS_TOPIC = "validate-items";

  @SpyBean
  private JpaShoppingCartRepository jpaShoppingCartRepository;

  @SpyBean
  private KafkaTemplate<String, String> kafkaTemplate;

  @Test
  @DisplayName("Test shopping cart creation successfully")
  @DirtiesContext
  public void givenShoppingCartCreationRequestWhenCreateThenShouldCreateAndReturnAcceptedResponseAndId() {
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
  public void givenShoppingCartCreationRequestWhenCreateAndAlreadyExistAnIncompleteShoppingCartThenShouldReturnConflictResponse()
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
  public void givenShoppingCartCreationRequestWhenCreateAndInternalErrorHappensThenShouldReturnInternalServerErrorResponse() {

    this.webClient
        .post()
        .uri(SHOPPING_CART_BASE_URL)
        .headers(http -> http.setBearerAuth(this.generateTokenWithNotNumericUserId()))
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  @DisplayName("Test shopping cart deletion successfully")
  @DirtiesContext
  public void givenShoppingCartIdWhenDeleteThenShouldReturnAcceptedResponseAndDeletedShoppingCartInfo()
      throws InterruptedException {
    String token = this.generateValidToken();

    HttpHeaders headers = this.webClient
        .post()
        .uri(SHOPPING_CART_BASE_URL)
        .headers(http -> http.setBearerAuth(token))
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

    Thread.sleep(KAFKA_TIMEOUT);

    this.webClient
        .get()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isOk();

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isAccepted();

    Thread.sleep(KAFKA_TIMEOUT);

    this.webClient
        .get()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isNotFound();

  }

  @Test
  @DisplayName("Test shopping cart deletion with invalid identifier")
  public void givenInvalidExistingShoppingCartIdWhenDeleteThenShouldReturnBadRequestResponse() {

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + NOT_NUMERIC_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .exchange()
        .expectStatus()
        .isBadRequest();
  }


  @Test
  @DisplayName("Test shopping cart deletion without token")
  public void givenShoppingCartIdWithoutTokenWhenDeleteThenShouldReturnUnauthorizedResponse() {
    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("Test shopping cart deletion with invalid token")
  public void givenShoppingCartIdWithInvalidTokenWhenDeleteThenShouldReturnForbiddenResponse() {

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .headers(http -> http.setBearerAuth(this.generateExpiredToken()))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DisplayName("Test shopping cart deletion with non existing identifier")
  public void givenNonExistingShoppingCartIdWhenDeleteThenShouldReturnNotFoundResponse() {

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @DisplayName("Test shopping cart deletion with not deletable shopping cart")
  @DirtiesContext
  public void givenCompletedShoppingCartIdWhenDeleteThenShouldReturnConflictResponse() {

    ShoppingCartEntity shoppingCartEntity = buildShoppingCart(System.currentTimeMillis());
    shoppingCartEntity.setCompleted(true);
    this.jpaShoppingCartRepository.save(shoppingCartEntity);

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartEntity.getId())
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  @DisplayName("Test shopping cart deletion when there is an internal error")
  public void givenShoppingCartIdWhenDeleteAndInternalErrorHappensThenShouldReturnInternalServerErrorResponse() {

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .headers(http -> http.setBearerAuth(this.generateTokenWithNotNumericUserId()))
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  @DisplayName("Test shopping cart completion successfully")
  @DirtiesContext
  public void givenShoppingCartIdWithTokenWhenCompleteThenShouldReturnAcceptedResponse()
      throws InterruptedException {
    ShoppingCartEntity shoppingCartEntity = buildShoppingCart(System.currentTimeMillis());
    shoppingCartEntity.setItems("[{\n"
        + "    \"productId\": 1,\n"
        + "    \"unitPrice\": 1,\n"
        + "    \"quantity\": 1,\n"
        + "    \"totalPrice\": 1\n"
        + "  }]");
    shoppingCartEntity.setTotalPrice(1.0);
    this.jpaShoppingCartRepository.save(shoppingCartEntity);

    String token = this.generateValidToken();

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartEntity.getId())
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isAccepted();

    Thread.sleep(KAFKA_TIMEOUT);

    ShoppingCartResponseDto shoppingCartResponseDto = this.webClient
        .get()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartEntity.getId())
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(ShoppingCartResponseDto.class)
        .returnResult()
        .getResponseBody();

    assertTrue(shoppingCartResponseDto.isCompleted());
    verify(this.kafkaTemplate, times(1)).send(eq(VALIDATE_ITEMS_TOPIC),
        any(String.class));

  }

  @Test
  @DisplayName("Test shopping cart completion with invalid identifier")
  public void givenInvalidExistingShoppingCartIdWhenCompleteThenShouldReturnBadRequestResponse() {

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + NOT_NUMERIC_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test shopping cart completion with invalid token")
  public void givenShoppingCartIdWithInvalidTokenWhenCompleteThenShouldReturnForbiddenResponse() {

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .headers(http -> http.setBearerAuth(this.generateExpiredToken()))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DisplayName("Test shopping cart completion with non existing identifier")
  public void givenNonExistingShoppingCartIdWhenCompleteThenShouldReturnNotFoundResponse() {

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @DisplayName("Test shopping cart completion with empty shopping cart")
  @DirtiesContext
  public void givenEmptyShoppingCartIdWhenCompleteThenShouldReturnConflictResponse()
      throws InterruptedException {

    String token = this.generateValidToken();

    HttpHeaders headers = this.webClient
        .post()
        .uri(SHOPPING_CART_BASE_URL)
        .headers(http -> http.setBearerAuth(token))
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

    Thread.sleep(KAFKA_TIMEOUT);

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  @DisplayName("Test shopping cart completion when there is an internal error")
  public void givenShoppingCartIdWhenCompleteAndInternalErrorHappensThenShouldReturnInternalServerErrorResponse() {

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .headers(http -> http.setBearerAuth(this.generateTokenWithNotNumericUserId()))
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
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
