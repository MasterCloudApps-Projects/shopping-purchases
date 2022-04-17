package es.codeurjc.mca.tfm.purchases.application.controllers;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.mca.tfm.purchases.application.dtos.requests.SetItemRequest;
import es.codeurjc.mca.tfm.purchases.application.dtos.responses.ShoppingCartResponseDto;
import es.codeurjc.mca.tfm.purchases.application.exceptions.BadRequestException;
import es.codeurjc.mca.tfm.purchases.application.exceptions.ConflictException;
import es.codeurjc.mca.tfm.purchases.application.exceptions.InternalServerErrorException;
import es.codeurjc.mca.tfm.purchases.application.exceptions.NotFoundException;
import es.codeurjc.mca.tfm.purchases.application.mappers.ApplicationShoppingCartMapper;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IllegalShoppingCartStateException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IncompleteShoppingCartAlreadyExistsException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.InvalidItemException;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.ShoppingCartUseCase;
import java.net.URI;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Shopping cart command controller.
 */
@RestController
@RequestMapping("/api/v1/shopping-carts")
@Slf4j
public class ShoppingCartCommandController {

  /**
   * Shopping cart mapper.
   */
  private final ApplicationShoppingCartMapper applicationShoppingCartMapper;

  /**
   * Shopping cart use case.
   */
  private final ShoppingCartUseCase shoppingCartUseCase;

  /**
   * Constructor.
   *
   * @param applicationShoppingCartMapper shopping cart mapper.
   * @param shoppingCartUseCase           shopping cart use case.
   */
  public ShoppingCartCommandController(
      ApplicationShoppingCartMapper applicationShoppingCartMapper,
      ShoppingCartUseCase shoppingCartUseCase) {
    this.applicationShoppingCartMapper = applicationShoppingCartMapper;
    this.shoppingCartUseCase = shoppingCartUseCase;
  }

  /**
   * Create a shopping cart for authenticated user.
   *
   * @param authentication authenticated user info.
   * @return accepted code with Location header if request finish successfully.
   */
  @PostMapping
  public ResponseEntity<Void> createShoppingCart(
      Authentication authentication) {
    Integer userId = null;
    try {
      userId = Integer.valueOf(authentication.getName());
      ShoppingCartDto shoppingCartDto = this.shoppingCartUseCase.create(userId);

      URI location = fromCurrentRequest().path("/{id}")
          .buildAndExpand(shoppingCartDto.getId()).toUri();

      return ResponseEntity.accepted().location(location).build();
    } catch (IncompleteShoppingCartAlreadyExistsException iscae) {
      log.error("Already exists an incomplete shopping cart for user {}. Can't create new one",
          userId);
      throw new ConflictException(iscae.getMessage());
    } catch (Exception e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw new InternalServerErrorException(e.getMessage());
    }
  }

  /**
   * Deletes a shopping cart.
   *
   * @param id             shopping cart identifier.
   * @param authentication authenticated user info.
   * @return accepted code with shopping cart to delete info.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<ShoppingCartResponseDto> deleteShoppingCart(
      @PathVariable(name = "id") Long id,
      Authentication authentication) {
    Integer userId = null;
    try {
      userId = Integer.valueOf(authentication.getName());
      ShoppingCartDto shoppingCartDto = this.shoppingCartUseCase.delete(id, userId)
          .orElseThrow(
              () -> new NotFoundException(
                  "Shopping cart with passed id not found for logged user"));

      return ResponseEntity.accepted()
          .body(this.applicationShoppingCartMapper.map(shoppingCartDto));
    } catch (IllegalShoppingCartStateException illegalShoppingCartStateException) {
      log.error("Shopping cart with id {} and user {} is completed and can't be deleted", id,
          userId);
      throw new ConflictException(illegalShoppingCartStateException.getMessage());
    } catch (NotFoundException notFoundException) {
      log.error("Shopping cart not found with id and user", id, userId);
      throw notFoundException;
    } catch (Exception e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw new InternalServerErrorException(e.getMessage());
    }
  }

  /**
   * Completes a shopping cart.
   *
   * @param id             shopping cart identifier.
   * @param authentication authenticated user info.
   * @return accepted code response.
   */
  @PatchMapping("/{id}")
  public ResponseEntity<Void> completeShoppingCart(
      @PathVariable(name = "id") Long id,
      Authentication authentication) {
    Integer userId = null;
    try {
      userId = Integer.valueOf(authentication.getName());
      this.shoppingCartUseCase.complete(id, userId).orElseThrow(
          () -> new NotFoundException("Shopping cart with passed id not found for logged user"));

      return ResponseEntity.accepted().build();
    } catch (IllegalShoppingCartStateException illegalShoppingCartStateException) {
      log.error("Shopping cart with id {} and user {} can't be completed", id,
          userId);
      throw new ConflictException(illegalShoppingCartStateException.getMessage());
    } catch (NotFoundException notFoundException) {
      log.error("Shopping cart not found with id and user", id, userId);
      throw notFoundException;
    } catch (Exception e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw new InternalServerErrorException(e.getMessage());
    }
  }

