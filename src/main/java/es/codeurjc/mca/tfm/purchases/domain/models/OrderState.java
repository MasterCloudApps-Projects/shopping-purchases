package es.codeurjc.mca.tfm.purchases.domain.models;

/**
 * Order states enum.
 */
public enum OrderState {
  CREATED(0),
  VALIDATING_ITEMS(1),
  VALIDATING_BALANCE(2),
  DONE(3),
  REJECTED(3);

  public final int weight;

  private OrderState(int weight) {
    this.weight = weight;
  }

}
