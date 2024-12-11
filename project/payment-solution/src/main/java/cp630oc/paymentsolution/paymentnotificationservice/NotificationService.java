package cp630oc.paymentsolution.paymentnotificationservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;
import cp630oc.paymentsolution.paymentrequeststore.entity.TransactionState;
import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Customer;

import java.util.Date;
import java.util.Set;

/**
 * The payment notification service.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private static final String TAG = NotificationService.class.getName();

    @Autowired
    private SMTPRelayService smtpRelayService;

    /**
     * Send notification to related party.
     * 
     * @param card Card Entity
     * @param transaction Transaction Entity
     * @throws Exception
     */
    public void sendNotification(Card card, Transaction transaction) throws Exception {

        logger.debug("[{}] Sending notification for transaction ID: {}", TAG, transaction.getId());

        // Get the transaction ID from the notification request
        Long transactionId = transaction.getId();

        // Get the transaction state from the payment request store
        Set<TransactionState> transactionStates = transaction.getTransactionStates();

        // Get the transaction details
        Date transactionDatetime = transaction.getTransactionDatetime();
        double transactionAmount = transaction.getTransactionAmount();

        String cardNumber = card.getCardNumber();
        Customer customer = card.getCustomer();
        String firstName = customer.getFirstName();
        String email = customer.getEmail();

        String subject = null;
        String body = null;

        TransactionState currentState = null;
        for (TransactionState transactionState : transactionStates) {
            if (transactionState.getDeletedAt() == null) {
                currentState = transactionState;
            }
        }

        if (currentState == null) {
            throw new Exception("No current transaction state found for transaction ID: " + transactionId);
        }

        // Determine email template based on transaction state
        logger.debug("[{}] Rendering email template for transaction state: {}", TAG, currentState.getId().getState());
        if (currentState.getId().getState().equals("ACCEPTED")) {
            subject = "Payment Transaction Accepted";
            body = createAcceptedEmailBody(transactionId, transactionDatetime, transactionAmount, cardNumber, firstName);
        } else if (currentState.getId().getState().equals("ONHOLD")) {
            subject = "Payment Transaction On Hold";
            body = createOnHoldEmailBody(transactionId, transactionDatetime, transactionAmount, cardNumber, firstName);
        }

        // Send email
        logger.debug("[{}] Sending email to: {}", TAG, email);
        smtpRelayService.sendEmail(email, subject, body);
    }

    /**
     * Create the email body for an accepted transaction.
     * 
     * @param transactionId transaction ID
     * @param transactionDatetime transaction date and time
     * @param transactionAmount transaction amount
     * @param cardNumber card number
     * @param firstName customer first name
     * @return
     */
    private String createAcceptedEmailBody(Long transactionId, Date transactionDatetime, double transactionAmount, String cardNumber, String firstName) {
        return "Dear " + firstName + ",\n\n" +
               "Your payment transaction has been accepted.\n\n" +
               "Transaction Details:\n" +
               "Transaction ID: " + transactionId + "\n" +
               "Transaction Date: " + transactionDatetime + "\n" +
               "Transaction Amount: $" + transactionAmount + "\n" +
               "Card Number: " + cardNumber + "\n\n" +
               "Thank you for your business.\n\n" +
               "Best regards,\n" +
               "Your Company Name";
    }

    /**
     * Create the email body for a transaction on hold.
     * 
     * @param transactionId transaction ID
     * @param transactionDatetime transaction date and time
     * @param transactionAmount transaction amount
     * @param cardNumber card number
     * @param firstName customer first name
     * @return
     */
    private String createOnHoldEmailBody(Long transactionId, Date transactionDatetime, double transactionAmount, String cardNumber, String firstName) {
        return "Dear " + firstName + ",\n\n" +
               "Your payment transaction is currently on hold.\n\n" +
               "Transaction Details:\n" +
               "Transaction ID: " + transactionId + "\n" +
               "Transaction Date: " + transactionDatetime + "\n" +
               "Transaction Amount: $" + transactionAmount + "\n" +
               "Card Number: " + cardNumber + "\n\n" +
               "Please contact our support team for further assistance.\n\n" +
               "Best regards,\n" +
               "Your Company Name";
    }
}