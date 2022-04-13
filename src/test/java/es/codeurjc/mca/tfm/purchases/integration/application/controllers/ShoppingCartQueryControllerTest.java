package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.codeurjc.mca.tfm.purchases.application.dtos.ShoppingCartResponseDto;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("ShoppingCartQueryController integration tests")
public class ShoppingCartQueryControllerTest extends AuthenticatedBaseController {

  @Test
  @DirtiesContext
  @DisplayName("Test get shopping cart successfully")
  public void givenShoppingCartIdWithTokenWhenGetThenShouldReturnOkResponseWithShoppingCartInfo()
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

    assertEquals(shoppingCartId, shoppingCartResponseDto.getId());
    assertEquals(USER_ID, shoppingCartResponseDto.getUserId());
    assertFalse(shoppingCartResponseDto.isCompleted());
    assertTrue(shoppingCartResponseDto.getItems().isEmpty());
    assertEquals(0.0, shoppingCartResponseDto.getTotalPrice());
  }

  @Test
  @DisplayName("Test get shopping cart with invalid identifier")
  public void givenInvalidShoppingCartIdWhenGetThenShouldReturnBadRequestResponse() {

    this.webClient
        .get()
        .uri(SHOPPING_CART_BASE_URL + "/" + NOT_NUMERIC_ID)
        .headers(http -> http.setBearerAuth(this.generateExpiredToken()))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DisplayName("Test get shopping cart without token")
  public void givenShoppingCartIdWithoutTokenWhenGetThenShouldReturnUnauthorizedResponse() {
    this.webClient
        .get()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("Test get shopping cart with invalid token")
  public void givenShoppingCartIdWithInvalidTokenWhenGetThenShouldReturnForbiddenResponse() {

    this.webClient
        .get()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .headers(http -> http.setBearerAuth(this.generateExpiredToken()))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DirtiesContext
  @DisplayName("Test get shopping cart not found for passed user")
  public void givenShoppingCartIdOfOtherUserWithTokenWhenGetThenShouldReturnNotFoundResponse()
      throws InterruptedException {

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

    Thread.sleep(KAFKA_TIMEOUT);

    this.webClient
        .get()
        .uri(SHOPPING_CART_BASE_URL + "/" + shoppingCartId)
        .headers(http -> http.setBearerAuth(this.generateToken("2", TOKEN_EXPIRATION_IN_MILIS)))
        .exchange()
        .expectStatus()
        .isNotFound();

  }

  @Test
  @DisplayName("Test get shopping cart when there is an internal error")
  public void givenShoppingCartIdWhenGetAndInternalErrorHappensThenShouldReturnInternalServerErrorResponse() {

    this.webClient
        .get()
        .uri(SHOPPING_CART_BASE_URL + "/" + SHOPPING_CART_ID)
        .headers(http -> http.setBearerAuth(this.generateTokenWithNotNumericUserId()))
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
