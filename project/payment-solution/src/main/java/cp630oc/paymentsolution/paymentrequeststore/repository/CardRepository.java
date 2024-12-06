package cp630oc.paymentsolution.paymentrequeststore.repository;

import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The card repository.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
      
      /**
      * Find a card by the card number.
      *
      * @param cardNumber the card number
      * @return the card
      */
      Card findByCardNumber(String cardNumber);
      
}