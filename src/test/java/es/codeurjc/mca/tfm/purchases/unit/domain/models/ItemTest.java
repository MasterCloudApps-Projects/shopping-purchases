package es.codeurjc.mca.tfm.purchases.unit.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import es.codeurjc.mca.tfm.purchases.domain.models.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Tag("UnitTest")
@DisplayName("Item tests")
public class ItemTest {

  private static final Integer PRODUCT_1_ID = 100;

  private static final Double PRODUCT_1_PRICE = 19.99;

  @Test
  @DisplayName("Test update method")
  public void givenAUnitPriceAndQuantityWhenUpdateThenShouldUpdateUnitPriceQuantityAndTotalPrice() {
    Item item = new Item(PRODUCT_1_ID, 1.0, 1);
    item.update(PRODUCT_1_PRICE, 3);

    assertEquals(PRODUCT_1_PRICE, item.getUnitPrice());
    assertEquals(3, item.getQuantity());
    assertEquals(PRODUCT_1_PRICE * 3, item.getTotalPrice());
  }

}
