package cp630oc.paymentsolution.paymentrequeststore.repository;

import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The transaction repository.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
        
    /**
    * Find all transaction by card.
    *
    * @param card the card
    * @return the list of transaction
    */
    List<Transaction> findAllByCard(Card card);
}