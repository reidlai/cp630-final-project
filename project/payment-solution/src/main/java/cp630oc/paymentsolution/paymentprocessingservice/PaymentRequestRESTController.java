package cp630oc.paymentsolution.paymentprocessingservice;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cp630oc.paymentsolution.paymentprocessingservice.api.PaymentRequestApi;
import cp630oc.paymentsolution.paymentprocessingservice.api.PaymentRequestStatusApi;
import cp630oc.paymentsolution.paymentprocessingservice.api.PaymentRequestStatusesApi;
import cp630oc.paymentsolution.paymentprocessingservice.api.PaymentRequestsApi;
import cp630oc.paymentsolution.paymentprocessingservice.model.CreatePaymentRequestRequest;
import cp630oc.paymentsolution.paymentprocessingservice.model.CreatePaymentRequestResponse;
import cp630oc.paymentsolution.paymentprocessingservice.model.PaymentRequest;
import cp630oc.paymentsolution.paymentprocessingservice.model.PaymentRequestStatus;
import cp630oc.paymentsolution.paymentprocessingservice.model.UpdatePaymentRequestStatusByIdRequest;
import cp630oc.paymentsolution.paymentrequeststore.repository.CardRepository;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionRepository;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionStateRepository;
import jakarta.validation.Valid;
import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;
import cp630oc.paymentsolution.paymentrequeststore.entity.TransactionState;
import cp630oc.paymentsolution.paymentrequeststore.entity.TransactionStateId;

import cp630oc.paymentsolution.modelinferenceservice.ModelInferenceService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The payment request REST controller which implements the PaymentRequestApi interface generated 
 * by OpenAPI generator.
 */
@RestController
@CrossOrigin(
    origins = "*",
    allowedHeaders = {
        "Content-Type",
        "Accept",
        "Origin",
        "X-Notification",
        "Authorization"
    },
    methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE,
        RequestMethod.OPTIONS
    },
    exposedHeaders = {
        "Access-Control-Allow-Origin",
        "Access-Control-Allow-Credentials"
    }
)
public class PaymentRequestRESTController implements PaymentRequestApi, PaymentRequestsApi, PaymentRequestStatusApi, PaymentRequestStatusesApi {

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
     * 
     * @param request The request body.
     * @param Authorization The x-authorization header.
     * @param xApiKey The x-api-key header.
     * @param xNotification The x-notification header.
     * @return The response entity.
     */
    @Override
    public ResponseEntity<CreatePaymentRequestResponse> createPaymentRequest(@Valid CreatePaymentRequestRequest request, Optional<String> Authorization, Optional<String> xApiKey, Optional<String> xNotification) {

        logger.debug(TAG, "runnning createPaymentRequest ...");

        // Check if notification is enabled
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


            // Set fraud detected
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

            // Set transaction status
            logger.debug("[{}] Transaction status set to ACCEPTED in response", TAG);
            response.setTransactionStatus("ACCEPTED");
            logger.debug("[{}] createPaymentRequest finished", TAG);
            return ResponseEntity.status(HttpStatus.CREATED).header("Connection", "keep-alive").header("Keep-Alive", "time-out=60").contentType(MediaType.APPLICATION_JSON).body(response);
            
        } catch (Exception e) {
            logger.error("[{}] Exception rasised: {}", TAG, e.getMessage());

            CreatePaymentRequestResponse errorResponse = new CreatePaymentRequestResponse();
            errorResponse.setTransactionStatus("FAILED");
            errorResponse.setTransactionError(e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(errorResponse);

        }
        
    }

