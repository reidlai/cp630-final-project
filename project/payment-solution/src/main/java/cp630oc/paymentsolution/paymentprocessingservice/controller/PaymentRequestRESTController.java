package cp630oc.paymentsolution.paymentprocessingservice.controller;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import cp630oc.paymentsolution.paymentprocessingservice.api.PaymentRequestApi;
import cp630oc.paymentsolution.paymentprocessingservice.model.CreatePaymentRequestRequest;
import cp630oc.paymentsolution.paymentprocessingservice.model.CreatePaymentRequestResponse;

@RestController
public class PaymentRequestRESTController implements PaymentRequestApi {

    // @Autowired
    // private TransactionRepository transactionRepository;
    
    // @Autowired
    // private CardRepository cardRepository;
    
    // @Autowired
    // private TransactionStateRepository transactionStateRepository;

    // @PostMapping("/paymentrequest")
    // public ResponseEntity<?> processPaymentRequest(@RequestBody Map<String, Object> paymentRequest) {
    //     try {

    //         // // Create new transaction
    //         // Transaction transaction = new Transaction();

    //         // // Set created and updated timestamps
    //         // transactionState.setCreatedAt(new Date());
    //         // transactionState.setUpdatedAt(new Date());

    //         // // Create initial transaction state
    //         // TransactionState transactionState = new TransactionState();
            
    //         /***********************
    //          * Persist in database *
    //          ***********************/

    //         // // Save transaction
    //         // Transaction savedTransaction = transactionRepository.save(transaction);

    //         // // Set transaction state ID
    //         // TransactionStateId stateId = new TransactionStateId(savedTransaction.getId(), "pending");
    //         // transactionState.setId(stateId);
            
    //         // // Associate transaction state with transaction
    //         // transactionState.setTransaction(savedTransaction);
            
    //         // // Save transaction state
    //         // transactionStateRepository.save(transactionState);

    //         /*******************
    //          * Return response *
    //         ********************/
    //         return ResponseEntity.ok(Map.of(
    //             "transactionId", "1",
    //             "status", "pending",
    //             "message", "Not yet implemented"
    //         ));

    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest()
    //             .body(Map.of(
    //                 "status", "error",
    //                 "message", e.getMessage()
    //             ));
    //     }
    // }

    @Override
    public ResponseEntity<CreatePaymentRequestResponse> createPaymentRequest(CreatePaymentRequestRequest request) {
        try {
            CreatePaymentRequestResponse response = new CreatePaymentRequestResponse();
            response.setTransactionId(UUID.randomUUID().toString());
            response.setTransactionTimestamp(OffsetDateTime.now());  // Changed this line
            response.setTransactionStatus("APPROVED");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            CreatePaymentRequestResponse errorResponse = new CreatePaymentRequestResponse();
            errorResponse.setTransactionStatus("FAILED");
            errorResponse.setTransactionError(e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(errorResponse);
        }
    }
}