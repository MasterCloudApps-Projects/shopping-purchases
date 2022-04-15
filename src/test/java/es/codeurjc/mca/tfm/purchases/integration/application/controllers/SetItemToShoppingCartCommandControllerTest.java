package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import es.codeurjc.mca.tfm.purchases.application.dtos.requests.SetItemRequest;
import es.codeurjc.mca.tfm.purchases.application.dtos.responses.ItemResponseDto;
import es.codeurjc.mca.tfm.purchases.application.dtos.responses.ShoppingCartResponseDto;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("ShoppingCartCommandController set item endpoint integration tests")
public class SetItemToShoppingCartCommandControllerTest extends ShoppingCartCommandControllerTest {

  private static final Integer PRODUCT_ID = 1;

  private static final Double PRODUCT_UNIT_PRICE = 3.3;

  private static final Integer PRODUCT_QUANTITY = 2;

  private static final Double PRODUCT_TOTAL_PRICE = 6.6;

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

}
