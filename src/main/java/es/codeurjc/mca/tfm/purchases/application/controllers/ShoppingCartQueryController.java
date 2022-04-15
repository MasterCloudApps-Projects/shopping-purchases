package es.codeurjc.mca.tfm.purchases.application.controllers;

import es.codeurjc.mca.tfm.purchases.application.dtos.responses.ShoppingCartResponseDto;
import es.codeurjc.mca.tfm.purchases.application.exceptions.InternalServerErrorException;
import es.codeurjc.mca.tfm.purchases.application.exceptions.NotFoundException;
import es.codeurjc.mca.tfm.purchases.application.mappers.ApplicationShoppingCartMapper;
import es.codeurjc.mca.tfm.purchases.domain.dtos.ShoppingCartDto;
import es.codeurjc.mca.tfm.purchases.domain.ports.in.ShoppingCartUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Shopping cart query controller.
 */
@RestController
@RequestMapping("/api/v1/shopping-carts")
@Slf4j
public class ShoppingCartQueryController {

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
  public ShoppingCartQueryController(
      ApplicationShoppingCartMapper applicationShoppingCartMapper,
      ShoppingCartUseCase shoppingCartUseCase) {
    this.applicationShoppingCartMapper = applicationShoppingCartMapper;
    this.shoppingCartUseCase = shoppingCartUseCase;
  }

  /**
   * Get a shopping cart by passed identifier.
   *
   * @param id             shopping cart identifier.
   * @param authentication authenticated user info.
   * @return accepted code with Location header if request finish successfully.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ShoppingCartResponseDto> getShoppingCart(@PathVariable(name = "id") Long id,
      Authentication authentication) {
    Integer userId = null;
    try {
      userId = Integer.valueOf(authentication.getName());
      ShoppingCartDto shoppingCartDto = this.shoppingCartUseCase.get(id, userId)
          .orElseThrow(
              () -> new NotFoundException("Shopping cart not found with id for logged user"));

      return ResponseEntity.ok(this.applicationShoppingCartMapper.map(shoppingCartDto));
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
