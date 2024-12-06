package cp630oc.paymentsolution.paymentrequeststore.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TransactionStateTest {

    private TransactionState transactionState;
    
    @Mock
    private Transaction mockTransaction;

    @BeforeEach
    void setUp() {
        transactionState = new TransactionState();
        
        // Create and set TransactionStateId
        TransactionStateId stateId = new TransactionStateId();
        stateId.setId(1L);
        stateId.setState("PENDING");
        
        // Initialize the transaction state
        transactionState.setId(stateId);
        transactionState.setTransaction(mockTransaction);
        
        // Set dates
        Date now = new Date();
        transactionState.setCreatedAt(now);
        transactionState.setUpdatedAt(now);
    }

    @Test
    void testTransactionStateId() {
        TransactionStateId stateId = transactionState.getId();
        assertNotNull(stateId);
        assertEquals(1L, stateId.getId());
        assertEquals("PENDING", stateId.getState());
    }

    @Test
    void testTransactionRelationship() {
        assertEquals(mockTransaction, transactionState.getTransaction());
    }

    @Test
    void testCreatedAt() {
        Date testDate = new Date();
        transactionState.setCreatedAt(testDate);
        assertEquals(testDate, transactionState.getCreatedAt());
    }

    @Test
    void testUpdatedAt() {
        Date testDate = new Date();
        transactionState.setUpdatedAt(testDate);
        assertEquals(testDate, transactionState.getUpdatedAt());
    }

    @Test
    void testDeletedAt() {
        assertNull(transactionState.getDeletedAt());
        
        Date testDate = new Date();
        transactionState.setDeletedAt(testDate);
        assertEquals(testDate, transactionState.getDeletedAt());
    }

    @Test
    void testDateSequence() {
        // Create dates with small delays to ensure proper ordering
        Date created = new Date();
        transactionState.setCreatedAt(created);
        
        try {
            Thread.sleep(100); // Small delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Date updated = new Date();
        transactionState.setUpdatedAt(updated);
        
        try {
            Thread.sleep(100); // Small delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Date deleted = new Date();
        transactionState.setDeletedAt(deleted);

        assertNotNull(transactionState.getCreatedAt());
        assertNotNull(transactionState.getUpdatedAt());
        assertNotNull(transactionState.getDeletedAt());
        
        assertTrue(transactionState.getCreatedAt().before(transactionState.getDeletedAt()));
        assertTrue(transactionState.getUpdatedAt().before(transactionState.getDeletedAt()));
    }

    @Test
    void testSetAndGetTransactionStateId() {
        TransactionStateId newStateId = new TransactionStateId();
        newStateId.setId(2L);
        newStateId.setState("COMPLETED");
        
        transactionState.setId(newStateId);
        
        assertEquals(2L, transactionState.getId().getId());
        assertEquals("COMPLETED", transactionState.getId().getState());
    }
}