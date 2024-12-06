package cp630oc.paymentsolution.paymentnotificationservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;
import cp630oc.paymentsolution.paymentrequeststore.entity.TransactionState;
import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Customer;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class NotificationService {

    @Autowired
    private SMTPRelayService smtpRelayService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SpringTemplateEngine templateEngine;

    /**
     * Send notification to related party.
     * 
     * @param notificationRequest
     * @throws Exception
     */
    public void sendNotification(NotificationRequest notificationRequest) throws Exception {
        // Get the transaction ID from the notification request
        Long transactionId = Long.parseLong(notificationRequest.getTransactionId());

        // Get the transaction from the payment request store
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new Exception("Transaction not found"));

        // Get the transaction state from the payment request store
        Set<TransactionState> transactionStates = transaction.getTransactionStates();

        // Get the transaction details
        Date transactionDatetime = transaction.getTransactionDatetime();
        double transactionAmount = transaction.getTransactionAmount();
        Card card = transaction.getCard();
        String cardNumber = card.getCardNumber();
        Customer customer = card.getCustomer();
        String firstName = customer.getFirstName();
        String email = customer.getEmail();

        // Create a context for the email template
        Context context = new Context();
        Map<String, Object> params = new HashMap<>();
        params.put("transactionId", transactionId);
        params.put("transactionDatetime", transactionDatetime);
        params.put("transactionAmount", transactionAmount);
        params.put("cardNumber", cardNumber);
        params.put("firstName", firstName);
        context.setVariables(params);

        String subject = null;
        String body = null;

        // Find the latest transaction state
        TransactionState latestTransactionState = transactionStates.stream()
            .filter(state -> state.getDeletedAt() == null)
            .findFirst()
            .orElseThrow(() -> new Exception("Transaction state not found"));

        // Determine email template based on transaction state
        if (latestTransactionState.getId().getState().equals("accepted")) {
            subject = "Payment Transaction Accepted";
            body = templateEngine.process("payment-transaction-accepted", context);
        } else if (latestTransactionState.getId().getState().equals("onhold")) {
            subject = "Payment Transaction On Hold";
            body = templateEngine.process("payment-transaction-onhold", context);
        }

        // Send email
        smtpRelayService.sendEmail(email, subject, body);
    }
}