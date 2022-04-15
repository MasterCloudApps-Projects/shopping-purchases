package es.codeurjc.mca.tfm.purchases.integration.application.controllers;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import es.codeurjc.mca.tfm.purchases.application.dtos.responses.ShoppingCartResponseDto;
import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("ShoppingCartCommandController complete endpoint integration tests")
public class CompleteShoppingCartCommandControllerTest extends ShoppingCartCommandControllerTest {

  @Value("${kafka.topics.validateItems}")
  private String validateItemsTopic;

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

}
