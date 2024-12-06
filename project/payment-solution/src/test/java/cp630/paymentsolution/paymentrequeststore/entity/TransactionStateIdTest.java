package cp630oc.paymentsolution.paymentrequeststore.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TransactionStateIdTest {

    private TransactionStateId transactionStateId;

    @BeforeEach
    void setUp() {
        transactionStateId = new TransactionStateId(1L, "PENDING");
    }

    @Test
    void testDefaultConstructor() {
        TransactionStateId emptyStateId = new TransactionStateId();
        assertNull(emptyStateId.getId());
        assertNull(emptyStateId.getState());
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals(1L, transactionStateId.getId());
        assertEquals("PENDING", transactionStateId.getState());
    }

    @Test
    void testSetAndGetId() {
        TransactionStateId stateId = new TransactionStateId();
        stateId.setId(2L);
        assertEquals(2L, stateId.getId());
    }

    @Test
    void testSetAndGetState() {
        TransactionStateId stateId = new TransactionStateId();
        stateId.setState("COMPLETED");
        assertEquals("COMPLETED", stateId.getState());
    }

    @Test
    void testEqualsWithSameObject() {
        assertEquals(transactionStateId, transactionStateId);
    }

    @Test
    void testEqualsWithDifferentObject() {
        TransactionStateId stateId1 = new TransactionStateId(1L, "PENDING");
        TransactionStateId stateId2 = new TransactionStateId(1L, "PENDING");
        assertEquals(stateId1, stateId2);
    }

    @Test
    void testEqualsWithDifferentValues() {
        TransactionStateId stateId1 = new TransactionStateId(1L, "PENDING");
        TransactionStateId stateId2 = new TransactionStateId(2L, "COMPLETED");
        assertNotEquals(stateId1, stateId2);
    }

    @Test
    void testEqualsWithNull() {
        assertFalse(transactionStateId.equals(null));
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertFalse(transactionStateId.equals(new Object()));
    }

    @Test
    void testHashCodeConsistency() {
        TransactionStateId stateId1 = new TransactionStateId(1L, "PENDING");
        TransactionStateId stateId2 = new TransactionStateId(1L, "PENDING");
        assertEquals(stateId1.hashCode(), stateId2.hashCode());
    }

    @Test
    void testHashCodeWithDifferentValues() {
        TransactionStateId stateId1 = new TransactionStateId(1L, "PENDING");
        TransactionStateId stateId2 = new TransactionStateId(2L, "COMPLETED");
        assertNotEquals(stateId1.hashCode(), stateId2.hashCode());
    }
}