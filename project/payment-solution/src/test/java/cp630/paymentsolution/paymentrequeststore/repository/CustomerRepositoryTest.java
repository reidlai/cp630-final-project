package cp630oc.paymentsolution.paymentrequeststore.repository;

import cp630oc.paymentsolution.paymentrequeststore.entity.Customer;
import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerRepositoryTest {

    @Mock
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setCurrentAge(30);
        customer.setRetirementAge(65);
        customer.setBirthYear(1994);
        customer.setBirthMonth(6);
        customer.setGender("M");
        customer.setAddress("123 Main St");
        customer.setLatitude(43.4643f);
        customer.setLongitude(-80.5204f);
        customer.setPerCapitaIncome(45000.0f);
        customer.setYearlyIncome(90000.0f);
        customer.setTotalDebt(25000.0f);
        customer.setCreditScore(750);
        customer.setCreditCardCount(2);
        customer.setCards(new ArrayList<>());
    }

    @Test
    void testFindById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> foundCustomer = customerRepository.findById(1L);
        
        assertTrue(foundCustomer.isPresent());
        assertEquals("John", foundCustomer.get().getFirstName());
        assertEquals("Doe", foundCustomer.get().getLastName());
        verify(customerRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Customer> foundCustomer = customerRepository.findById(99L);
        
        assertFalse(foundCustomer.isPresent());
        verify(customerRepository).findById(99L);
    }

    @Test
    void testSave() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer savedCustomer = customerRepository.save(customer);
        
        assertNotNull(savedCustomer);
        assertEquals("john.doe@example.com", savedCustomer.getEmail());
        assertEquals(750, savedCustomer.getCreditScore());
        verify(customerRepository).save(customer);
    }

    @Test
    void testDeleteById() {
        doNothing().when(customerRepository).deleteById(1L);

        customerRepository.deleteById(1L);
        
        verify(customerRepository).deleteById(1L);
    }

    @Test
    void testFindAll() {
        List<Customer> customers = List.of(customer);
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> foundCustomers = customerRepository.findAll();
        
        assertFalse(foundCustomers.isEmpty());
        assertEquals(1, foundCustomers.size());
        assertEquals("John", foundCustomers.get(0).getFirstName());
        verify(customerRepository).findAll();
    }

    @Test
    void testExistsById() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.existsById(99L)).thenReturn(false);

        assertTrue(customerRepository.existsById(1L));
        assertFalse(customerRepository.existsById(99L));
        
        verify(customerRepository).existsById(1L);
        verify(customerRepository).existsById(99L);
    }

    @Test
    void testCount() {
        when(customerRepository.count()).thenReturn(1L);

        long count = customerRepository.count();
        
        assertEquals(1L, count);
        verify(customerRepository).count();
    }
}