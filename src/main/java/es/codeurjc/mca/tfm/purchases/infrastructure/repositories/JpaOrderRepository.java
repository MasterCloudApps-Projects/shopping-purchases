package es.codeurjc.mca.tfm.purchases.infrastructure.repositories;

import es.codeurjc.mca.tfm.purchases.infrastructure.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Order JPA repository interface.
 */
@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {

}
