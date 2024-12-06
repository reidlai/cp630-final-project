package cp630oc.paymentsolution.paymentrequeststore.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.repository.CardRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardTest {

    private Card card;

    @Mock
    private CardRepository cardRepository;

    /**
     * Set up the test environment.
     */
    @BeforeEach
    void setUp() {
        // Create a customer
        Customer customer = new Customer();
        customer.setId(1L);

        // Create a card
        card = new Card();
        card.setId(1L);
        card.setCardNumber("4111111111111111");
        card.setCardIndex(0);

        // Set the customer 
        card.setCustomer(customer);
    }

    /**
     * Test the card holder name validation.
     */
    @Test
    void testFindByCardNumber() {
        when(cardRepository.findByCardNumber("4111111111111111")).thenReturn(card);
    
        Card foundCard = cardRepository.findByCardNumber("4111111111111111");
    
        assertNotNull(foundCard);
        assertEquals("4111111111111111", foundCard.getCardNumber());
        assertEquals(0, foundCard.getCardIndex());
        assertEquals(1L, foundCard.getCustomer().getId());
    }

}