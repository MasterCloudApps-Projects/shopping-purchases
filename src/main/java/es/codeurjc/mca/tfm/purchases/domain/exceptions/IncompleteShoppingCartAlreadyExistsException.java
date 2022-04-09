package es.codeurjc.mca.tfm.purchases.domain.exceptions;

/**
 * Exception class for already exists an incomplete shopping cart for user.
 */
public class IncompleteShoppingCartAlreadyExistsException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message exception message.
   */
  public IncompleteShoppingCartAlreadyExistsException(String message) {
    super(message);
  }
}
