package cp630oc.paymentrequeststore.repository;

import cp630oc.paymentrequeststore.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The customer repository.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}