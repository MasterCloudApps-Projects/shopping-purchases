package es.codeurjc.mca.tfm.purchases.domain.exceptions;

/**
 * Exception class for bad order state update.
 */
public class PreviousOrderStateUpdateException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message exception message.
   */
  public PreviousOrderStateUpdateException(String message) {
    super(message);
  }
}
