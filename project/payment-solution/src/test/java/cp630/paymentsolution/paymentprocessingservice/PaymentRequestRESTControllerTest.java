package cp630oc.paymentsolution.paymentprocessingservice;

import cp630oc.paymentsolution.modelinferenceservice.ModelInferenceService;
import cp630oc.paymentsolution.paymentprocessingservice.PaymentRequestRESTController;
import cp630oc.paymentsolution.paymentprocessingservice.model.CreatePaymentRequestRequest;
import cp630oc.paymentsolution.paymentprocessingservice.model.CreatePaymentRequestResponse;
import cp630oc.paymentsolution.paymentrequeststore.entity.Customer;
import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;
import cp630oc.paymentsolution.paymentrequeststore.entity.TransactionState;
import cp630oc.paymentsolution.paymentrequeststore.entity.TransactionStateId;
import cp630oc.paymentsolution.paymentrequeststore.repository.CardRepository;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionRepository;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionStateRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ch.qos.logback.core.model.Model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentRequestRESTControllerTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionStateRepository transactionStateRepository;

    @Mock
    private ModelInferenceService modelInferenceService;

    @InjectMocks
    private PaymentRequestRESTController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPaymentRequest_Success() {
        // Arrange
        CreatePaymentRequestRequest request = new CreatePaymentRequestRequest();
        request.setCardNumber("1234567890123456");
        request.setTransactionAmount(100.0f);
        request.setTransactionDatetime(OffsetDateTime.now(ZoneOffset.UTC));
        request.setMerchantCity("Test City");
        request.setMerchantState("TS");
        request.setMerchantZip("12345");
        request.setMerchantMccCode("1234");

        Customer customer = new Customer();
        customer.setId(1L);

        Card mockCard = new Card();
        mockCard.setId(1L);
        mockCard.setCardNumber("1234567890123456");
        mockCard.setCustomer(customer);
        mockCard.setCardIndex(0);

        Transaction mockTransaction = new Transaction();
        mockTransaction.setId(1L);
        mockTransaction.setCard(mockCard);
        mockTransaction.setTransactionDatetime(new Date());

        TransactionState mockTransactionState = new TransactionState();
        mockTransactionState.setId(new TransactionStateId(1L, "PENDING"));
        mockTransactionState.setCreatedAt(new Date());
        mockTransactionState.setTransaction(mockTransaction);

        // Mock

        when(cardRepository.findByCardNumber(request.getCardNumber())).thenReturn(mockCard);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);
        when(transactionStateRepository.save(any(TransactionState.class))).thenReturn(mockTransactionState);
        when(modelInferenceService.detectFraud(mockCard, mockTransaction, false)).thenReturn(false);

        // Act
        Optional<String> xAuthorization = Optional.of("your-auth-token"); 
        Optional<String> xApiKey = Optional.of("your-api-key"); 
        Optional<String> xNotification = Optional.ofNullable(null);
        ResponseEntity<CreatePaymentRequestResponse> response = controller.createPaymentRequest(request, xAuthorization, xApiKey, xNotification);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), response.getBody().getTransactionError());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getTransactionId());
        assertEquals("ACCEPTED", response.getBody().getTransactionStatus());
    }

    @Test
    void createPaymentRequest_CardNotFound() {
        // Arrange
        CreatePaymentRequestRequest request = new CreatePaymentRequestRequest();
        request.setCardNumber("nonexistent");

        when(cardRepository.findByCardNumber(request.getCardNumber())).thenReturn(null);

        // Act
        Optional<String> xAuthorization = Optional.of("your-auth-token"); 
        Optional<String> xApiKey = Optional.of("your-api-key"); 
        Optional<String> xNotification = Optional.ofNullable(null);
        ResponseEntity<CreatePaymentRequestResponse> response = controller.createPaymentRequest(request, xAuthorization, xApiKey, xNotification);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FAILED", response.getBody().getTransactionStatus());
        assertEquals("Failed to fetch card: Card number not found", response.getBody().getTransactionError());
    }

    @Test
    void createPaymentRequest_TransactionError() {
        // Arrange
        CreatePaymentRequestRequest request = new CreatePaymentRequestRequest();
        request.setCardNumber("1234567890123456");

        Card mockCard = new Card();
        when(cardRepository.findByCardNumber(request.getCardNumber())).thenReturn(mockCard);
        when(transactionRepository.save(any(Transaction.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        Optional<String> xAuthorization = Optional.of("your-auth-token"); 
        Optional<String> xApiKey = Optional.of("your-api-key"); 
        Optional<String> xNotification = Optional.ofNullable(null);
        ResponseEntity<CreatePaymentRequestResponse> response = controller.createPaymentRequest(request, xAuthorization, xApiKey, xNotification);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FAILED", response.getBody().getTransactionStatus());
        assertTrue(response.getBody().getTransactionError().contains("Failed to create transaction"));
    }
}