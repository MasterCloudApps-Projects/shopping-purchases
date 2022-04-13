package es.codeurjc.mca.tfm.purchases.domain.exceptions;

/**
 * Exception class for incomplete shopping cart.
 */
public class IllegalShoppingCartStateException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message exception message.
   */
  public IllegalShoppingCartStateException(String message) {
    super(message);
  }
}
