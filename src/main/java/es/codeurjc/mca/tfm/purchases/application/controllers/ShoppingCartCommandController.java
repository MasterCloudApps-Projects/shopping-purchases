package es.codeurjc.mca.tfm.purchases.application.controllers;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.mca.tfm.purchases.application.dtos.ShoppingCartResponseDto;
import es.codeurjc.mca.tfm.purchases.application.exceptions.ConflictException;
import es.codeurjc.mca.tfm.purchases.application.exceptions.InternalServerErrorException;
import es.codeurjc.mca.tfm.purchases.application.exceptions.NotFoundException;
import es.codeurjc.mca.tfm.purchases.application.mappers.ApplicationShoppingCartMapper;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IllegalShoppingCartStateException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IncompleteShoppingCartAlreadyExistsException;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.ShoppingCartUseCase;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

}
