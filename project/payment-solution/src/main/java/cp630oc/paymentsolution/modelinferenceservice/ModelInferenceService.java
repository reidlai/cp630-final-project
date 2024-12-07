package cp630oc.paymentsolution.modelinferenceservice;

import ai.onnxruntime.*;

import java.nio.FloatBuffer;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionRepository;
import cp630oc.paymentsolution.paymentnotificationservice.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ModelInferenceService implements IModelInferenceService {

    private static final Logger logger = LoggerFactory.getLogger(ModelInferenceService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;

    private OrtEnvironment env;
    private OrtSession session;
    private static final String MODEL_PATH = "path/to/your/model.onnx";
    private static final int NUM_FEATURES = 9;

    /**
     * Default constructor
     */
    public ModelInferenceService() {
        // loadOnnxModel();
    }

    /**
     * Load an ONNX model
     */ 
    public OrtSession loadOnnxModel() {
        try {
            if (session == null) {
                env = OrtEnvironment.getEnvironment();
                OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
                sessionOptions.setIntraOpNumThreads(1);
                return env.createSession(MODEL_PATH, sessionOptions);
            }
            return session;
        } catch (OrtException e) {
            throw new RuntimeException("Failed to load ONNX model: " + e.getMessage());
        }
    }

    /**
     * Detect fraud using the loaded ONNX model
     * @param card Card information
     * @param transaction Transaction information
     * @return boolean indicating if fraud is detected
     */
    @Override
    public boolean detectFraud(Card card, Transaction transaction, boolean notificationEnabled) {
        try {            
            // Load model if not loaded
            if (session == null) {
                session = loadOnnxModel();
            }
            
            // Prepare input features
            float[] features = extractFeatures(card, transaction);
            OnnxTensor inputTensor = OnnxTensor.createTensor(env, 
                FloatBuffer.wrap(features), new long[]{1, features.length});

            // Create input map
            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put(session.getInputNames().iterator().next(), inputTensor);

            // Run inference
            try (OrtSession.Result results = session.run(inputs)) {
                // Get output
                float[] outputProbs = ((OnnxTensor) results.get(0)).getFloatBuffer().array();
                
                boolean fraudDetected = outputProbs[0] > 0.5;

                // Update fraud_detected field in transaction
                transaction.setFraudDetected(fraudDetected);
            
                transactionRepository.save(transaction);

                if (fraudDetected && notificationEnabled) {
                    // Call notification service
                    try {
                        notificationService.sendNotification(card, transaction);
                    } catch (Exception e) {
                        logger.error("Failed to send notification: " + e.getMessage());
                        throw new RuntimeException("Failed to send notification", e);
                    }
                }

                return fraudDetected;
            }

        } catch (OrtException e) {
            throw new RuntimeException("Error during fraud detection: " + e.getMessage());
        }
    }

    private float[] extractFeatures(Card card, Transaction transaction) {
        float[] features = new float[NUM_FEATURES];
        
        // 1. merchant_city_encoded (mean: -2.944654, std: 1.889771)
        features[0] = encodeMerchantCity(transaction.getMerchantCity());
        
        // 2. transaction_amount (mean: 6.788333, std: 0.567789)
        features[1] = (float) Math.log(transaction.getTransactionAmount());
        
        // 3. merchant_mcc_code_encoded (mean: 0.105373, std: 0.111984)
        features[2] = encodeMerchantMccCode(transaction.getMerchantMccCode());
        
        // 4. credit_score (mean: 3.410716, std: 0.893172)
        features[3] = normalizeCreditScore(card.getCreditLimit());
        
        // 5. latitude (mean: 3.168211, std: 0.921945)
        features[4] = encodeLatitude(transaction.getMerchantState());
        
        // 6. total_debt (mean: 0.676236, std: 0.438641)
        features[5] = calculateTotalDebt(card);
        
        // 7. transaction_day (mean: 1.669347, std: 0.981270)
        features[6] = normalizeTransactionDay(transaction.getTransactionDatetime());
        
        // 8. birth_year (mean: 3.046614, std: 0.947527)
        features[7] = normalizeBirthYear(card.getAccountOpenDate());
        
        // 9. transaction_month (mean: 1.604990, std: 1.000072)
        features[8] = normalizeTransactionMonth(transaction.getTransactionDatetime());
        
        return features;
    }

    // Feature normalization methods
    private float encodeMerchantCity(String city) {
        if (city == null) return -2.944654f; // mean value
        return Math.max(-6.308005f, Math.min(0.353721f, 
            (city.hashCode() % 7) * 1.889771f - 2.944654f));
    }

    private float encodeMerchantMccCode(String mccCode) {
        if (mccCode == null) return 0.105373f; // mean value
        return Math.max(0.0f, Math.min(0.306327f,
            Float.parseFloat(mccCode) / 10000.0f));
    }

    private float normalizeCreditScore(float creditLimit) {
        return Math.max(1.557801f, Math.min(4.945640f,
            (creditLimit - 5000.0f) / 10000.0f + 3.410716f));
    }

    private float encodeLatitude(String state) {
        if (state == null) return 3.168211f; // mean value
        return Math.max(1.380197f, Math.min(4.598690f,
            (state.hashCode() % 4) * 0.921945f + 3.168211f));
    }

    private float calculateTotalDebt(Card card) {
        float baseDebt = card.getCardOnDarkWeb() ? 0.8f : 0.4f;
        return Math.max(0.0f, Math.min(1.443322f, 
            baseDebt + (card.getNumberCardsIssued() - 1) * 0.1f));
    }

    private float normalizeTransactionDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        float day = cal.get(Calendar.DAY_OF_MONTH);
        return Math.max(0.113700f, Math.min(3.183609f,
            (day / 31.0f) * 2.0f + 0.5f));
    }

    private float normalizeBirthYear(Date accountOpenDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(accountOpenDate);
        int year = cal.get(Calendar.YEAR);
        return Math.max(1.120426f, Math.min(4.419460f,
            (year - 2000) / 20.0f + 3.0f));
    }

    private float normalizeTransactionMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        float month = cal.get(Calendar.MONTH);
        return Math.max(0.0f, Math.min(3.190054f,
            month / 12.0f * 3.2f));
    }

}