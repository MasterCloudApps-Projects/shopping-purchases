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
import es.codeurjc.mca.tfm.purchases.domain.dtos.OrderDto;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IllegalOrderStateException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IllegalShoppingCartStateException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.PreviousOrderStateUpdateException;
import es.codeurjc.mca.tfm.purchases.domain.models.Item;
import es.codeurjc.mca.tfm.purchases.domain.models.OrderState;
import es.codeurjc.mca.tfm.purchases.domain.models.ShoppingCart;
import es.codeurjc.mca.tfm.purchases.domain.ports.out.OrderRepository;
import es.codeurjc.mca.tfm.purchases.domain.services.OrderStateService;
import es.codeurjc.mca.tfm.purchases.domain.usecases.OrderUseCaseImpl;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Tag("UnitTest")
@DisplayName("Order Use Case tests")
public class OrderUseCaseTest {

  private static final Integer USER_ID = 1;

  private static final Long SHOPPING_CART_ID = 1652692327498L;

  private static final Integer PRODUCT_1_ID = 100;

  private static final Double PRODUCT_1_PRICE = 19.99;

  private static final Item ITEM_1 = new Item(PRODUCT_1_ID, PRODUCT_1_PRICE, 1);

  private static final Long ORDER_ID = 1652692327532L;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private OrderStateService orderStateService;

  @Mock
  private Map<String, OrderStateService> orderStateServiceMap;

  private OrderUseCaseImpl orderUseCase;

  @Captor
  ArgumentCaptor<OrderDto> orderDtoArgumentCaptor;

  @BeforeEach
  public void setUp() {
    this.orderUseCase = new OrderUseCaseImpl(this.orderRepository, this.orderStateServiceMap);
  }

  @Test
  @DisplayName("Test order creation with completed shopping cart")
  public void givenACompletedShoppingCartDtoWhenCreateThenShouldCreateAndReturnOrderDto() {
    OrderDto orderDto = this.orderUseCase.create(buildCompletedShoppingCartDto());

    verify(this.orderRepository, times(1)).create(orderDtoArgumentCaptor.capture());

    assertNotNull(orderDto.getId());
    assertEquals(SHOPPING_CART_ID, orderDto.getShoppingCart().getId());
    assertEquals(USER_ID, orderDto.getShoppingCart().getUserId());
    assertEquals(List.of(new ItemDto(ITEM_1)), orderDto.getShoppingCart().getItems());
    assertEquals(PRODUCT_1_PRICE, orderDto.getShoppingCart().getTotalPrice());
    assertEquals(OrderState.CREATED.name(), orderDto.getState());
    assertTrue(orderDto.getErrors().isEmpty());

    assertEquals(orderDto, orderDtoArgumentCaptor.getValue());
  }

  @Test
  @DisplayName("Test order creation with incomplete shopping cart")
  public void givenAnIncompleteShoppingCartDtoWhenCreateThenShouldThrowIllegalShoppingCartStateException() {
    assertThrows(IllegalShoppingCartStateException.class,
        () -> this.orderUseCase.create(buildIncompleteShoppingCartDto()));
    verify(this.orderRepository, never()).create(any(OrderDto.class));
  }

  @Test
  @DisplayName("Test update a non existing order")
  public void givenANonExistingOrderWhenUpdateThenShouldTReturnEmptyOptional() {
    assertTrue(this.orderUseCase
        .update(ORDER_ID, OrderState.VALIDATING_ITEMS.name(), Optional.empty())
        .isEmpty());
    verify(this.orderRepository, never()).update(any(OrderDto.class));
  }

  @Test
  @DisplayName("Test update an order in final state")
  public void givenAFinalStateOrderWhenUpdateThenShouldThrowIllegalOrderStateException() {
    when(this.orderRepository.findById(ORDER_ID)).thenReturn(
        Optional.of(buildFinalStateOrderDto()));

    assertThrows(IllegalOrderStateException.class, () -> this.orderUseCase
        .update(ORDER_ID, OrderState.VALIDATING_ITEMS.name(), Optional.empty()));
    verify(this.orderRepository, never()).update(any(OrderDto.class));
  }

