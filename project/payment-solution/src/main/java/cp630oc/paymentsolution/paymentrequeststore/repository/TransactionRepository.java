package cp630oc.paymentsolution.paymentrequeststore.repository;

import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Find all transaction by card number.
     * 
     * @param cardNumber the card number
     * @return the list of transaction
     */
    @Query(value = "SELECT tx.* FROM cards cd, transactions tx " +
           "WHERE cd.card_number = :cardNumber " +
           "AND cd.customer_id = tx.customer_id AND cd.card_index = tx.card_index " +
           "AND EXISTS (SELECT 1 FROM transaction_states ts WHERE tx.id = ts.id)", 
           nativeQuery = true)
    List<Transaction> findAllByCardNumber(@Param("cardNumber") String cardNumber);
}