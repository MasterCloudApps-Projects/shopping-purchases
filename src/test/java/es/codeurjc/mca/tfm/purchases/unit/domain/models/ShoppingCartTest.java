package es.codeurjc.mca.tfm.purchases.unit.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.codeurjc.mca.tfm.purchases.domain.models.Item;
import es.codeurjc.mca.tfm.purchases.domain.models.ShoppingCart;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Tag("UnitTest")
@DisplayName("Shopping cart tests")
public class ShoppingCartTest {

  private static final Integer USER_ID = 1;

  private static final Long SHOPPING_CART_ID = 1652692327498L;

  private static final Integer PRODUCT_1_ID = 100;

  private static final Double PRODUCT_1_PRICE = 19.99;

  private static final Item ITEM_1 = new Item(PRODUCT_1_ID, PRODUCT_1_PRICE, 1);

  private static final Integer PRODUCT_2_ID = 200;

  private static final Double PRODUCT_2_PRICE = 3.05;

  private static final Item ITEM_2 = new Item(PRODUCT_2_ID, PRODUCT_2_PRICE, 2);

  @Test
  @DisplayName("Test is deletable on incomplete shopping cart")
  public void givenAnIncompleteShoppingCartWhenIsDeletableThenShouldReturnTrue() {
    ShoppingCart shoppingCart = new ShoppingCart(USER_ID);
    assertTrue(shoppingCart.isDeletable());
  }

  @Test
  @DisplayName("Test is deletable on completed shopping cart")
  public void givenACompletedShoppingCartWhenIsDeletableThenShouldReturnFalse() {
    ShoppingCart shoppingCart = buildCompleteShoppingCart();
    assertFalse(shoppingCart.isDeletable());
  }

  @Test
  @DisplayName("Test is completable on incomplete shopping cart")
  public void givenACompletedShoppingCartWhenIsCompletableThenShouldReturnFalse() {
    ShoppingCart shoppingCart = buildCompleteShoppingCart();
    assertFalse(shoppingCart.isCompletable());
  }

  @Test
  @DisplayName("Test is completable on incomplete shopping cart but without items")
  public void givenAnIncompleteShoppingCartWithoutItemsWhenIsCompletableThenShouldReturnFalse() {
    ShoppingCart shoppingCart = new ShoppingCart(SHOPPING_CART_ID, USER_ID, false,
        List.of(), 0.0);
    assertFalse(shoppingCart.isCompletable());
  }

  @Test
  @DisplayName("Test is completable on incomplete shopping cart with items but with zero total price")
  public void givenAnIncompleteShoppingCartWithItemsButNotTotalPriceWhenIsCompletableThenShouldReturnFalse() {
    ShoppingCart shoppingCart = new ShoppingCart(SHOPPING_CART_ID, USER_ID, true,
        List.of(ITEM_1), 0.0);
    assertFalse(shoppingCart.isCompletable());
  }

  @Test
  @DisplayName("Test is completable on incomplete shopping cart with items and non zero total price")
  public void givenAnIncompleteShoppingCartWithItemsAndNonZeroTotalPriceWhenIsCompletableThenShouldReturnFalse() {
    ShoppingCart shoppingCart = new ShoppingCart(SHOPPING_CART_ID, USER_ID, true,
        List.of(ITEM_1), PRODUCT_1_PRICE);
    assertFalse(shoppingCart.isCompletable());
  }

  @Test
  @DisplayName("Test set item on incomplete shopping cart with invalid item price")
  public void givenAnIncompleteShoppingCartAndInvalidItemUnitPriceWhenSetItemThenShouldNotSetItemAndReturnFalse() {
    ShoppingCart shoppingCart = new ShoppingCart(USER_ID);
    assertFalse(
        shoppingCart.setItem(ITEM_2.getProductId(), 0.0, ITEM_2.getQuantity()));
    assertTrue(shoppingCart.getItems().isEmpty());
    assertEquals(0.0, shoppingCart.getTotalPrice());
  }

  @Test
  @DisplayName("Test set item on incomplete shopping cart with invalid item quantity")
  public void givenAnIncompleteShoppingCartAndInvalidItemQuantityWhenSetItemThenShouldNotSetItemAndReturnFalse() {
    ShoppingCart shoppingCart = new ShoppingCart(USER_ID);
    assertFalse(
        shoppingCart.setItem(ITEM_2.getProductId(), ITEM_2.getUnitPrice(), 0));
    assertTrue(shoppingCart.getItems().isEmpty());
    assertEquals(0.0, shoppingCart.getTotalPrice());
  }

  @Test
  @DisplayName("Test set item on incomplete shopping cart with no items")
  public void givenAnIncompleteShoppingCartWithEmptyItemsAndValidItemWhenSetItemThenShouldSetItemAndReturnTrue() {
    ShoppingCart shoppingCart = new ShoppingCart(USER_ID);
    assertTrue(
        shoppingCart.setItem(ITEM_2.getProductId(), ITEM_2.getUnitPrice(), ITEM_2.getQuantity()));
    assertEquals(1, shoppingCart.getItems().size());
    Item addedItem = shoppingCart.getItems().get(0);
    assertEquals(ITEM_2.getProductId(), addedItem.getProductId());
    assertEquals(ITEM_2.getUnitPrice(), addedItem.getUnitPrice());
    assertEquals(ITEM_2.getQuantity(), addedItem.getQuantity());
    assertEquals(ITEM_2.getTotalPrice(), addedItem.getTotalPrice());
    assertEquals(ITEM_2.getTotalPrice(), shoppingCart.getTotalPrice());
  }

