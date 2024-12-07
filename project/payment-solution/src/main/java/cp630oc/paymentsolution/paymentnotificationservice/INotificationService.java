package cp630oc.paymentsolution.paymentnotificationservice;

import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;

public interface INotificationService {
    /**
     * Send notification to related party.
     * 
     * @param cards Card Entity
     * @param transaction Transaction Entity
     * @throws Exception
     */
    void sendNotification(Card cards, Transaction transaction) throws Exception;
}