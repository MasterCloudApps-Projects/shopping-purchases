package es.codeurjc.mca.tfm.purchases.domain.models;

/**
 * Order states enum.
 */
public enum OrderState {
  VALIDATING_ITEMS,
  VALIDATING_USER_CREDIT,
  DONE,
  REJECTED
}
