package cp630oc.paymentsolution.paymentrequeststore.repository;

import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;
import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionRepository;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionRepositoryTest {

    @Mock
    private TransactionRepository transactionRepository;

    private Transaction transaction;
    private Card card;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Setup Customer
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setCards(new ArrayList<>());

        // Setup Card
        card = new Card();
        card.setId(1L);
        card.setCardNumber("4111111111111111");
        card.setCardBrand("VISA");
        card.setCustomer(customer);
        card.setTransactions(new ArrayList<>());

        // Setup Transaction
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionDatetime(new Date());
        transaction.setTransactionAmount(100.00f);
        transaction.setTransactionType("PURCHASE");
        transaction.setMerchantId("MERCH123");
        transaction.setMerchantCity("Toronto");
        transaction.setMerchantState("ON");
        transaction.setMerchantZip("M5V 2T6");
        transaction.setMerchantMccCode("5411");
        transaction.setFraudDetected(false);
        transaction.setCard(card);
        transaction.setTransactionStates(new HashSet<>());
    }

    @Test
    void testFindById() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Optional<Transaction> foundTransaction = transactionRepository.findById(1L);
        
        assertTrue(foundTransaction.isPresent());
        assertEquals(100.00, foundTransaction.get().getTransactionAmount());
        assertEquals("PURCHASE", foundTransaction.get().getTransactionType());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Transaction> foundTransaction = transactionRepository.findById(99L);
        
        assertFalse(foundTransaction.isPresent());
        verify(transactionRepository).findById(99L);
    }

    @Test
    void testSave() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction savedTransaction = transactionRepository.save(transaction);
        
        assertNotNull(savedTransaction);
        assertEquals(100.00, savedTransaction.getTransactionAmount());
        assertEquals("MERCH123", savedTransaction.getMerchantId());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void testDeleteById() {
        doNothing().when(transactionRepository).deleteById(1L);

        transactionRepository.deleteById(1L);
        
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void testFindAll() {
        List<Transaction> transactions = List.of(transaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> foundTransactions = transactionRepository.findAll();
        
        assertFalse(foundTransactions.isEmpty());
        assertEquals(1, foundTransactions.size());
        assertEquals("PURCHASE", foundTransactions.get(0).getTransactionType());
        verify(transactionRepository).findAll();
    }

    @Test
    void testExistsById() {
        when(transactionRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.existsById(99L)).thenReturn(false);

        assertTrue(transactionRepository.existsById(1L));
        assertFalse(transactionRepository.existsById(99L));
        
        verify(transactionRepository).existsById(1L);
        verify(transactionRepository).existsById(99L);
    }

    @Test
    void testCount() {
        when(transactionRepository.count()).thenReturn(1L);

        long count = transactionRepository.count();
        
        assertEquals(1L, count);
        verify(transactionRepository).count();
    }
}