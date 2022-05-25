package es.codeurjc.mca.tfm.purchases.unit.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.codeurjc.mca.tfm.purchases.domain.dtos.ItemDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IllegalShoppingCartStateException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IncompleteShoppingCartAlreadyExistsException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.InvalidItemException;
import es.codeurjc.mca.tfm.purchases.domain.models.Item;
import es.codeurjc.mca.tfm.purchases.domain.models.ShoppingCart;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.ShoppingCartRepository;
import es.codeurjc.mca.tfm.purchases.domain.usecases.ShoppingCartUseCaseImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Tag("UnitTest")
@DisplayName("Shopping Cart Use Case tests")
public class ShoppingCartUseCaseTest {

  private static final Integer USER_ID = 1;

  private static final Long SHOPPING_CART_ID = 1652692327498L;

  private static final Integer PRODUCT_1_ID = 100;

  private static final Double PRODUCT_1_PRICE = 19.99;

  private static final Item ITEM_1 = new Item(PRODUCT_1_ID, PRODUCT_1_PRICE, 1);

  @Mock
  private ShoppingCartRepository shoppingCartRepository;

  @Captor
  ArgumentCaptor<ShoppingCartDto> shoppingCartDtoArgumentCaptor;

  private ShoppingCartUseCaseImpl shoppingCartUseCase;

  @BeforeEach
  public void setUp() {
    this.shoppingCartUseCase = new ShoppingCartUseCaseImpl(this.shoppingCartRepository);
  }

  @Test
  @DisplayName("Test shopping cart creation when user already has an incomplete shopping cart")
  public void givenAnUserIdWhenCreateAndAlreadyExistsIncompleteShoppingCartForHimThenShouldThrowIncompleteShoppingCartAlreadyExistsException() {
    when(this.shoppingCartRepository.getIncompleteByUser(USER_ID)).thenReturn(
        Optional.of(buildShoppingCartDto(USER_ID)));

    assertThrows(IncompleteShoppingCartAlreadyExistsException.class,
        () -> this.shoppingCartUseCase.create(USER_ID));
    verify(this.shoppingCartRepository, never()).create(any(ShoppingCartDto.class));
  }

  @Test
  @DisplayName("Test shopping cart creation when user hasn't an incomplete shopping cart")
  public void givenAnUserIdWhenCreateAndDoesNotExistIncompleteShoppingCartForHimThenShouldCreateShoppingCart() {
    ShoppingCartDto shoppingCartDto = this.shoppingCartUseCase.create(USER_ID);

    verify(this.shoppingCartRepository, times(1)).create(shoppingCartDtoArgumentCaptor.capture());

    assertNotNull(shoppingCartDto.getId());
    assertEquals(USER_ID, shoppingCartDto.getUserId());
    assertTrue(shoppingCartDto.getItems().isEmpty());
    assertEquals(0d, shoppingCartDto.getTotalPrice());

    assertEquals(shoppingCartDto, shoppingCartDtoArgumentCaptor.getValue());
  }

  @Test
  @DisplayName("Test shopping cart deletion when shopping cart not found")
  public void givenAShoppingCartIdAndUserIdWhenDeleteAndNotFoundShoppingCartThenShouldReturnEmptyOptional() {
    assertTrue(this.shoppingCartUseCase.delete(SHOPPING_CART_ID, USER_ID).isEmpty());

    verify(this.shoppingCartRepository, never()).delete(SHOPPING_CART_ID);
  }

  @Test
  @DisplayName("Test shopping cart deletion when shopping cart is not deletable")
  public void givenAShoppingCartIdAndUserIdWhenDeleteAndFoundShoppingCartIsNotDeletableThenShouldThrowIllegalShoppingCartStateException() {
    when(this.shoppingCartRepository.getByIdAndUser(SHOPPING_CART_ID, USER_ID))
        .thenReturn(Optional.of(buildCompletedShoppingCartDto(SHOPPING_CART_ID, USER_ID)));

    assertThrows(IllegalShoppingCartStateException.class,
        () -> this.shoppingCartUseCase.delete(SHOPPING_CART_ID, USER_ID));
    verify(this.shoppingCartRepository, never()).delete(SHOPPING_CART_ID);
  }

