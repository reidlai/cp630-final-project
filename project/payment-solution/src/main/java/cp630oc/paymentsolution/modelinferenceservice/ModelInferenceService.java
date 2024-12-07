package cp630oc.paymentsolution.modelinferenceservice;

import ai.onnxruntime.*;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionRepository;
import cp630oc.paymentsolution.paymentnotificationservice.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.example.data.Group;

@Service
public class ModelInferenceService implements IModelInferenceService {

    private static final Logger logger = LoggerFactory.getLogger(ModelInferenceService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;

    private OrtEnvironment env;
    private OrtSession session;

    @Value("${MODEL_PATH}")
    private String modelPath;

    @Value("${ECONDING_MAPPING_DIR}")
    private String encodingMappingDir;

    private static final int NUM_FEATURES = 9;

    private Map<String, Map<String, Float>> encodingMappings = new HashMap<>();
    private static String[] encoderNames = {
        "card_brand",
        "card_type",
        "gender",
        "merchant_city",
        "merchant_mcc_code",
        "transaction_error",
        "transaction_type"
    };

    private static String[] featureNames = {
        "merchant_city_encoded",
        "transaction_amount",
        "merchant_mcc_code_encoded",
        "credit_score",
        "latitude",
        "total_debt",
        "transaction_day",
        "birth_year",
        "transaction_month"
    };

    private static String[] categoricalFeatureNames = {
        "merchant_city_encoded",
        "merchant_mcc_code_encoded"
    };

    private static String[] numericalFeatureNames = {
        "transaction_amount",
        "credit_score",
        "latitude",
        "total_debt",
        "transaction_day",
        "birth_year",
        "transaction_month"
    };

    /**
     * Default constructor
     */
    public ModelInferenceService() {
        try {
            loadEncodingMappings();
        } catch (IOException e) {
            logger.error("Failed to load encoding mappings: {}", e.getMessage());
            throw new RuntimeException("Failed to load encoding mappings", e);
        }
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
                return env.createSession(modelPath, sessionOptions);
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
    private float encodeMerchantCity(String merchantCity) {
        return encodingMappings.get("merchant_city").get(merchantCity);
    }

    private float encodeMerchantMccCode(String mccCode) {
        return encodingMappings.get("merchant_mcc_code").get(mccCode);
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
    
    private void loadEncodingMappings() throws IOException {
        try {
            for (String encoderName : encoderNames) {

                // Initialize the map for this encoder if it doesn't exist
                encodingMappings.putIfAbsent(encoderName, new HashMap<>());
                
                // Create file and path
                File parquetFile = new File(encodingMappingDir + "/" + encoderName + "_encoded.parquet");
                Path path = new Path(parquetFile.getAbsolutePath());
                
                // Configure and create reader
                Configuration conf = new Configuration();
                ParquetReader<Group> reader = ParquetReader.builder(new GroupReadSupport(), path)
                    .withConf(conf)
                    .build();
        
                // Read records
                Group group;
                while ((group = reader.read()) != null) {
                    String recordKey = group.getString("key", 0);
                    float recordValue = group.getFloat("value", 0);
                    encodingMappings.get(encoderName).put(recordKey, recordValue);
                }
        
                reader.close();
            } 
            
        } catch (IOException e) {
            logger.error("Failed to load encoding mappings: {}", e.getMessage());
            throw e;
        }
    }
}