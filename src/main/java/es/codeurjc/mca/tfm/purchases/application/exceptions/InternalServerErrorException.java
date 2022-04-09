package es.codeurjc.mca.tfm.purchases.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Internal server error exception.
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message exception message.
   */
  public InternalServerErrorException(String message) {
    super(message);
  }
}
