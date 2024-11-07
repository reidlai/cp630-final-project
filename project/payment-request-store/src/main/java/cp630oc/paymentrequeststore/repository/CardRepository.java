package cp630oc.paymentrequeststore.repository;

import cp630oc.paymentrequeststore.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The card repository.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
}