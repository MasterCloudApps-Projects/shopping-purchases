package es.codeurjc.mca.tfm.purchases.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Not found exception.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message exception message.
   */
  public NotFoundException(String message) {
    super(message);
  }
}
