package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("ShoppingCartCommandController create endpoint integration tests")
public class CreateShoppingCartCommandControllerTest extends ShoppingCartCommandControllerTest {

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
        buildShoppingCart(shoppingCartId));

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

}
