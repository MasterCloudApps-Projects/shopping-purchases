package es.codeurjc.mca.tfm.purchases.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Conflict exception.
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message exception message.
   */
  public ConflictException(String message) {
    super(message);
  }
}
