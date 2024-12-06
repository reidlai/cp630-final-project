package cp630oc.paymentsolution.paymentnotificationservice;

public interface INotificationService {
    /**
     * Send notification to related party.
     * 
     * @param notificationRequest
     * @throws Exception
     */
    void sendNotification(NotificationRequest notificationRequest) throws Exception;
}