  /**
   * Sets item in shopping cart.
   *
   * @param id             shopping cart identifier.
   * @param productId      product identifier.
   * @param setItemRequest set item info.
   * @param authentication authenticated user info.
   * @return accepted code response.
   */
  @PatchMapping("/{id}/products/{productId}")
  public ResponseEntity<Void> setItemToShoppingCart(
      @PathVariable(name = "id") Long id, @PathVariable(name = "productId") Integer productId,
      @Valid @RequestBody SetItemRequest setItemRequest, Authentication authentication) {
    Integer userId = null;
    try {
      userId = Integer.valueOf(authentication.getName());
      this.shoppingCartUseCase.setItem(
              id, userId, productId, setItemRequest.getUnitPrice(), setItemRequest.getQuantity())
          .orElseThrow(
              () -> new NotFoundException(
                  "Shopping cart with passed id not found for logged user"));

      return ResponseEntity.accepted().build();
    } catch (IllegalShoppingCartStateException illegalShoppingCartStateException) {
      log.error("Shopping cart with id {} and user {} is completed and can't set items on it",
          id, userId);
      throw new ConflictException(illegalShoppingCartStateException.getMessage());
    } catch (InvalidItemException invalidItemException) {
      log.error("Can't set item with id {} and body {} to shopping cart with id {} and user {}",
          productId, setItemRequest, id, userId);
      throw new BadRequestException(invalidItemException.getMessage());
    } catch (NotFoundException notFoundException) {
      log.error("Shopping cart not found with id and user", id, userId);
      throw notFoundException;
    } catch (Exception e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw new InternalServerErrorException(e.getMessage());
    }
  }

  /**
   * Deletes item from shopping cart.
   *
   * @param id             shopping cart identifier.
   * @param productId      product identifier.
   * @param authentication authenticated user info.
   * @return accepted code response.
   */
  @DeleteMapping("/{id}/products/{productId}")
  public ResponseEntity<Void> deleteItemFromShoppingCart(
      @PathVariable(name = "id") Long id, @PathVariable(name = "productId") Integer productId,
      Authentication authentication) {
    Integer userId = null;
    try {
      userId = Integer.valueOf(authentication.getName());
      this.shoppingCartUseCase.deleteItem(id, userId, productId).orElseThrow(
          () -> new NotFoundException("Shopping cart with passed id not found for logged user"));

      return ResponseEntity.accepted().build();
    } catch (IllegalShoppingCartStateException illegalShoppingCartStateException) {
      log.error("Shopping cart with id {} and user {} is completed and can't delete items from it",
          id, userId);
      throw new ConflictException(illegalShoppingCartStateException.getMessage());
    } catch (NotFoundException notFoundException) {
      log.error("Shopping cart not found with id and user", id, userId);
      throw notFoundException;
    } catch (Exception e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw new InternalServerErrorException(e.getMessage());
    }
  }

}
