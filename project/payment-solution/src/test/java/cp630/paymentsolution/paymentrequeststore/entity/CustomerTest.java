package cp630oc.paymentsolution.paymentrequeststore.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cp630oc.paymentsolution.paymentrequeststore.entity.Customer;
import cp630oc.paymentsolution.paymentrequeststore.repository.CustomerRepository;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerTest {
  
    private Customer customer;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
    }

    @Test
    void testFindById() {
        // Setup mock to return Optional.of(customer)
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        
        // Get the Optional<Customer> from the repository
        Optional<Customer> foundCustomer = customerRepository.findById(1L);
        
        // Assert that the Optional contains a value
        assertTrue(foundCustomer.isPresent());
        
        // Get the actual Customer object and verify its properties
        Customer retrievedCustomer = foundCustomer.get();
        assertEquals(1L, retrievedCustomer.getId());
        assertEquals("John", retrievedCustomer.getFirstName());
        assertEquals("Doe", retrievedCustomer.getLastName());
        
        // Verify that the repository method was called
        verify(customerRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        // Setup mock to return empty Optional
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        
        // Get the Optional<Customer> from the repository
        Optional<Customer> foundCustomer = customerRepository.findById(99L);
        
        // Assert that the Optional is empty
        assertFalse(foundCustomer.isPresent());
        
        // Verify that the repository method was called
        verify(customerRepository).findById(99L);
    }
}