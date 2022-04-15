package es.codeurjc.mca.tfm.purchases.domain.exceptions;

/**
 * Exception class for invalid item.
 */
public class InvalidItemException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message exception message.
   */
  public InvalidItemException(String message) {
    super(message);
  }
}
