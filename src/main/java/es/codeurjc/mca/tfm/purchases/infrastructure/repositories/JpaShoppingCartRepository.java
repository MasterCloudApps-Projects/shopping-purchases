package es.codeurjc.mca.tfm.purchases.infrastructure.repositories;

import es.codeurjc.mca.tfm.purchases.infrastructure.entities.ShoppingCartEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Shopping cart JPA repository interface.
 */
@Repository
public interface JpaShoppingCartRepository extends JpaRepository<ShoppingCartEntity, Long> {

  /**
   * Find shopping cart by user identifier and completed false.
   *
   * @param userId user identifier.
   * @return optional of incomplete shopping cart of passed user, else empty.
   */
  Optional<ShoppingCartEntity> findByUserIdAndCompletedIsFalse(Integer userId);

  /**
   * Find shopping cart by identifier and user identifier.
   *
   * @param id     shopping cart identifier.
   * @param userId user identifier.
   * @return optional of shopping cart with passed id and user, else empty.
   */
  Optional<ShoppingCartEntity> findByIdAndUserId(Long id, Integer userId);

}
