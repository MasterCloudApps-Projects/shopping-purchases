package es.codeurjc.mca.tfm.purchases.application.controllers;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.mca.tfm.purchases.application.exceptions.ConflictException;
import es.codeurjc.mca.tfm.purchases.application.exceptions.InternalServerErrorException;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IncompleteShoppingCartAlreadyExistsException;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.ShoppingCartUseCase;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
   * Shopping cart use case.
   */
  private final ShoppingCartUseCase shoppingCartUseCase;

  /**
   * Constructor.
   *
   * @param shoppingCartUseCase shopping cart use case.
   */
  public ShoppingCartCommandController(
      ShoppingCartUseCase shoppingCartUseCase) {
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

}
