package cp630oc.paymentsolution.paymentrequeststore.repository;

import cp630oc.paymentsolution.paymentrequeststore.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The customer repository.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}