    /**
     * Get all payment requests.
     * 
     * @param Authorization The authorization header.
     * @param xApiKey The x-api-key header.
     * @return The response entity.
     */
    @Override
    public ResponseEntity<PaymentRequest> getPaymentRequestById(String id, Optional<String> Authorization, Optional<String> xApiKey) {

        // Fetch transaction
        Optional<Transaction> transactionOptional = transactionRepository.findById(Long.parseLong(id));
        if (!transactionOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Transaction transaction = transactionOptional.get();

        // Create response
        PaymentRequest response = new PaymentRequest();
        response.setId(transaction.getId().toString());
        response.setTransactionAmount(transaction.getTransactionAmount());
        response.setTransactionDatetime(Instant.ofEpochMilli(transaction.getTransactionDatetime().getTime()).atOffset(ZoneOffset.UTC));
        response.setTransactionType(transaction.getTransactionType());
        response.setMerchantId(transaction.getMerchantId());
        response.setMerchantCity(transaction.getMerchantCity());
        response.setMerchantState(transaction.getMerchantState());
        response.setMerchantZip(transaction.getMerchantZip());
        response.setMerchantMccCode(transaction.getMerchantMccCode());
        response.setFraudDetected(transaction.isFraudDetected());
        return ResponseEntity.status(HttpStatus.OK).header("Connection", "keep-alive").header("Keep-Alive", "time-out=60").contentType(MediaType.APPLICATION_JSON).body(response);
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

            // Create transaction
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

            // Set merchant city, state, zip, and mcc code
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

            // Set transaction state
            logger.debug("[{}] Setting transaction state to PENDING", TAG);
            transactionState.setCreatedAt(new Date());
            transactionState.setUpdatedAt(new Date()); 
            TransactionStateId stateId = new TransactionStateId(savedTransaction.getId(), "PENDING");
            transactionState.setId(stateId);
            transactionState.setTransaction(savedTransaction);

            // Save transaction state
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

            // Fetch card
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

    /**
     * Get all payment requests.
     * 
     * @param Authorization The authorization header.
     * @param xApiKey The x-api-key header.
     * @return The response entity.
     */
    @Override
    public ResponseEntity<List<PaymentRequest>> getPaymentRequestsByCardNumber(String cardNumber, Optional<String> Authorization, Optional<String> xApiKey) {
        try {

            // Fetch card
            Card card = cardRepository.findByCardNumber(cardNumber);
            if (card == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Fetch transactions
            List<Transaction> transactions = transactionRepository.findAllByCard(card);
            if (transactions == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Create response
            List<PaymentRequest> response = new ArrayList<>();

            // Iterate through transactions
            for (Transaction transaction : transactions) {

                // Create payment request
                PaymentRequest paymentRequest = new PaymentRequest();
                paymentRequest.setId(transaction.getId().toString());
                paymentRequest.setTransactionAmount(transaction.getTransactionAmount());
                paymentRequest.setTransactionDatetime(Instant.ofEpochMilli(transaction.getTransactionDatetime().getTime()).atOffset(ZoneOffset.UTC));
                paymentRequest.setTransactionType(transaction.getTransactionType());
                paymentRequest.setMerchantId(transaction.getMerchantId());
                paymentRequest.setMerchantCity(transaction.getMerchantCity());
                paymentRequest.setMerchantState(transaction.getMerchantState());
                paymentRequest.setMerchantZip(transaction.getMerchantZip());
                paymentRequest.setMerchantMccCode(transaction.getMerchantMccCode());
                paymentRequest.setFraudDetected(transaction.isFraudDetected());

                // Add payment request to response
                response.add(paymentRequest);
            }
            return ResponseEntity.status(HttpStatus.OK).header("Connection", "keep-alive").header("Keep-Alive", "time-out=60").contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (Exception e) {
            logger.error("[{}] Exception rasied: {}", TAG, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    /**
     * Get all payment requests.
     * 
     * @param Authorization The authorization header.
     * @param xApiKey The x-api-key header.
     * @return The response entity.
     */
    @Override
    public ResponseEntity<List<PaymentRequestStatus>> getPaymentRequestStatusesById(String id, Optional<String> Authorization, Optional<String> xApiKey) {
        try {

            // Fetch transaction
            Optional<Transaction> transactionOptional = transactionRepository.findById(Long.parseLong(id));
            if (!transactionOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            Transaction transaction = transactionOptional.get();

            // Create response
            List<TransactionState> transactionStates = new ArrayList<>(transaction.getTransactionStates());
            List<PaymentRequestStatus> response = new ArrayList<>();
            for (TransactionState transactionState : transactionStates) {
                PaymentRequestStatus paymentRequestStatus = new PaymentRequestStatus();
                paymentRequestStatus.setId(transactionState.getTransaction().getId().toString());
                paymentRequestStatus.setState(transactionState.getId().getState());
                paymentRequestStatus.setCreatedAt(Instant.ofEpochMilli(transactionState.getCreatedAt().getTime()).atOffset(ZoneOffset.UTC));
                paymentRequestStatus.setUpdatedAt(Instant.ofEpochMilli(transactionState.getUpdatedAt().getTime()).atOffset(ZoneOffset.UTC));
                // Check if deletedAt is null
                if (transactionState.getDeletedAt() != null) {
                    paymentRequestStatus.setDeletedAt(Instant.ofEpochMilli(transactionState.getDeletedAt().getTime()).atOffset(ZoneOffset.UTC));
                } else {
                    paymentRequestStatus.setDeletedAt(null);
                }
                response.add(paymentRequestStatus);
            }
            return ResponseEntity.status(HttpStatus.OK).header("Connection", "keep-alive").header("Keep-Alive", "time-out=60").contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (Exception e) {
            logger.error("[{}] Exception rasied: {}", TAG, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Get all payment requests.
     * 
     * @param Authorization The authorization header.
     * @param xApiKey The x-api-key header.
     * @return The response entity.
     */
    @Override
    public ResponseEntity<PaymentRequestStatus> getPaymentRequestStatusById(String id, Optional<String> Authorization, Optional<String> xApiKey) {
        try {

            // Fetch transaction
            Optional<Transaction> transactionOptional = transactionRepository.findById(Long.parseLong(id));
            if (!transactionOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Create response
            Transaction transaction = transactionOptional.get();
            List<TransactionState> transactionStates = new ArrayList<>(transaction.getTransactionStates());
            TransactionState transactionState = transactionStates.get(transactionStates.size() - 1);
            PaymentRequestStatus response = new PaymentRequestStatus();
            response.setId(transactionState.getTransaction().getId().toString());
            response.setState(transactionState.getId().getState());
            response.setCreatedAt(Instant.ofEpochMilli(transactionState.getCreatedAt().getTime()).atOffset(ZoneOffset.UTC));
            response.setUpdatedAt(Instant.ofEpochMilli(transactionState.getUpdatedAt().getTime()).atOffset(ZoneOffset.UTC));
            return ResponseEntity.status(HttpStatus.OK).header("Connection", "keep-alive").header("Keep-Alive", "time-out=60").contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (Exception e) {
            logger.error("[{}] Exception rasied: {}", TAG, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Update payment request status by id.
     * 
     * @param request The request body.
     * @param Authorization The x-authorization header.
     * @param xApiKey The x-api-key header.
     * @param xNotification The x-notification header.
     * @return The response entity.
     */
    @Override
    public ResponseEntity<PaymentRequestStatus> updatePaymentRequestStatusById(UpdatePaymentRequestStatusByIdRequest request, Optional<String> Authorization, Optional<String> xApiKey, Optional<String> xNotification) {
    // public ResponseEntity<PaymentRequestStatus> updatePaymentRequestStatusById(UpdatePaymentRequestStatusByIdRequest request) {
        logger.debug("[{}] Updating payment request status by id ...", TAG);
        try {

            // Check request state exists or not
            TransactionState existingState = transactionStateRepository.findLatestStateByIdAndState(Long.parseLong(request.getId()), request.getState());

            // Find current state
            TransactionState currentState = transactionStateRepository.findLatestStateById(Long.parseLong(request.getId()));
            if (currentState == null) {
                logger.debug("[{}] No latest transaction state found", TAG);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(null);
            }
            logger.debug("[{}] Current state: {}", TAG, currentState.getId().getState());

            // Disable latest state
            Date now = new Date();
            logger.debug("[{}] Updating transaction state: before state {}", TAG, currentState.getId().getState());
            currentState.setUpdatedAt(now);
            currentState.setDeletedAt(now);
            transactionStateRepository.save(currentState);
            transactionStateRepository.flush();
            logger.debug("[{}] Updating deletedAt of state {}", TAG, currentState.getId().getState());
            
            // Create new state
            TransactionState savedTransactionState = null;
            if (existingState == null) {
                logger.debug("[{}] Creating new transaction state id ...", TAG);
                TransactionStateId stateId = new TransactionStateId();
                stateId.setId(Long.parseLong(request.getId()));
                stateId.setState(request.getState());
                logger.debug("[{}] New transaction state id created: {}", TAG, stateId.getState());

                logger.debug("[{}] Creating new transaction state ...", TAG);
                TransactionState newTransactionState = new TransactionState();
                newTransactionState.setId(stateId);
                newTransactionState.setCreatedAt(now);
                newTransactionState.setUpdatedAt(now);
                logger.debug("[{}] New transaction state created: {}", TAG, newTransactionState.getId().getState());
                logger.debug("[{}] Saving new transaction state {} ...", TAG, newTransactionState.getId().getState());
                savedTransactionState = transactionStateRepository.save(newTransactionState);
                if (savedTransactionState == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
            } else {
                logger.debug("[{}] Existing transaction state found: {}", TAG, existingState.getId().getState());
                logger.debug("[{}] Updating existing transaction state ...", TAG);
                existingState.setUpdatedAt(now);
                existingState.setDeletedAt(null);
                logger.debug("[{}] Saving existing transaction state ...", TAG);
                savedTransactionState = transactionStateRepository.save(existingState);
                if (savedTransactionState == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
            }
            transactionStateRepository.flush();
            logger.debug("[{}] New transaction state saved: {}", TAG, savedTransactionState.getId().getState());

            // Create response
            logger.debug("[{}] Creating response for Updating payment request status by id ...", TAG);
            PaymentRequestStatus response = new PaymentRequestStatus();
            response.setId(savedTransactionState.getId().getId().toString());
            response.setState(savedTransactionState.getId().getState());
            response.setCreatedAt(Instant.ofEpochMilli(savedTransactionState.getCreatedAt().getTime()).atOffset(ZoneOffset.UTC));
            response.setUpdatedAt(Instant.ofEpochMilli(savedTransactionState.getUpdatedAt().getTime()).atOffset(ZoneOffset.UTC));
            response.setDeletedAt(savedTransactionState.getDeletedAt() != null ? Instant.ofEpochMilli(savedTransactionState.getDeletedAt().getTime()).atOffset(ZoneOffset.UTC) : null);
            logger.debug("[{}] Response created for Updating payment request status by id", TAG);
            return ResponseEntity.status(HttpStatus.OK).header("Connection", "keep-alive").header("Keep-Alive", "time-out=60").contentType(MediaType.APPLICATION_JSON).body(response);


        } catch (Exception e) {
            logger.error("[{}] Exception rasied: {}", TAG, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


}