  @Test
  @DisplayName("Test shopping cart deletion when shopping cart is deletable")
  public void givenAShoppingCartIdAndUserIdWhenDeleteAndFoundShoppingCartIsDeletableThenShouldDeleteShoppingCart() {
    when(this.shoppingCartRepository.getByIdAndUser(SHOPPING_CART_ID, USER_ID))
        .thenReturn(Optional.of(buildIncompleteShoppingCartDto(SHOPPING_CART_ID, USER_ID)));

    Optional<ShoppingCartDto> optionalDeletedCart = this.shoppingCartUseCase.delete(
        SHOPPING_CART_ID, USER_ID);

    verify(this.shoppingCartRepository, times(1)).delete(SHOPPING_CART_ID);

    assertFalse(optionalDeletedCart.isEmpty());
    ShoppingCartDto deletedShoppingCart = optionalDeletedCart.get();
    assertEquals(SHOPPING_CART_ID, deletedShoppingCart.getId());
    assertEquals(USER_ID, deletedShoppingCart.getUserId());
    assertEquals(List.of(new ItemDto(ITEM_1)), deletedShoppingCart.getItems());
    assertEquals(PRODUCT_1_PRICE, deletedShoppingCart.getTotalPrice());
  }

  @Test
  @DisplayName("Test shopping cart completion when shopping cart not found")
  public void givenAShoppingCartIdAndUserIdWhenCompleteAndNotFoundShoppingCartThenShouldReturnEmptyOptional() {
    assertTrue(this.shoppingCartUseCase.complete(SHOPPING_CART_ID, USER_ID).isEmpty());

    verify(this.shoppingCartRepository, never()).complete(any(ShoppingCartDto.class));
  }

  @Test
  @DisplayName("Test shopping cart completion when shopping cart is not completable")
  public void givenAShoppingCartIdAndUserIdWhenCompleteAndFoundShoppingCartIsNotCompletableThenShouldThrowIllegalShoppingCartStateException() {
    when(this.shoppingCartRepository.getByIdAndUser(SHOPPING_CART_ID, USER_ID))
        .thenReturn(Optional.of(buildCompletedShoppingCartDto(SHOPPING_CART_ID, USER_ID)));

    assertThrows(IllegalShoppingCartStateException.class,
        () -> this.shoppingCartUseCase.complete(SHOPPING_CART_ID, USER_ID));
    verify(this.shoppingCartRepository, never()).complete(any(ShoppingCartDto.class));
  }

  @Test
  @DisplayName("Test shopping cart completion when shopping cart is completable")
  public void givenAShoppingCartIdAndUserIdWhenCompleteAndFoundShoppingCartIsCompletableThenShouldCompleteShoppingCart() {
    when(this.shoppingCartRepository.getByIdAndUser(SHOPPING_CART_ID, USER_ID))
        .thenReturn(Optional.of(buildIncompleteShoppingCartDto(SHOPPING_CART_ID, USER_ID)));

    Optional<ShoppingCartDto> optionalCompletedCart =
        this.shoppingCartUseCase.complete(SHOPPING_CART_ID, USER_ID);

    assertFalse(optionalCompletedCart.isEmpty());
    ShoppingCartDto completedShoppingCart = optionalCompletedCart.get();
    assertEquals(SHOPPING_CART_ID, completedShoppingCart.getId());
    assertEquals(USER_ID, completedShoppingCart.getUserId());
    assertEquals(List.of(new ItemDto(ITEM_1)), completedShoppingCart.getItems());
    assertEquals(PRODUCT_1_PRICE, completedShoppingCart.getTotalPrice());

    verify(this.shoppingCartRepository, times(1))
        .complete(completedShoppingCart);
  }

  @Test
  @DisplayName("Test set item to shopping cart when shopping cart not found")
  public void givenAShoppingCartIdAndUserIdAdnProductInfoWhenSetItemAndNotFoundShoppingCartThenShouldReturnEmptyOptional() {
    assertTrue(this.shoppingCartUseCase.setItem(SHOPPING_CART_ID, USER_ID, ITEM_1.getProductId(),
        ITEM_1.getUnitPrice(), ITEM_1.getQuantity()).isEmpty());

    verify(this.shoppingCartRepository, never()).updateItems(any(ShoppingCartDto.class));
  }

