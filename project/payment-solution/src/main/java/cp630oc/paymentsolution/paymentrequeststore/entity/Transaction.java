package cp630oc.paymentsolution.paymentrequeststore.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * The transaction entity.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_datetime", nullable = false)
    private Date transactionDatetime;

    // @Column(name = "card_index", nullable = false)
    // private int cardIndex;

    @Column(name = "transaction_amount", nullable = false)
    private float transactionAmount;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "merchant_id", nullable = false)
    private String merchantId;

    @Column(name = "merchant_city", nullable = true)
    private String merchantCity;

    @Column(name = "merchant_state", nullable = true)
    private String merchantState;

    @Column(name = "merchant_zip", nullable = true)
    private String merchantZip;

    @Column(name = "merchant_mcc_code", nullable = true)
    private String merchantMccCode;

    @Column(name = "transaction_error", nullable = true)
    private String transactionError;

    @Column(name = "fraud_detected", nullable = true)
    private boolean fraudDetected;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "customer_id", referencedColumnName = "customer_id"),
        @JoinColumn(name = "card_index", referencedColumnName = "card_index")
    })
    private Card card;

    @OneToMany(mappedBy = "transaction")
    private Set<TransactionState> transactionStates;

    /**
     * Get the id of the transaction.
     * @return Transaction ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the transaction.
     * @param id Transaction ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the transaction date and time.
     * @return the transaction date and time
     */
    public Date getTransactionDatetime() {
        return transactionDatetime;
    }

    /**
     * Set the transaction date and time.
     * @param transactionDatetime the transaction date and time
     */
    public void setTransactionDatetime(Date transactionDatetime) {
        this.transactionDatetime = transactionDatetime;
    }

    /**
     * Get the transaction amount.
     * @return the transaction amount
     */
    public float getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * Set the transaction amount.
     * @param transactionAmount the transaction amount
     */
    public void setTransactionAmount(float transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /**
     * Get the transaction type.
     * @return the transaction type
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * Set the transaction type.
     * @param transactionType the transaction type
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * Get the merchant ID.
     * @return the merchant ID
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * Set the merchant ID.
     * @param merchantId the merchant ID
     */
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * Get the merchant city.
     * @return the merchant city
     */
    public String getMerchantCity() {
        return merchantCity;
    }

    /**
     * Set the merchant city.
     * @param merchantCity the merchant city
     */
    public void setMerchantCity(String merchantCity) {
        this.merchantCity = merchantCity;
    }

    /**
     * Get the merchant state.
     * @return the merchant state
     */
    public String getMerchantState() {
        return merchantState;
    }

    /**
     * Set the merchant state.
     * @param merchantState the merchant state
     */
    public void setMerchantState(String merchantState) {
        this.merchantState = merchantState;
    }

    /**
     * Get the merchant ZIP code.
     * @return the merchant ZIP code
     */
    public String getMerchantZip() {
        return merchantZip;
    }

    /**
     * Set the merchant ZIP code.
     * @param merchantZip the merchant ZIP code
     */
    public void setMerchantZip(String merchantZip) {
        this.merchantZip = merchantZip;
    }

    /**
     * Get the merchant MCC code.
     * @return the merchant MCC code
     */
    public String getMerchantMccCode() {
        return merchantMccCode;
    }

    /**
     * Set the merchant MCC code.
     * @param merchantMccCode the merchant MCC code
     */
    public void setMerchantMccCode(String merchantMccCode) {
        this.merchantMccCode = merchantMccCode;
    }

    /**
     * Get the transaction error.
     * @return the transaction error
     */
    public String getTransactionError() {
        return transactionError;
    }

    /**
     * Set the transaction error.
     * @param transactionError the transaction error
     */
    public void setTransactionError(String transactionError) {
        this.transactionError = transactionError;
    }

    /**
     * Check if fraud was detected.
     * @return true if fraud was detected, false otherwise
     */
    public boolean isFraudDetected() {
        return fraudDetected;
    }

    /**
     * Set if fraud was detected.
     * @param fraudDetected true if fraud was detected, false otherwise
     */
    public void setFraudDetected(boolean fraudDetected) {
        this.fraudDetected = fraudDetected;
    }

    /**
     * Get the card used for the transaction.
     * @return the card used for the transaction
     */
    public Card getCard() {
        return card;
    }

    /**
     * Set the card used for the transaction.
     * @param card the card used for the transaction
     */
    public void setCard(Card card) {
        this.card = card;
    }

    /**
     * Get the transaction states.
     * @return the transaction states
     */
    public Set<TransactionState> getTransactionStates() {
        return transactionStates;
    }

    /**
     * Set the transaction states.
     * @param transactionStates the transaction states
     */
    public void setTransactionStates(Set<TransactionState> transactionStates) {
        this.transactionStates = transactionStates;
    }
}