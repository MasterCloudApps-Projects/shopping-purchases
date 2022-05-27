package es.codeurjc.mca.tfm.purchases.unit.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.codeurjc.mca.tfm.purchases.domain.models.Item;
import es.codeurjc.mca.tfm.purchases.domain.models.Order;
import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;
import es.codeurjc.mca.tfm.purchases.domain.models.ShoppingCart;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Tag("UnitTest")
@DisplayName("Order tests")
public class OrderTest {

  private static final Integer USER_ID = 1;

  private static final Long SHOPPING_CART_ID = 1652692327498L;

  private static final Integer PRODUCT_1_ID = 100;

  private static final Double PRODUCT_1_PRICE = 19.99;

  private static final Item ITEM_1 = new Item(PRODUCT_1_ID, PRODUCT_1_PRICE, 1);

  private static final Long ORDER_ID = 1652692327532L;

  private static final List<String> ERRORS = List.of(
      "Not enough stock for product " + PRODUCT_1_ID);

  @Test
  @DisplayName("Test has final state when order is DONE state")
  public void givenADoneStateOrderWhenHasFinalStateThenShouldReturnTrue() {
    Order order = buildOrder(OrderState.DONE);
    assertTrue(order.hasFinalState());
  }

  @Test
  @DisplayName("Test has final state when order is REJECTED state")
  public void givenARejectedStateOrderWhenHasFinalStateThenShouldReturnTrue() {
    Order order = buildOrder(OrderState.REJECTED);
    assertTrue(order.hasFinalState());
  }

  @Test
  @DisplayName("Test has final state when order is in not final state")
  public void givenANonFinalStateOrderWhenHasFinalStateThenShouldReturnFalse() {
    Order order = buildOrder(OrderState.VALIDATING_ITEMS);
    assertFalse(order.hasFinalState());
  }

  @Test
  @DisplayName("Test update state when current order state has less weight")
  public void givenANonFinalStateOrderWhenUpdateToFinalStateThenShouldUpdateAndReturnTrue() {
    Order order = buildOrder(OrderState.VALIDATING_ITEMS);
    assertTrue(order.updateState(OrderState.REJECTED));
    assertEquals(OrderState.REJECTED, order.getState());
  }

  @Test
  @DisplayName("Test update state when current order state has more weight")
  public void givenAFinalStateOrderWhenUpdateToNonFinalStateThenShouldNotUpdateAndReturnFalse() {
    Order order = buildOrder(OrderState.DONE);
    assertFalse(order.updateState(OrderState.VALIDATING_BALANCE));
    assertEquals(OrderState.DONE, order.getState());
  }

  @Test
  @DisplayName("Test update state from final state to other final state")
  public void givenAFinalStateOrderWhenUpdateToOtherFinalStateThenShouldNotUpdateAndReturnFalse() {
    Order order = buildOrder(OrderState.DONE);
    assertFalse(order.updateState(OrderState.REJECTED));
    assertEquals(OrderState.DONE, order.getState());
  }

  @Test
  @DisplayName("Test reject order in done state")
  public void givenADoneStateOrderWhenRejectOrderThenShouldNotUpdateErrorsAndReturnFalse() {
    Order order = buildOrder(OrderState.DONE);
    assertFalse(order.rejectOrder(Optional.of(ERRORS)));
    assertEquals(OrderState.DONE, order.getState());
    assertTrue(order.getErrors().isEmpty());
  }

  @Test
  @DisplayName("Test reject order in not done state")
  public void givenANotDoneStateOrderWhenRejectOrderThenShouldUpdateErrorsAndReturnTrue() {
    Order order = buildOrder(OrderState.VALIDATING_BALANCE);
    assertTrue(order.rejectOrder(Optional.of(ERRORS)));
    assertEquals(OrderState.REJECTED, order.getState());
    assertEquals(ERRORS, order.getErrors().get());
  }

  @Test
  @DisplayName("Test reject order in not done state with previous errors")
  public void givenANotDoneStateOrderWithPreviousErrorsWhenRejectOrderThenShouldUpdateErrorsAndReturnTrue() {
    Order order = buildOrder(OrderState.VALIDATING_BALANCE);
    assertTrue(order.rejectOrder(Optional.of(ERRORS)));
    assertEquals(OrderState.REJECTED, order.getState());
    assertEquals(ERRORS, order.getErrors().get());

    List<String> newErrors = List.of("New error message");
    assertTrue(order.rejectOrder(Optional.of(newErrors)));
    assertEquals(OrderState.REJECTED, order.getState());
    List<String> allErrors = new ArrayList<>();
    allErrors.addAll(ERRORS);
    allErrors.addAll(newErrors);
    assertEquals(allErrors, order.getErrors().get());
  }

  private static ShoppingCart buildShoppingCart() {
    return new ShoppingCart(SHOPPING_CART_ID, USER_ID, true,
        List.of(ITEM_1), PRODUCT_1_PRICE);
  }

  private static Order buildOrder(OrderState orderState) {
    Order order = new Order(ORDER_ID, buildShoppingCart(), orderState,
        Optional.empty());
    return order;
  }

}