  @Test
  @DisplayName("Test set item to shopping cart when shopping cart is completed")
  public void givenAShoppingCartIdAndUserIdAndProductInfoWhenSetItemAndFoundShoppingCartIsCompletedThenShouldThrowIllegalShoppingCartStateException() {
    when(this.shoppingCartRepository.getByIdAndUser(SHOPPING_CART_ID, USER_ID))
        .thenReturn(Optional.of(buildCompletedShoppingCartDto(SHOPPING_CART_ID, USER_ID)));

    assertThrows(IllegalShoppingCartStateException.class,
        () -> this.shoppingCartUseCase.setItem(SHOPPING_CART_ID, USER_ID, ITEM_1.getProductId(),
            ITEM_1.getUnitPrice(), ITEM_1.getQuantity()));
    verify(this.shoppingCartRepository, never()).updateItems(any(ShoppingCartDto.class));
  }

  @Test
  @DisplayName("Test set item with negative quantity to incomplete shopping cart")
  public void givenAShoppingCartIdAndUserIdAndInvalidProductInfoWhenSetItemAndFoundShoppingCartIsIncompleteThenShouldThrowInvalidItemException() {
    when(this.shoppingCartRepository.getByIdAndUser(SHOPPING_CART_ID, USER_ID))
        .thenReturn(Optional.of(buildIncompleteShoppingCartDto(SHOPPING_CART_ID, USER_ID)));

    assertThrows(InvalidItemException.class,
        () -> this.shoppingCartUseCase.setItem(SHOPPING_CART_ID, USER_ID, ITEM_1.getProductId(),
            ITEM_1.getUnitPrice(), -1));
    verify(this.shoppingCartRepository, never()).updateItems(any(ShoppingCartDto.class));
  }

  @Test
  @DisplayName("Test set item to incomplete shopping cart")
  public void givenAShoppingCartIdAndUserIdAndProductInfoWhenSetItemAndFoundShoppingCartIsIncompleteThenShouldSetItem() {
    when(this.shoppingCartRepository.getByIdAndUser(SHOPPING_CART_ID, USER_ID))
        .thenReturn(Optional.of(buildIncompleteShoppingCartDto(SHOPPING_CART_ID, USER_ID)));

    Optional<ShoppingCartDto> optionalUpdatedCart =
        this.shoppingCartUseCase.setItem(SHOPPING_CART_ID, USER_ID, ITEM_1.getProductId(),
            ITEM_1.getUnitPrice(), ITEM_1.getQuantity());

    assertFalse(optionalUpdatedCart.isEmpty());
    ShoppingCartDto updatedShoppingCart = optionalUpdatedCart.get();
    assertEquals(SHOPPING_CART_ID, updatedShoppingCart.getId());
    assertEquals(USER_ID, updatedShoppingCart.getUserId());
    assertEquals(List.of(new ItemDto(ITEM_1)), updatedShoppingCart.getItems());
    assertEquals(PRODUCT_1_PRICE, updatedShoppingCart.getTotalPrice());

    verify(this.shoppingCartRepository, times(1))
        .updateItems(updatedShoppingCart);
  }

  @Test
  @DisplayName("Test delete item from shopping cart when shopping cart not found")
  public void givenAShoppingCartIdAndUserIdAndProductIdWhenDeleteItemAndNotFoundShoppingCartThenShouldReturnEmptyOptional() {
    assertTrue(this.shoppingCartUseCase.deleteItem(SHOPPING_CART_ID, USER_ID, ITEM_1.getProductId())
        .isEmpty());

    verify(this.shoppingCartRepository, never()).updateItems(any(ShoppingCartDto.class));
  }

