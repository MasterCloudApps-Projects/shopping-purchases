package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@DisplayName("ShoppingCartCommandController delete item endpoint integration tests")
public class DeleteItemFromShoppingCartCommandControllerTest extends
    ShoppingCartCommandControllerTest {

  @Test
  @DisplayName("Test delete item from shopping cart successfully")
  @DirtiesContext
  public void givenShoppingCartIdAndProductIdWithTokenWhenDeleteItemThenShouldReturnAcceptedResponse()
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

    Thread.sleep(WAIT_TIME);

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(token))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSetItemRequest())
        .exchange()
        .expectStatus()
        .isAccepted();

    Thread.sleep(WAIT_TIME);

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

    Thread.sleep(WAIT_TIME);

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isAccepted();

    Thread.sleep(WAIT_TIME);

    shoppingCartResponseDto = this.webClient
        .get()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(ShoppingCartResponseDto.class)
        .returnResult()
        .getResponseBody();

    assertTrue(shoppingCartResponseDto.getItems().isEmpty());

  }

  @Test
  @DisplayName("Test delete item from shopping cart with invalid identifier")
  public void givenInvalidExistingShoppingCartIdWhenDeleteItemThenShouldReturnBadRequestResponse() {

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + NOT_NUMERIC_ID + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test delete item from shopping cart without token")
  public void givenShoppingCartIdAndProductIdWithoutTokenWhenDeleteItemThenShouldReturnUnauthorizedResponse() {
    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID + "/products/" + PRODUCT_ID)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("Test delete item from shopping cart with invalid token")
  public void givenShoppingCartIdAndProductIdWithInvalidTokenWhenDeleteItemThenShouldReturnForbiddenResponse() {

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(this.generateExpiredToken()))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DisplayName("Test delete item from shopping cart with non existing shopping cart identifier")
  public void givenNonExistingShoppingCartIdAndProductIdWhenDeleteItemThenShouldReturnNotFoundResponse() {

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @DisplayName("Test delete item from a completed shopping cart")
  @DirtiesContext
  public void givenCompletedShoppingCartIdAndProductIdWhenDeleteItemThenShouldReturnConflictResponse()
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

    Thread.sleep(WAIT_TIME);

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(buildSetItemRequest())
        .exchange()
        .expectStatus()
        .isAccepted();

    Thread.sleep(WAIT_TIME);

    this.webClient
        .patch()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isAccepted();

    Thread.sleep(WAIT_TIME);

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId + "/products/" + PRODUCT_ID)
        .headers(http -> http.setBearerAuth(this.generateValidToken()))
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  @DisplayName("Test delete item from shopping cart when there is an internal error")
  public void givenShoppingCartIdAndProductIdWhenDeleteItemAndInternalErrorHappensThenShouldReturnInternalServerErrorResponse() {

    this.webClient
        .delete()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .headers(http -> http.setBearerAuth(this.generateTokenWithNotNumericUserId()))
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
