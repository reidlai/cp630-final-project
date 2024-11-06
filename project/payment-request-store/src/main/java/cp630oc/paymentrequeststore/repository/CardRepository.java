package cp630oc.paymentrequeststore.repository;

import cp630oc.paymentrequeststore.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    // Find cards by card number
    Optional<Card> findByCardNumber(String cardNumber);
    
    // Find cards by card brand
    List<Card> findByCardBrand(String cardBrand);
    
    // Find cards by card type
    List<Card> findByCardType(String cardType);
    
    // Find cards by customer id
    List<Card> findByCustomerId(Long customerId);
    
    // Find cards expiring before a certain date
    List<Card> findByCardExpirationDateBefore(Date expirationDate);
    
    // Find cards by credit limit greater than
    List<Card> findByCreditLimitGreaterThan(float creditLimit);
    
    // Find cards by card brand and type
    List<Card> findByCardBrandAndCardType(String cardBrand, String cardType);
    
    // Check if card number exists
    boolean existsByCardNumber(String cardNumber);
}