  @Test
  @DisplayName("Test delete item from shopping cart when shopping cart is completed")
  public void givenAShoppingCartIdAndUserIdAndProductIdWhenDeleteItemAndFoundShoppingCartIsCompletedThenShouldThrowIllegalShoppingCartStateException() {
    when(this.shoppingCartRepository.getByIdAndUser(SHOPPING_CART_ID, USER_ID))
        .thenReturn(Optional.of(buildCompletedShoppingCartDto(SHOPPING_CART_ID, USER_ID)));

    assertThrows(IllegalShoppingCartStateException.class,
        () -> this.shoppingCartUseCase.deleteItem(SHOPPING_CART_ID, USER_ID,
            ITEM_1.getProductId()));
    verify(this.shoppingCartRepository, never()).updateItems(any(ShoppingCartDto.class));
  }

  @Test
  @DisplayName("Test delete non existing item from incomplete shopping cart")
  public void givenAShoppingCartIdAndUserIdAndNonExistingProductIdWhenDeleteItemAndFoundShoppingCartIsIncompleteThenShouldReturnShoppingCart() {
    when(this.shoppingCartRepository.getByIdAndUser(SHOPPING_CART_ID, USER_ID))
        .thenReturn(Optional.of(buildIncompleteShoppingCartDto(SHOPPING_CART_ID, USER_ID)));

    Optional<ShoppingCartDto> optionalUpdatedCart =
        this.shoppingCartUseCase.deleteItem(SHOPPING_CART_ID, USER_ID, 999);

    assertFalse(optionalUpdatedCart.isEmpty());
    ShoppingCartDto updatedShoppingCart = optionalUpdatedCart.get();
    assertEquals(SHOPPING_CART_ID, updatedShoppingCart.getId());
    assertEquals(USER_ID, updatedShoppingCart.getUserId());
    assertEquals(List.of(new ItemDto(ITEM_1)), updatedShoppingCart.getItems());
    assertEquals(PRODUCT_1_PRICE, updatedShoppingCart.getTotalPrice());

    verify(this.shoppingCartRepository, never()).updateItems(updatedShoppingCart);
  }

  @Test
  @DisplayName("Test delete item from incomplete shopping cart")
  public void givenAShoppingCartIdAndUserIdAndProductIdWhenDeleteItemAndFoundShoppingCartIsIncompleteThenShouldDeleteItem() {
    when(this.shoppingCartRepository.getByIdAndUser(SHOPPING_CART_ID, USER_ID))
        .thenReturn(Optional.of(buildIncompleteShoppingCartDto(SHOPPING_CART_ID, USER_ID)));

    Optional<ShoppingCartDto> optionalUpdatedCart =
        this.shoppingCartUseCase.deleteItem(SHOPPING_CART_ID, USER_ID, ITEM_1.getProductId());

    assertFalse(optionalUpdatedCart.isEmpty());
    ShoppingCartDto updatedShoppingCart = optionalUpdatedCart.get();
    assertEquals(SHOPPING_CART_ID, updatedShoppingCart.getId());
    assertEquals(USER_ID, updatedShoppingCart.getUserId());
    assertTrue(updatedShoppingCart.getItems().isEmpty());
    assertEquals(0, updatedShoppingCart.getTotalPrice());

    verify(this.shoppingCartRepository, times(1))
        .updateItems(updatedShoppingCart);
  }

  private static ShoppingCartDto buildShoppingCartDto(Integer userId) {
    ShoppingCart shoppingCart = new ShoppingCart(userId);
    return new ShoppingCartDto(shoppingCart);
  }

  private static ShoppingCartDto buildShoppingCartDto(Long shoppingCartId, Integer userId,
      boolean completed) {
    ShoppingCart shoppingCart = new ShoppingCart(shoppingCartId, userId, completed,
        List.of(ITEM_1), PRODUCT_1_PRICE);
    return new ShoppingCartDto(shoppingCart);
  }

  private static ShoppingCartDto buildCompletedShoppingCartDto(Long shoppingCartId,
      Integer userId) {
    return buildShoppingCartDto(shoppingCartId, userId, true);
  }

  private static ShoppingCartDto buildIncompleteShoppingCartDto(Long shoppingCartId,
      Integer userId) {
    return buildShoppingCartDto(shoppingCartId, userId, false);
  }
}
