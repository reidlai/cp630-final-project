package cp630oc.paymentnotificationservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

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
        mailSender.send(mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
        });
    }


}