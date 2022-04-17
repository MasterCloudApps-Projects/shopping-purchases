package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("ShoppingCartCommandController delete endpoint integration tests")
public class DeleteShoppingCartCommandControllerTest extends ShoppingCartCommandControllerTest {

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

    Thread.sleep(WAIT_TIME);

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

    Thread.sleep(WAIT_TIME);

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

}
