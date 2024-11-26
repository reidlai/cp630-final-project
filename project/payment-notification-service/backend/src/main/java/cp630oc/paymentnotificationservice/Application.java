package cp630oc.paymentnotificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
    "cp630oc.paymentnotificationservice", 
    "cp630oc.paymentrequeststore"  
})
@EntityScan(basePackages = {
    "cp630oc.paymentrequeststore.entity"  
})
@EnableJpaRepositories(basePackages = {
    "cp630oc.paymentrequeststore.repository"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}