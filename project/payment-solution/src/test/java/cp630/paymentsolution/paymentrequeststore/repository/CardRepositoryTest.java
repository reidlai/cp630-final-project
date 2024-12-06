package cp630oc.paymentsolution.paymentrequeststore.repository;

import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardRepositoryTest {

    @Mock
    private CardRepository cardRepository;

    private Card card;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Setup Customer
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setCards(new ArrayList<>());

        // Setup Card
        card = new Card();
        card.setId(1L);
        card.setCardNumber("4111111111111111");
        card.setCardBrand("VISA");
        card.setCardType("CREDIT");
        card.setCardExpirationDate(new Date());
        card.setCvvCode("123");
        card.setCardIndex(0);
        card.setHasChip(true);
        card.setCreditLimit(5000.0f);
        card.setCustomer(customer);
        card.setNumberCardsIssued(1);
        card.setPinLastChangedYear(2024);
        card.setAccountOpenDate(new Date());
        card.setCardOnDarkWeb(false);
        card.setTransactions(new ArrayList<>());
    }

    @Test
    void testFindById() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        Optional<Card> foundCard = cardRepository.findById(1L);
        
        assertTrue(foundCard.isPresent());
        assertEquals("4111111111111111", foundCard.get().getCardNumber());
        assertEquals("VISA", foundCard.get().getCardBrand());
        verify(cardRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Card> foundCard = cardRepository.findById(99L);
        
        assertFalse(foundCard.isPresent());
        verify(cardRepository).findById(99L);
    }

    @Test
    void testFindByCardNumber() {
        when(cardRepository.findByCardNumber("4111111111111111")).thenReturn(card);

        Card foundCard = cardRepository.findByCardNumber("4111111111111111");
        
        assertNotNull(foundCard);
        assertEquals("VISA", foundCard.getCardBrand());
        assertEquals("CREDIT", foundCard.getCardType());
        verify(cardRepository).findByCardNumber("4111111111111111");
    }

    @Test
    void testSave() {
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card savedCard = cardRepository.save(card);
        
        assertNotNull(savedCard);
        assertEquals("4111111111111111", savedCard.getCardNumber());
        assertEquals(5000.0f, savedCard.getCreditLimit());
        verify(cardRepository).save(card);
    }

    @Test
    void testDeleteById() {
        doNothing().when(cardRepository).deleteById(1L);

        cardRepository.deleteById(1L);
        
        verify(cardRepository).deleteById(1L);
    }

    @Test
    void testExistsById() {
        when(cardRepository.existsById(1L)).thenReturn(true);
        when(cardRepository.existsById(99L)).thenReturn(false);

        assertTrue(cardRepository.existsById(1L));
        assertFalse(cardRepository.existsById(99L));
        
        verify(cardRepository).existsById(1L);
        verify(cardRepository).existsById(99L);
    }
}