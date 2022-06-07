package es.codeurjc.mca.tfm.purchases.application.exceptions;

import es.codeurjc.mca.tfm.purchases.domain.exceptions.IllegalShoppingCartStateException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.IncompleteShoppingCartAlreadyExistsException;
import es.codeurjc.mca.tfm.purchases.domain.exceptions.InvalidItemException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Exception handler.
 */
@ControllerAdvice
@Slf4j
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

  private static final String ERROR_FIELD = "error";

  /**
   * Handles IncompleteShoppingCartAlreadyExistsException.
   *
   * @param ex IncompleteShoppingCartAlreadyExistsException.
   * @return ResponseEntity with an error message and conflict status code.
   */
  @ExceptionHandler(IncompleteShoppingCartAlreadyExistsException.class)
  public ResponseEntity<Object> handleIncompleteShoppingCartAlreadyExistsException(
      IncompleteShoppingCartAlreadyExistsException ex) {
    return this.handle(ex.getMessage(), HttpStatus.CONFLICT);
  }

  /**
   * Handles IllegalShoppingCartStateException.
   *
   * @param ex IllegalShoppingCartStateException.
   * @return ResponseEntity with an error message and conflict status code.
   */
  @ExceptionHandler(IllegalShoppingCartStateException.class)
  public ResponseEntity<Object> handleIllegalShoppingCartStateException(
      IllegalShoppingCartStateException ex) {
    return this.handle(ex.getMessage(), HttpStatus.CONFLICT);
  }

  /**
   * Handles NotFoundException.
   *
   * @param ex NotFoundException.
   * @return ResponseEntity with an error message and not found status code.
   */
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
    return this.handle(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  /**
   * Handles InvalidItemException.
   *
   * @param ex InvalidItemException.
   * @return ResponseEntity with an error message and bad request status code.
   */
  @ExceptionHandler(InvalidItemException.class)
  public ResponseEntity<Object> handleInvalidItemException(InvalidItemException ex) {
    return this.handle(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles Exception.
   *
   * @param ex Exception.
   * @return ResponseEntity with an error message and internal server error status code.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(
      Exception ex) {
    String exceptionMessage = ex.getMessage() != null ? ex.getMessage() : "Internal error.";
    ex.printStackTrace();
    return this.handle(exceptionMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Function with common exception handling behaviour.
   *
   * @param errorMessage message to return in ResponseEntity.
   * @param httpStatus   http status code to return in ResponseEntity.
   * @return ResponseEntity with an error message and a status code.
   */
  private ResponseEntity<Object> handle(String errorMessage, HttpStatus httpStatus) {
    log.error(errorMessage);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put(ERROR_FIELD, errorMessage);

    return new ResponseEntity<>(body, httpStatus);
  }

}
