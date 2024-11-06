package cp630oc.paymentrequeststore.repository;

import cp630oc.paymentrequeststore.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Find by card
    List<Transaction> findByCardId(Long cardId);
    
    // Find by date range
    List<Transaction> findByTransactionDatetimeBetween(Date startDate, Date endDate);
    
    // Find by transaction type
    List<Transaction> findByTransactionType(String transactionType);
    
    // Find by merchant details
    List<Transaction> findByMerchantId(String merchantId);
    List<Transaction> findByMerchantCity(String merchantCity);
    List<Transaction> findByMerchantState(String merchantState);
    List<Transaction> findByMerchantMccCode(String mccCode);
    
    // Find fraudulent transactions
    List<Transaction> findByFraudDetectedTrue();
    
    // Find by status
    List<Transaction> findByStatus(String status);
    
    // Find by amount range
    List<Transaction> findByTransactionAmountBetween(double minAmount, double maxAmount);
    
    // Find by currency
    List<Transaction> findByCurrencyCode(String currencyCode);
    
    // Custom queries for transaction analysis
    @Query("SELECT t FROM Transaction t WHERE t.card.id = :cardId AND t.transactionAmount > :amount")
    List<Transaction> findLargeTransactionsByCard(@Param("cardId") Long cardId, @Param("amount") double amount);
    
    // Find transactions with errors
    List<Transaction> findByTransactionErrorIsNotNull();
    
    // Aggregate queries
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.card.id = :cardId AND t.fraudDetected = true")
    long countFraudulentTransactionsByCard(@Param("cardId") Long cardId);
    
    @Query("SELECT SUM(t.transactionAmount) FROM Transaction t WHERE t.card.id = :cardId AND t.status = 'COMPLETED'")
    Double getTotalTransactionAmountByCard(@Param("cardId") Long cardId);
    
    // Find recent transactions
    List<Transaction> findByCardIdOrderByTransactionDatetimeDesc(Long cardId);
    
    // Find transactions by location
    List<Transaction> findByMerchantCityAndMerchantState(String city, String state);
}