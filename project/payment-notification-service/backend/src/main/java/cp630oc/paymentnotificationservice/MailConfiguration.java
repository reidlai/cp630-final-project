package cp630oc.paymentnotificationservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * The mail configuration.
 */
@Configuration
public class MailConfiguration {

    /**
     * The java mail sender.
     *
     * @return the java mail sender
     */
    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }
}