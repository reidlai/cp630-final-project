package cp630oc.paymentrequeststore.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_datetime", nullable = false)
    private Date transactionDatetime;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "transaction_amount", nullable = false)
    private double transactionAmount;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private String merchantId;

    @Column(name = "merchant_city", nullable = false)
    private String merchantCity;

    @Column(name = "merchant_state", nullable = false)
    private String merchantState;

    @Column(name = "merchant_zip", nullable = false)
    private String merchantZip;

    @Column(name = "merchant_mcc_code", nullable = false)
    private String merchantMccCode;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "transaction_error")
    private String transactionError;

    @Column(name = "fraud_detected", nullable = false)
    private boolean fraudDetected;

    @Column(name = "status", nullable = false)
    private String status;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTransactionDatetime() {
        return transactionDatetime;
    }

    public void setTransactionDatetime(Date transactionDatetime) {
        this.transactionDatetime = transactionDatetime;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantCity() {
        return merchantCity;
    }

    public void setMerchantCity(String merchantCity) {
        this.merchantCity = merchantCity;
    }

    public String getMerchantState() {
        return merchantState;
    }

    public void setMerchantState(String merchantState) {
        this.merchantState = merchantState;
    }

    public String getMerchantZip() {
        return merchantZip;
    }

    public void setMerchantZip(String merchantZip) {
        this.merchantZip = merchantZip;
    }

    public String getMerchantMccCode() {
        return merchantMccCode;
    }

    public void setMerchantMccCode(String merchantMccCode) {
        this.merchantMccCode = merchantMccCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getTransactionError() {
        return transactionError;
    }

    public void setTransactionError(String transactionError) {
        this.transactionError = transactionError;
    }

    public boolean isFraudDetected() {
        return fraudDetected;
    }

    public void setFraudDetected(boolean fraudDetected) {
        this.fraudDetected = fraudDetected;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}