  @Test
  @DisplayName("Test update an order to rejected state")
  public void givenAnOrderWhenUpdateToRejectedStateThenShouldUpdateOrderWithErrors() {
    when(this.orderRepository.findById(ORDER_ID)).thenReturn(
        Optional.of(buildNonFinalStateOrderDto()));

    when(this.orderStateServiceMap.get(OrderState.REJECTED.name()))
        .thenReturn(this.orderStateService);

    List<String> errors = List.of("Not enough stock for product " + PRODUCT_1_ID);
    Optional<OrderDto> optionalOrderDto =
        this.orderUseCase.update(ORDER_ID, OrderState.REJECTED.name(), Optional.of(errors));

    assertFalse(optionalOrderDto.isEmpty());
    OrderDto updatedOrder = optionalOrderDto.get();
    assertEquals(ORDER_ID, updatedOrder.getId());
    assertEquals(SHOPPING_CART_ID, updatedOrder.getShoppingCart().getId());
    assertEquals(USER_ID, updatedOrder.getShoppingCart().getUserId());
    assertEquals(List.of(new ItemDto(ITEM_1)), updatedOrder.getShoppingCart().getItems());
    assertEquals(PRODUCT_1_PRICE, updatedOrder.getShoppingCart().getTotalPrice());
    assertEquals(OrderState.REJECTED.name(), updatedOrder.getState());
    assertEquals(errors, updatedOrder.getErrors().get());

    verify(this.orderRepository, times(1)).update(updatedOrder);
    verify(this.orderStateService, times(1))
        .performAction(OrderState.VALIDATING_ITEMS, OrderState.REJECTED, updatedOrder);
  }

  @Test
  @DisplayName("Test update an order to a previous state")
  public void givenANonFinalStateOrderWhenUpdateToAPreviousStateThenShouldThrowPreviousOrderStateUpdateException() {
    when(this.orderRepository.findById(ORDER_ID)).thenReturn(
        Optional.of(buildNonFinalStateOrderDto()));

    assertThrows(PreviousOrderStateUpdateException.class, () -> this.orderUseCase
        .update(ORDER_ID, OrderState.CREATED.name(), Optional.empty()));
    verify(this.orderRepository, never()).update(any(OrderDto.class));
  }

  @Test
  @DisplayName("Test update an order to non rejected state")
  public void givenAnNonFinalStateOrderWhenUpdateToNonRejectedStateThenShouldUpdateOrderWithNoErrors() {
    when(this.orderRepository.findById(ORDER_ID)).thenReturn(
        Optional.of(buildNonFinalStateOrderDto()));

    when(this.orderStateServiceMap.get(OrderState.VALIDATING_BALANCE.name()))
        .thenReturn(this.orderStateService);

    List<String> errors = List.of("Not enough stock for product " + PRODUCT_1_ID);
    Optional<OrderDto> optionalOrderDto =
        this.orderUseCase.update(ORDER_ID, OrderState.VALIDATING_BALANCE.name(), Optional.of(errors));

    assertFalse(optionalOrderDto.isEmpty());
    OrderDto updatedOrder = optionalOrderDto.get();
    assertEquals(ORDER_ID, updatedOrder.getId());
    assertEquals(SHOPPING_CART_ID, updatedOrder.getShoppingCart().getId());
    assertEquals(USER_ID, updatedOrder.getShoppingCart().getUserId());
    assertEquals(List.of(new ItemDto(ITEM_1)), updatedOrder.getShoppingCart().getItems());
    assertEquals(PRODUCT_1_PRICE, updatedOrder.getShoppingCart().getTotalPrice());
    assertEquals(OrderState.VALIDATING_BALANCE.name(), updatedOrder.getState());
    assertTrue(updatedOrder.getErrors().isEmpty());

    verify(this.orderRepository, times(1)).update(updatedOrder);
    verify(this.orderStateService, times(1))
        .performAction(OrderState.VALIDATING_ITEMS, OrderState.VALIDATING_BALANCE, updatedOrder);
  }

  private static ShoppingCartDto buildShoppingCartDto(boolean completed) {
    ShoppingCart shoppingCart = new ShoppingCart(SHOPPING_CART_ID, USER_ID, completed,
        List.of(ITEM_1), PRODUCT_1_PRICE);
    return new ShoppingCartDto(shoppingCart);
  }

  private static ShoppingCartDto buildCompletedShoppingCartDto() {
    return buildShoppingCartDto(true);
  }

  private static ShoppingCartDto buildIncompleteShoppingCartDto() {
    return buildShoppingCartDto(false);
  }

  private static OrderDto buildOrderDto(String orderState) {
    OrderDto orderDto = new OrderDto(ORDER_ID, buildCompletedShoppingCartDto(), orderState,
        Optional.empty());
    return orderDto;
  }

  private static OrderDto buildFinalStateOrderDto() {
    return buildOrderDto(OrderState.DONE.name());
  }

  private static OrderDto buildNonFinalStateOrderDto() {
    return buildOrderDto(OrderState.VALIDATING_ITEMS.name());
  }

}
