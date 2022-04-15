package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import es.codeurjc.mca.tfm.purchases.application.dtos.requests.SetItemRequest;
import es.codeurjc.mca.tfm.purchases.application.dtos.responses.ItemResponseDto;
import es.codeurjc.mca.tfm.purchases.application.dtos.responses.ShoppingCartResponseDto;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import es.codeurjc.mca.tfm.purchases.infrastructure.repositories.JpaShoppingCartRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("ShoppingCartCommandController integration tests")
public class ShoppingCartCommandControllerTest extends AuthenticatedBaseController {

  private static final Integer PRODUCT_ID = 1;

  private static final Double PRODUCT_UNIT_PRICE = 3.3;

  private static final Integer PRODUCT_QUANTITY = 2;

  private static final Double PRODUCT_TOTAL_PRICE = 6.6;

  @SpyBean
  private JpaShoppingCartRepository jpaShoppingCartRepository;

  @SpyBean
  private KafkaTemplate<String, String> kafkaTemplate;

  @Value("${kafka.topics.validateItems}")
  private String validateItemsTopic;

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
    verify(this.kafkaTemplate, times(1)).send(eq(this.validateItemsTopic),
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

  @Test
  @DisplayName("Test set item to shopping cart successfully")
  @DirtiesContext
  public void givenShoppingCartIdAndProductIdAndSetItemRequestWithTokenWhenSetItemThenShouldReturnAcceptedResponse()
      throws InterruptedException {
    String token = this.generateValidToken();

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

    Thread.sleep(KAFKA_TIMEOUT);

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(token))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSetItemRequest())
        .exchange()
        .expectStatus()
        .isAccepted();

    Thread.sleep(KAFKA_TIMEOUT);

    ShoppingCartResponseDto shoppingCartResponseDto = this.webClient
        .get()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(ShoppingCartResponseDto.class)
        .returnResult()
        .getResponseBody();

    assertEquals(List.of(ItemResponseDto.builder()
            .productId(PRODUCT_ID)
            .unitPrice(PRODUCT_UNIT_PRICE)
            .quantity(PRODUCT_QUANTITY)
            .totalPrice(PRODUCT_TOTAL_PRICE)
            .build()),
        shoppingCartResponseDto.getItems());

  }

  @Test
  @DisplayName("Test set item to shopping cart with invalid identifier")
  public void givenInvalidExistingShoppingCartIdWhenSetItemThenShouldReturnBadRequestResponse() {

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + NOT_NUMERIC_ID + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSetItemRequest())
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test set item to shopping cart with invalid quantity")
  @DirtiesContext
  public void givenExistingShoppingCartIdAndProductIdAndInvalidSetItemRequestWithTokenWhenSetItemThenShouldReturnBadRequestResponse()
      throws InterruptedException {

    String token = this.generateValidToken();

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

    Thread.sleep(KAFKA_TIMEOUT);

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(token))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildInvalidSetItemRequest())
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test set item to shopping cart with invalid token")
  public void givenShoppingCartIdAndProductIdAndSetItemRequestWithInvalidTokenWhenSetItemThenShouldReturnForbiddenResponse() {

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(this.generateExpiredToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSetItemRequest())
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DisplayName("Test set item to shopping cart with non existing shopping cart identifier")
  public void givenNonExistingShoppingCartIdAndProductIdAndSetItemRequestWhenSetItemThenShouldReturnNotFoundResponse() {

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSetItemRequest())
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @DisplayName("Test set item to a completed shopping cart")
  @DirtiesContext
  public void givenCompletedShoppingCartIdAndProductIdAndSetItemRequestWhenSetItemThenShouldReturnConflictResponse()
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

    Thread.sleep(KAFKA_TIMEOUT);

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSetItemRequest())
        .exchange()
        .expectStatus()
        .isAccepted();

    Thread.sleep(KAFKA_TIMEOUT);

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isAccepted();

    Thread.sleep(KAFKA_TIMEOUT);

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSetItemRequest())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  @DisplayName("Test set item to shopping cart when there is an internal error")
  public void givenShoppingCartIdAndProductIdAndSetItemRequestWhenSetItemAndInternalErrorHappensThenShouldReturnInternalServerErrorResponse() {

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .headers(http -> http.setBearerAuth(this.generateTokenWithNotNumericUserId()))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSetItemRequest())
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private static SetItemRequest buildSetItemRequest() {
    return SetItemRequest.builder()
        .unitPrice(PRODUCT_UNIT_PRICE)
        .quantity(PRODUCT_QUANTITY)
        .build();
  }

  private static SetItemRequest buildInvalidSetItemRequest() {
    return SetItemRequest.builder()
        .unitPrice(PRODUCT_UNIT_PRICE)
        .quantity(0)
        .build();
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
