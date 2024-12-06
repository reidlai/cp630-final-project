package cp630oc.paymentsolution.paymentnotificationservice;

/**
 * The notification request.
 * 
 * This class represents the notification request.
 */
public class NotificationRequest {

    private String transactionId;

    /**
     * Instantiates a new notification request.
     * 
     * @param transactionId the transaction id
     */
    public NotificationRequest(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Gets the transaction id.
     *
     * @return the transaction id
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the transaction id.
     *
     * @param transactionId the new transaction id
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}