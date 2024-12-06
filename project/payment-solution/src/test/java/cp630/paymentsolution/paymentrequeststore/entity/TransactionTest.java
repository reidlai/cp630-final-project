package cp630oc.paymentsolution.paymentrequeststore.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionRepository;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionTest {

    private Transaction transaction;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        // Create a transaction
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionAmount(100.0);
        Card card = new Card();
        card.setId(1L);
        card.setCardNumber("4111111111111111");
        card.setCardIndex(0);
        transaction.setCard(card);
    }

    @Test
    void testFindById() {
        // Setup mock to return Optional.of(transaction)
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
    
        // Get the Optional<Transaction> from the repository
        Optional<Transaction> foundTransaction = transactionRepository.findById(1L);
    
        // Assert that the Optional contains a value
        assertTrue(foundTransaction.isPresent());
        
        // Get the actual Transaction object and verify its properties
        Transaction retrievedTransaction = foundTransaction.get();
        assertEquals(100.0, retrievedTransaction.getTransactionAmount());
        assertEquals("4111111111111111", retrievedTransaction.getCard().getCardNumber());
        assertEquals(0, retrievedTransaction.getCard().getCardIndex());
        
        // Verify that the repository method was called
        verify(transactionRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        // Setup mock to return empty Optional
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());
        
        // Get the Optional<Transaction> from the repository
        Optional<Transaction> foundTransaction = transactionRepository.findById(99L);
        
        // Assert that the Optional is empty
        assertFalse(foundTransaction.isPresent());
        
        // Verify that the repository method was called
        verify(transactionRepository).findById(99L);
    }
}