  @Test
  @DisplayName("Test set item on incomplete shopping cart with items")
  public void givenAnIncompleteShoppingCartWithItemsAndValidItemWhenSetItemThenShouldSetItemAndReturnTrue() {
    List<Item> items = new ArrayList<>();
    items.add(ITEM_1);
    ShoppingCart shoppingCart = new ShoppingCart(SHOPPING_CART_ID, USER_ID, false, items,
        PRODUCT_1_PRICE);
    assertTrue(
        shoppingCart.setItem(ITEM_2.getProductId(), ITEM_2.getUnitPrice(), ITEM_2.getQuantity()));
    assertEquals(2, shoppingCart.getItems().size());
    assertEquals(ITEM_1.getProductId(), shoppingCart.getItems().get(0).getProductId());
    assertEquals(ITEM_1.getUnitPrice(), shoppingCart.getItems().get(0).getUnitPrice());
    assertEquals(ITEM_1.getQuantity(), shoppingCart.getItems().get(0).getQuantity());
    assertEquals(ITEM_1.getTotalPrice(), shoppingCart.getItems().get(0).getTotalPrice());
    assertEquals(ITEM_2.getProductId(), shoppingCart.getItems().get(1).getProductId());
    assertEquals(ITEM_2.getUnitPrice(), shoppingCart.getItems().get(1).getUnitPrice());
    assertEquals(ITEM_2.getQuantity(), shoppingCart.getItems().get(1).getQuantity());
    assertEquals(ITEM_2.getTotalPrice(), shoppingCart.getItems().get(1).getTotalPrice());
    assertEquals(ITEM_1.getTotalPrice() + ITEM_2.getTotalPrice(), shoppingCart.getTotalPrice());
  }

  @Test
  @DisplayName("Test set item on incomplete shopping cart containing that item")
  public void givenAnIncompleteShoppingCartContainingThatItemWhenSetItemThenShouldSetItemAndReturnTrue() {
    ShoppingCart shoppingCart = new ShoppingCart(SHOPPING_CART_ID, USER_ID, false,
        List.of(ITEM_1), PRODUCT_1_PRICE);
    assertTrue(
        shoppingCart.setItem(ITEM_1.getProductId(), ITEM_2.getUnitPrice(), ITEM_2.getQuantity()));
    assertEquals(1, shoppingCart.getItems().size());
    Item updatedItem = shoppingCart.getItems().get(0);
    assertEquals(ITEM_1.getProductId(), updatedItem.getProductId());
    assertEquals(ITEM_2.getUnitPrice(), updatedItem.getUnitPrice());
    assertEquals(ITEM_2.getQuantity(), updatedItem.getQuantity());
    assertEquals(ITEM_2.getTotalPrice(), updatedItem.getTotalPrice());
    assertEquals(ITEM_2.getTotalPrice(), shoppingCart.getTotalPrice());
  }

  @Test
  @DisplayName("Test delete non containing item from incomplete shopping cart")
  public void givenACompletedShoppingCartAndNonContainingItemWhenDeleteItemThenShouldNotDeleteItemAndReturnFalse() {
    List<Item> items = new ArrayList<>();
    items.add(ITEM_1);
    ShoppingCart shoppingCart = new ShoppingCart(SHOPPING_CART_ID, USER_ID, false,
        items, PRODUCT_1_PRICE);
    assertFalse(
        shoppingCart.deleteItem(ITEM_2.getProductId()));
    assertEquals(1, shoppingCart.getItems().size());
    assertEquals(ITEM_1, shoppingCart.getItems().get(0));
    assertEquals(PRODUCT_1_PRICE, shoppingCart.getTotalPrice());
  }

  @Test
  @DisplayName("Test delete containing only item from incomplete shopping cart")
  public void givenACompletedShoppingCartContainingOnlyItemToDeleteWhenDeleteItemThenShouldDeleteItemAndReturnTrue() {
    List<Item> items = new ArrayList<>();
    items.add(ITEM_1);
    ShoppingCart shoppingCart = new ShoppingCart(SHOPPING_CART_ID, USER_ID, false,
        items, PRODUCT_1_PRICE);
    assertTrue(
        shoppingCart.deleteItem(ITEM_1.getProductId()));
    assertTrue(shoppingCart.getItems().isEmpty());
    assertEquals(0.0, shoppingCart.getTotalPrice());
  }

  @Test
  @DisplayName("Test delete item from incomplete shopping cart with more items")
  public void givenACompletedShoppingCartWithItemsWhenDeleteItemThenShouldDeleteItemAndReturnTrue() {
    List<Item> items = new ArrayList<>();
    items.add(ITEM_1);
    items.add(ITEM_2);
    ShoppingCart shoppingCart = new ShoppingCart(SHOPPING_CART_ID, USER_ID, false,
        items, ITEM_1.getTotalPrice() + ITEM_2.getTotalPrice());
    assertTrue(
        shoppingCart.deleteItem(ITEM_2.getProductId()));
    assertEquals(1, shoppingCart.getItems().size());
    assertEquals(ITEM_1, shoppingCart.getItems().get(0));
    assertEquals(PRODUCT_1_PRICE, shoppingCart.getTotalPrice());
  }

  private static ShoppingCart buildCompleteShoppingCart() {
    return new ShoppingCart(SHOPPING_CART_ID, USER_ID, true,
        List.of(ITEM_1), PRODUCT_1_PRICE);
  }

}
