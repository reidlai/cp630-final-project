package cp630oc.paymentsolution.paymentrequeststore.repository;

import cp630oc.paymentsolution.paymentrequeststore.entity.TransactionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Transaction state repository.
 */
@Repository
public interface TransactionStateRepository extends JpaRepository<TransactionState, Long> {

    /**
     * Find the latest transaction state by transaction ID.
     *
     * @param id the transaction ID
     * @return the latest transaction state
     */
    @Query("SELECT ts FROM TransactionState ts WHERE ts.id.id = :id AND ts.deletedAt IS NULL ORDER BY ts.createdAt DESC")
    TransactionState findLatestStateById(@Param("id") Long id);
}