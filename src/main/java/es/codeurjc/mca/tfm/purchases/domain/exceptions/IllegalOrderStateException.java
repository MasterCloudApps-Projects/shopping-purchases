package es.codeurjc.mca.tfm.purchases.domain.exceptions;

/**
 * Exception class for illegal order state.
 */
public class IllegalOrderStateException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message exception message.
   */
  public IllegalOrderStateException(String message) {
    super(message);
  }
}
