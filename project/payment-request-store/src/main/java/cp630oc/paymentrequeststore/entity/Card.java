package cp630oc.paymentrequeststore.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Card entity class to represent a card in the database table cards. The class is 
 * annotated with JPA annotations to define the mapping between the Java class and 
 * the database table. The class contains the following attributes:
 * - id: Long
 * - cardNumber: String
 * - cardBrand: String
 * - cardType: String
 * - cardExpirationDate: Date
 * - cvvCode: String
 * - hasChip: boolean
 * - numberCardsIssued: int
 * - creditLimit: float
 * - customer: Customer
 * - transactions: List of Transaction
 */
@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "card_brand", nullable = false)
    private String cardBrand;

    @Column(name = "card_type", nullable = false)
    private String cardType;

    @Column(name = "card_expiration_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date cardExpirationDate;

    @Column(name = "cvv_code", nullable = false)
    private String cvvCode;

    @Column(name = "has_chip", nullable = false)
    private boolean hasChip;

    @Column(name = "number_cards_issued", nullable = false)
    private int numberCardsIssued;

    @Column(name = "credit_limit", nullable = false)
    private float creditLimit;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    /**
     * Get the card ID
     * @return card ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the card ID
     * @param id card ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the card number
     * @return card number
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Set the card number
     * @param cardNumber card number
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Get the card brand
     * @return card brand
     */
    public String getCardBrand() {
        return cardBrand;
    }

    /**
     * Set the card brand
     * @param cardBrand card brand
     */
    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    /**
     * Get the card type
     * @return card type
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * Set the card type
     * @param cardType card type
     */
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    /**
     * Get the card expiration date
     * @return card expiration date
     */
    public Date getCardExpirationDate() {
        return cardExpirationDate;
    }

    /**
     * Set the card expiration date
     * @param cardExpirationDate card expiration date
     */
    public void setCardExpirationDate(Date cardExpirationDate) {
        this.cardExpirationDate = cardExpirationDate;
    }

    /**
     * Get the CVV code
     * @return CVV code
     */
    public String getCvvCode() {
        return cvvCode;
    }

    /**
     * Set the CVV code
     * @param cvvCode CVV code
     */
    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }

    /**
     * Check if the card has a chip
     * @return true if the card has a chip, false otherwise
     */
    public boolean isHasChip() {
        return hasChip;
    }

    /**
     * Set if the card has a chip
     * @param hasChip true if the card has a chip, false otherwise
     */
    public void setHasChip(boolean hasChip) {
        this.hasChip = hasChip;
    }

    /**
     * Get the number of cards issued
     * @return number of cards issued
     */
    public int getNumberCardsIssued() {
        return numberCardsIssued;
    }

    /**
     * Set the number of cards issued
     * @param numberCardsIssued number of cards issued
     */
    public void setNumberCardsIssued(int numberCardsIssued) {
        this.numberCardsIssued = numberCardsIssued;
    }

    /**
     * Get the credit limit
     * @return credit limit
     */
    public float getCreditLimit() {
        return creditLimit;
    }

    /**
     * Set the credit limit
     * @param creditLimit credit limit
     */
    public void setCreditLimit(float creditLimit) {
        this.creditLimit = creditLimit;
    }

    /**
     * Get the customer
     * @return Customer 
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Set the customer
     * @param customer Customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Get the transactions
     * @return List of Transaction
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Set the transactions
     * @param transactions List of Transaction
     */
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}