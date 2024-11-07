package cp630oc.paymentnotificationservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import cp630oc.paymentrequeststore.entity.Transaction;
import cp630oc.paymentrequeststore.entity.Card;
import cp630oc.paymentrequeststore.entity.Customer;
import cp630oc.paymentrequeststore.repository.TransactionRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class NotificationRESTController {

    @Autowired
    private SMTPRelayService smtpRelayService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @PostMapping("/notification")
    public ResponseEntity<String> postNotification(@RequestBody NotificationRequest notificationRequest) {

        // Get the transaction ID from the notification request
        Long transactionId = Long.parseLong(notificationRequest.getTransactionId());

        // Get the transaction from the payment request store
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);

        if (transaction == null) {
            return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
        }

        // Get the transaction datetime
        Date transactionDatetime = transaction.getTransactionDatetime();

        // Get the transaction amount
        double transactionAmount = transaction.getTransactionAmount();

        // Get the card from the transaction
        Card card = transaction.getCard();

        // Get the card number
        String cardNumber = card.getCardNumber();

        // Get the customer from the card
        Customer customer = card.getCustomer();

        // Get customer first name
        String firstName = customer.getFirstName();

        // Get customer email
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

        if (transaction.getStatus().equals("accepted")) {
            subject = "Payment Transaction Accepted";
            body = templateEngine.process("payment-transaction-accepted", context);
        } else if (transaction.getStatus().equals("onhold")) {
            subject = "Payment Transaction On Hold";
            body = templateEngine.process("payment-transaction-onhold", context);
        }

        try {
            smtpRelayService.sendEmail(email, subject, body);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Notification was sent", HttpStatus.OK);
    }
}