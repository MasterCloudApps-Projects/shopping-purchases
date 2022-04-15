package es.codeurjc.mca.tfm.purchases.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Bad request exception.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message exception message.
   */
  public BadRequestException(String message) {
    super(message);
  }
}
