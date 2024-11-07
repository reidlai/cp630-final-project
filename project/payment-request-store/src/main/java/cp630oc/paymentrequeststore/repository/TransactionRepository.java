package cp630oc.paymentrequeststore.repository;

import cp630oc.paymentrequeststore.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The transaction repository.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}