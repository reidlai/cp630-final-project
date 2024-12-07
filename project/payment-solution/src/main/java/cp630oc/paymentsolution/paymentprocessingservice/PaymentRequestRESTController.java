package cp630oc.paymentsolution.paymentprocessingservice;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cp630oc.paymentsolution.paymentprocessingservice.api.PaymentRequestApi;
import cp630oc.paymentsolution.paymentprocessingservice.model.CreatePaymentRequestRequest;
import cp630oc.paymentsolution.paymentprocessingservice.model.CreatePaymentRequestResponse;

import cp630oc.paymentsolution.paymentrequeststore.repository.CardRepository;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionRepository;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionStateRepository;
import jakarta.validation.Valid;
import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;
import cp630oc.paymentsolution.paymentrequeststore.entity.TransactionState;
import cp630oc.paymentsolution.paymentrequeststore.entity.TransactionStateId;

import cp630oc.paymentsolution.modelinferenceservice.ModelInferenceService;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The payment request REST controller which implements the PaymentRequestApi interface generated 
 * by OpenAPI generator.
 */
@RestController
public class PaymentRequestRESTController implements PaymentRequestApi {

    boolean notificationEnabled = false;

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ModelInferenceService modelInferenceService;
    
    @Autowired
    private TransactionStateRepository transactionStateRepository;

    /**
     * Create a payment request matched with operationId in payment-solution-apis.yaml.
     */
    @Override
    public ResponseEntity<CreatePaymentRequestResponse> createPaymentRequest(
        @Valid CreatePaymentRequestRequest request, Optional<String> xAuthorization,
        Optional<String> xApiKey, Optional<String> xNotification) {
        if (xNotification.isPresent()) {
            if (xNotification.get().equals("true")) {
                this.notificationEnabled = true;
            } 
        }
        try {

            // Fetch card
            Card card = fetchCard(request);

            // Create transaction
            Transaction savedTransaction = createTransaction(card, request);

            boolean fraudDetected = modelInferenceService.detectFraud(card, savedTransaction, notificationEnabled);

            // Create response
            CreatePaymentRequestResponse response = new CreatePaymentRequestResponse();

            // Set transaction ID
            response.setTransactionId(Long.toString(savedTransaction.getId()));

            // Set transaction timestamp
            Date transactionDatetime = savedTransaction.getTransactionDatetime();
            OffsetDateTime transactionTimestamp = Instant.ofEpochMilli(transactionDatetime.getTime())
                                                .atOffset(ZoneOffset.UTC);
            response.setTransactionTimestamp(transactionTimestamp);

            // Set transaction status
            if (fraudDetected) {
                response.setTransactionStatus("CANCELLED");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } 

            response.setTransactionStatus("ACCEPTED");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            CreatePaymentRequestResponse errorResponse = new CreatePaymentRequestResponse();
            errorResponse.setTransactionStatus("FAILED");
            errorResponse.setTransactionError(e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        }
    }

    /**
     * Create a transaction.
     * @param card
     * @param request
     * @return
     * @throws Exception
     */
    private Transaction createTransaction(Card card, CreatePaymentRequestRequest request) throws Exception {

        try {
            Transaction transaction = new Transaction();

            // Set transaction date and time
            OffsetDateTime transactionTimestamp = request.getTransactionDatetime();
            transaction.setTransactionDatetime(Date.from(transactionTimestamp.toInstant()));

            // Set transaction amount
            transaction.setTransactionAmount(request.getTransactionAmount());
            
            // Set merchant information
            transaction.setMerchantCity(request.getMerchantCity());
            transaction.setMerchantState(request.getMerchantState());
            transaction.setMerchantZip(request.getMerchantZip());
            transaction.setMerchantMccCode(request.getMerchantMccCode());
            
            // Set transaction error
            transaction.setTransactionError(null);
            
            // Set fraud detection
            transaction.setFraudDetected(false);
            
            // Set card
            transaction.setCard(card);

            // Save transaction
            Transaction savedTransaction = transactionRepository.save(transaction);

            // Create initial transaction state
            TransactionState transactionState = new TransactionState();
            transactionState.setCreatedAt(new Date());
            transactionState.setUpdatedAt(new Date()); 
            TransactionStateId stateId = new TransactionStateId(savedTransaction.getId(), "pending");
            transactionState.setId(stateId);
            transactionState.setTransaction(savedTransaction);
            
            TransactionState savedTransactionState = transactionStateRepository.save(transactionState);

            Set<TransactionState> transactionStates = new HashSet<>();
            transactionStates.add(savedTransactionState);

            savedTransaction.setTransactionStates(transactionStates);

            return savedTransaction;
            
        } catch (Exception e) {
            throw new Exception("Failed to create transaction: " + e.getMessage());
        }
    }

    /**
     * Fetch card by card number.
     * @param request
     * @return
     * @throws Exception
     */
    private Card fetchCard(CreatePaymentRequestRequest request) throws Exception {
        try {
            Card card = cardRepository.findByCardNumber(request.getCardNumber());
            if (card == null) {
                throw new Exception("Card number not found");
            }
            return card;
        } catch (Exception e) {
            throw new Exception("Failed to fetch card: " + e.getMessage());
        }
        
    }

}