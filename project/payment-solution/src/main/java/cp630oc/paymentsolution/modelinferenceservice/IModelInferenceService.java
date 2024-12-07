package cp630.oc.paymentsolution.modelinferenceservice;

import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;

public interface IModelInferenceService {
  public boolean detectFraud(Card card, Transaction transaction);
} 