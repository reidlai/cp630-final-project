package cp630oc.paymentsolution.modelinferenceservice;

import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;

public interface IModelInferenceService {
  public float fraudProbability(Card card, Transaction transaction, boolean notificationEnabled);
} 