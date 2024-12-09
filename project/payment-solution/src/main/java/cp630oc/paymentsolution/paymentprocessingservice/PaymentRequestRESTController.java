package cp630oc.paymentsolution.paymentprocessingservice;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(PaymentRequestRESTController.class);

    private static final String TAG = PaymentRequestRESTController.class.getName();

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
    public ResponseEntity<CreatePaymentRequestResponse> createPaymentRequest(@Valid CreatePaymentRequestRequest request, Optional<String> xAuthorization, Optional<String> xApiKey, Optional<String> xNotification) {

        logger.debug(TAG, "runnning createPaymentRequest ...");

        if (xNotification.isPresent()) {
            if (xNotification.get().equals("true")) {
                this.notificationEnabled = true;
            } 
        }

        try {

            // Fetch card
            logger.debug("[{}] Fetching card ...", TAG);
            Card card = fetchCard(request);
            logger.debug("[{}] Card fetched: {}", TAG, card.getId());

            // Create transaction
            logger.debug( "[{}] Creating transaction ...", TAG);
            Transaction savedTransaction = createTransaction(card, request);
            logger.debug("[{}] Transaction created: {}", TAG,  savedTransaction.getId());

            // Detect fraud
            logger.debug("[{}] Detecting fraud ...", TAG);
            boolean fraudDetected = modelInferenceService.detectFraud(card, savedTransaction, notificationEnabled);
            logger.debug("[{}] Fraud detected: {}", TAG, fraudDetected);

            // Create response
            logger.debug("[{}] Creating response ...", TAG);
            CreatePaymentRequestResponse response = new CreatePaymentRequestResponse();

            // Set transaction ID
            logger.debug("[{}] Setting transaction id  of response", TAG);
            response.setTransactionId(Long.toString(savedTransaction.getId()));

            // Set transaction timestamp
            logger.debug(TAG, "[{}] Setting transaction timestamp of response", TAG);
            Date transactionDatetime = savedTransaction.getTransactionDatetime();
            OffsetDateTime transactionTimestamp = Instant.ofEpochMilli(transactionDatetime.getTime())
                                                .atOffset(ZoneOffset.UTC);
            response.setTransactionTimestamp(transactionTimestamp);


    
            response.setFraudDetected(fraudDetected);

            // Set transaction status
            if (fraudDetected) {
                logger.debug("[{}] Fraud has been detected", TAG);

                logger.debug("[{}] Transaction status set to CANCELLED in response", TAG);
                response.setTransactionStatus("CANCELLED");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                logger.debug("[{}] Fraud not detected", TAG);
            }

            logger.debug("[{}] Transaction status set to ACCEPTED in response", TAG);
            response.setTransactionStatus("ACCEPTED");

            logger.debug("[{}] createPaymentRequest finished", TAG);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("[{}] Exception rasised: {}", TAG, e.getMessage());

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
            logger.debug("[{}] Setting transaction date and time: {}", TAG, transactionTimestamp);
            transaction.setTransactionDatetime(Date.from(transactionTimestamp.toInstant()));

            // Set transaction amount
            float transactionAmount = request.getTransactionAmount();
            logger.debug("[{}] Setting transaction amount: {}", TAG, transactionAmount);
            transaction.setTransactionAmount(request.getTransactionAmount());

            // Set transaction type
            String transactionType = request.getTransactionType();
            logger.debug("[{}] Setting transaction type: {}", TAG, transactionType);
            transaction.setTransactionType(request.getTransactionType());
            
            // Set merchant information
            long merchantId = Long.parseLong(request.getMerchantId());
            logger.debug("[{}] Setting merchant id: {}", TAG, merchantId);
            transaction.setMerchantId(request.getMerchantId());

            String merchantCity = request.getMerchantCity();
            logger.debug("[{}] Setting merchant city: {}", TAG, merchantCity);
            transaction.setMerchantCity(request.getMerchantCity());

            String merchantState = request.getMerchantState();
            logger.debug("[{}] Setting merchant state: {}", TAG, merchantState);
            transaction.setMerchantState(request.getMerchantState());

            String merchantZip = request.getMerchantZip();
            logger.debug("[{}] Setting merchant zip: {}", TAG, merchantZip);
            transaction.setMerchantZip(request.getMerchantZip());

            String merchantMccCode = request.getMerchantMccCode();
            logger.debug("[{}] Setting merchant mcc code {}", TAG, merchantMccCode);
            transaction.setMerchantMccCode(request.getMerchantMccCode());
            
            // Set fraud detection
            boolean fraudDetected = false;
            logger.debug("[{}] Setting fraud detection: {}", TAG, fraudDetected);
            transaction.setFraudDetected(fraudDetected);
            
            // Set card
            logger.debug("[{}] Setting card: {}", TAG, card.getId());
            transaction.setCard(card);

            // Save transaction
            logger.debug("[{}] Saving transaction ...", TAG);
            Transaction savedTransaction = transactionRepository.save(transaction);
            if (savedTransaction == null) {
                logger.debug("[{}] Failed to save transaction", TAG);
                throw new Exception("Failed to save transaction");
            }
            logger.debug("[{}] Transaction saved: {}", TAG, savedTransaction.getId());
            transactionRepository.flush();

            // Create initial transaction state
            logger.debug("[{}] Creating initial transaction state ...", TAG);
            TransactionState transactionState = new TransactionState();

            logger.debug("[{}] Setting transaction state to PENDING", TAG);

            transactionState.setCreatedAt(new Date());
            transactionState.setUpdatedAt(new Date()); 
            TransactionStateId stateId = new TransactionStateId(savedTransaction.getId(), "PENDING");
            transactionState.setId(stateId);
            transactionState.setTransaction(savedTransaction);

            logger.debug("[{}] Saving transaction state ...", TAG);
            TransactionState savedTransactionState = transactionStateRepository.save(transactionState);
            if (savedTransactionState == null) {
                logger.debug("[{}] Failed to save transaction state", TAG);
                throw new Exception("Failed to save transaction state");
            }
            logger.debug("[{}] Transaction state saved: {}", TAG, savedTransactionState.getId());
            transactionStateRepository.flush();
            
            // add intial states to transaction
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
            logger.debug("[{}] Fetching card by card number: {}", TAG, request.getCardNumber());
            Card card = cardRepository.findByCardNumber(request.getCardNumber());
            if (card == null) {
                logger.debug("[{}] Card not found", TAG);
                throw new Exception("Card number not found");
            }
            logger.debug("[{}] Card fetched: {}", TAG, card.getId());
            return card;
        } catch (Exception e) {
            throw new Exception("Failed to fetch card: " + e.getMessage());
        }
        
    }

}