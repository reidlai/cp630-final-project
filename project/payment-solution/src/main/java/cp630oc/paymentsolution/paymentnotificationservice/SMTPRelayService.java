package cp630oc.paymentsolution.paymentnotificationservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


/**
 * The SMTP relay service.
 */
@Service
public class SMTPRelayService {

    private final JavaMailSender mailSender;

    /**
     * Instantiates a new SMTP relay service.
     *
     * @param mailSender the mail sender
     */
    @Autowired
    public SMTPRelayService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send an email.
     *
     * @param email the email
     * @param subject the subject
     * @param body the body
     * @throws MessagingException the messaging exception
     */
    public void sendEmail(String email, String subject, String body) throws MessagingException {
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(email);
        // message.setSubject(subject);
        // message.setText(body);
        // mailSender.send(message);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom("noreply@mailhog.local");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(body, false); // false indicates this is plain text, not HTML
        
        mailSender.send(message);
    }


}