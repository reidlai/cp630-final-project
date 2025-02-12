package cp630oc.paymentsolution.modelinferenceservice;

import ai.onnxruntime.*; // impport Microsoft ONNXRuntime 
import jakarta.annotation.PostConstruct; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;    
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.time.ZoneId;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cp630oc.paymentsolution.paymentrequeststore.entity.Card;
import cp630oc.paymentsolution.paymentrequeststore.entity.Transaction;
import cp630oc.paymentsolution.paymentrequeststore.entity.TransactionState;
import cp630oc.paymentsolution.paymentrequeststore.entity.TransactionStateId;
import cp630oc.paymentsolution.paymentrequeststore.repository.TransactionStateRepository;
import cp630oc.paymentsolution.paymentnotificationservice.NotificationService;

/**
 * The model inference service.
 */
@Service
public class ModelInferenceService  {

    private static final Logger logger = LoggerFactory.getLogger(ModelInferenceService.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TransactionStateRepository transactionStateRepository;

    private OrtEnvironment env;
    private OrtSession session;

    @Value("${MODEL_PATH}")
    private String modelPath;

    @Value("${ENCODING_MAPPING_DIR}")
    private String encodingMappingDir;

    private static final int NUM_FEATURES = 9;

    private static String[] featureNames = {
        "merchant_city_encoded",
        "transaction_amount",
        "latitude",
        "merchant_mcc_code_encoded",
        "total_debt",
        "credit_score",
        "longitude",
        "transaction_day",
        "birth_year"
    };
 
    private Map<String, Map<String, Float>> encodingMappings = new HashMap<>();

    private static String[] encoderNames = {
        "card_brand",
        "card_type",
        "gender",
        "merchant_city",
        "merchant_mcc_code",
        "transaction_error",
        "transaction_type",
    };

    private Map<String, Map<String, Float>> scalarParameters = new HashMap<>();

    private Map<String, Map<String, Float>> winsorBounds = new HashMap<>();

    private Map<String, Map<String, Float>> boxcoxParams = new HashMap<>();

    private static final String TAG = ModelInferenceService.class.getSimpleName();

    /**
     * Default constructor
     */
    public ModelInferenceService() {

    }

    /**
     * Initialize the ModelInferenceService
     */
    @PostConstruct
    public void init() {
        logger.debug("[{}] Initializing ModelInferenceService...", TAG);    

        if (encodingMappingDir == null || encodingMappingDir.trim().isEmpty()) {
            logger.debug("[{}] ENCODING_MAPPING_DIR environment variable is not set", TAG);
            throw new IllegalStateException("ENCODING_MAPPING_DIR environment variable is not set");
        }

        try {
            logger.debug("[{}] Loading encoding mappings...", TAG);
            loadEncodingMappings();
            logger.debug("[{}] Loaded encoding mappings", TAG);

            logger.debug("[{}] Loading scalar parameters...", TAG);
            loadScalerParameters();
            logger.debug("[{}] Loaded scalar parameters", TAG);

            logger.debug("[{}] Loading winsorization parameters...", TAG);
            loadWinsorParams();
            logger.debug("[{}] Loaded winsorization parameters", TAG);

            logger.debug("[{}] Loading Box-Cox parameters...", TAG);
            loadBoxcoxParameters();
            logger.debug("[{}] Loaded Box-Cox parameters", TAG);

        } catch (IOException e) {
            logger.error("Failed to initialize ModelInferenceService: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize ModelInferenceService", e);
        }

        logger.debug("[{}] ModelInferenceService initialized", TAG);
    }

    /**
     * Load an ONNX model
     * 
     * @return OrtSession ONNXRuntime session
     */ 
    public OrtSession loadOnnxModel() {
        try {
            if (session == null) {
                logger.debug("[{}] Getting ONNX environment...", TAG);
                env = OrtEnvironment.getEnvironment();

                // Add validation for modelPath
                if (modelPath == null || modelPath.trim().isEmpty()) {
                    throw new IllegalStateException("MODEL_PATH environment variable is not set");
                }

                logger.debug("[{}] Loading ONNX session options...", TAG);
                OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();

                logger.debug("[{}] Setting intra-op num threads to 1...", TAG);
                sessionOptions.setIntraOpNumThreads(1);

                return env.createSession(modelPath, sessionOptions);
            }
            return session;
        } catch (OrtException e) {

            logger.error("[{}] Failed to load ONNX model: {}", TAG, e.getMessage());
            throw new RuntimeException("Failed to load ONNX model: " + e.getMessage());
        }
    }

    /**
     * Detect fraud using the loaded ONNX model
     * 
     * @param card Card information
     * @param transaction Transaction information
     * @param notificationEnabled boolean indicating if notification is enabled
     * @return boolean indicating if fraud is detected
     */
    public boolean detectFraud(Card card, Transaction transaction, boolean notificationEnabled) {
     
        // Validate input
        if (card == null || transaction == null) {
            throw new IllegalArgumentException("Card and Transaction cannot be null");
        }
    
        try {       
            // Load the ONNX model     
            if (session == null) {
                logger.debug("[{}] Calling loadOnnxModel...", TAG);
                session = loadOnnxModel();
            }
            
            // Extract features
            logger.debug("[{}] Extracting features...", TAG);
            float[] features = extractFeatures(card, transaction);
            
            // Create input tensor
            logger.debug("[{}] Creating input tensor...", TAG);
            OnnxTensor inputTensor = OnnxTensor.createTensor(env, 
                FloatBuffer.wrap(features), new long[]{1, features.length});
    
            // Prepare the input map
            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put(session.getInputNames().iterator().next(), inputTensor);
    
            logger.debug("[{}] Running inference...", TAG);

            // Run inference
            logger.debug("[{}] Running inference...", TAG);
            OrtSession.Result results = session.run(inputs);

            // Get the label output
            Optional<OnnxValue> labelOptional = results.get("label");

            // Check if the label output is present
            if (!labelOptional.isPresent()) {
                throw new RuntimeException("Label output not found in model results");
            }

            // Get the label output
            OnnxValue labelOutput = labelOptional.get();

            // Check if the label output is an OnnxTensor
            if (!(labelOutput instanceof OnnxTensor)) {
                throw new RuntimeException("Expected OnnxTensor output but got: " + labelOutput.getClass());
            }

            // Get the label tensor
            OnnxTensor labelTensor = (OnnxTensor) labelOutput;

            // Get the label array
            long[] labelArray = (long[]) labelTensor.getValue();

            // Get the fraud detection result
            boolean fraudDetected = (labelArray[0] == 1);

            // Close the input tensor
            inputTensor.close();

            // Close the results
            results.close();

            // Update the transaction status
            updateTransactionStatus(transaction, fraudDetected);

            // Send notification if enabled
            if (notificationEnabled) {
                logger.debug("[{}] Calling sendNotification...", TAG);
                notificationService.sendNotification(card, transaction);
                logger.debug("[{}] sendNotification call returned", TAG);
            }

            return fraudDetected;

        } catch (Exception e) {
            logger.error("[{}] Error during fraud detection: {}", TAG, e.getMessage());
            throw new RuntimeException("Error during fraud detection: " + e.getMessage());
        }
    }

    /**
     * Extract features from the Card and Transaction entities
     * 
     * @param card Card entity
     * @param transaction Transaction entity
     * @return float[] array of features
     */
    private float[] extractFeatures(Card card, Transaction transaction) {

        // Validate input
        if (card == null || transaction == null) {
            throw new IllegalArgumentException("Card and Transaction cannot be null");
        }
        if (card.getCustomer() == null) {
            throw new IllegalArgumentException("Card must have associated Customer");
        }

        // Initialize features array
        float[] features = new float[NUM_FEATURES];
        
        // 1. merchant_city_encoded (mean: -2.944654, std: 1.889771)
        logger.debug("[{}] Extracting merchant_city: {}", TAG, transaction.getMerchantCity());
        features[0] = encodeValue("merchant_city", transaction.getMerchantCity());
        
        // 2. transaction_amount (mean: 6.788333, std: 0.567789)
        logger.debug("[{}] Extracting transaction_amount: {}", TAG, transaction.getTransactionAmount());
        features[1] = transaction.getTransactionAmount();
        
        // 3. latitude (mean: 0.000000, std: 1.000000)
        logger.debug("[{}] Extracting latitude: {}", TAG, card.getCustomer().getLatitude());
        features[2] = card.getCustomer().getLatitude();

        // 4. merchant_mcc_code_encoded (mean: -0.000000, std: 1.000000)
        logger.debug("[{}] Extracting merchant_mcc_code: {}", TAG, transaction.getMerchantMccCode());
        features[3] = encodeValue("merchant_mcc_code", transaction.getMerchantMccCode());

        // 5. total_debt (mean: 0.000000, std: 1.000000)
        logger.debug("[{}] Extracting total_debt: {}", TAG, card.getCustomer().getTotalDebt());
        features[4] = card.getCustomer().getTotalDebt();

        // 6. credit_score (mean: 0.000000, std: 1.000000)
        logger.debug("[{}] Extracting credit_score: {}", TAG, card.getCustomer().getCreditScore());
        features[5] = card.getCustomer().getCreditScore();

        // 7. longitude (mean: 0.000000, std: 1.000000)
        logger.debug("[{}] Extracting longitude: {}", TAG, card.getCustomer().getLongitude());
        features[6] = card.getCustomer().getLongitude();

        // 8. transaction_day (mean: 0.000000, std: 1.000000)
        logger.debug("[{}] Extracting transaction_day: {}", TAG, transaction.getTransactionDatetime().toInstant().atZone(ZoneId.systemDefault()).getDayOfMonth());
        features[7] = transaction.getTransactionDatetime().toInstant().atZone(ZoneId.systemDefault()).getDayOfMonth();

        // 9. birth_year (mean: 0.000000, std: 1.000000)
        logger.debug("[{}] Extracting birth_year: {}", TAG, card.getCustomer().getBirthYear());
        features[8] = card.getCustomer().getBirthYear();
        
        // Standardize features
        for (int i = 0; i < NUM_FEATURES; i++) {
            String featureName = featureNames[i];
            logger.debug("[{}] Standardizing feature: {}", TAG, featureName);
            features[i] = normalizeValue(featureName, features[i]);
            logger.debug("[{}] Standardized feature: {}", TAG, features[i]);
        }

        // Winsorize features
        for (int i = 0; i < NUM_FEATURES; i++) {
            String featureName = featureNames[i];

            logger.debug("[{}] Winsorizing feature: {}", TAG, featureName);
            float lowerBound = winsorBounds.get(featureName).get("lower_bound");
            float upperBound = winsorBounds.get(featureName).get("upper_bound");
            features[i] = Math.min(Math.max(features[i], lowerBound), upperBound);
            logger.debug("[{}] Winsorized feature: {}", TAG, features[i]);
        }

        // Apply Box-Cox transformation to numeric features
        for (int i = 0; i < featureNames.length; i++) {
            logger.debug("[{}] Applying Box-Cox transformation to feature: {}", TAG, featureNames[i]);
            features[i] = applyBoxCox(featureNames[i], features[i]);
            logger.debug("[{}] Box-Cox transformed feature: {}", TAG, features[i]);
        }

        return features;
    }

    /**
     * Feature normalization methods
     * 
     * @param key feature name
     * @param value feature value
     * @return float normalized value
     */
    private float encodeValue(String key, String value) {
        // return encodingMappings.get(key).get(value).floatValue();
        Map<String, Float> encoding = encodingMappings.get(key);

        // Check if the encoding is null or the value is not found
        if (encoding == null || !encoding.containsKey(value)) {
            // Return -1.0f if the value is not found in the encoding to handle missing values
            return -1.0f;
        } else {
            return encoding.get(value).floatValue();
        }
    }

    
    /**
     * Normalize a feature value using StandardScaler algorithm
     * 
     * @param key
     * @param value
     * @return
     */
    private float normalizeValue(String key, float value) {
        // StandardScaler method
        Float mean = scalarParameters.get(key).get("mean");
        Float std = scalarParameters.get(key).get("std");
        return Float.valueOf((value - mean) / std).floatValue();
    }
    
    /**
     * Load encoding mappings from JSON files
     * 
     * @throws IOException
     */
    private void loadEncodingMappings() throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            for (String encoderName : encoderNames) {

                logger.debug("[{}] Loading encoding mappings for {}...", TAG, encoderName);

                // Initialize the map for this encoder if it doesn't exist
                logger.debug("[{}] Initializing encoding mappings for {}...", TAG, encoderName);
                encodingMappings.putIfAbsent(encoderName, new HashMap<>());

                // Load the JSON file
                File jsonFile = new File(encodingMappingDir, encoderName + "_encoded.json");
                logger.debug("[{}] Loading encoding mappings from {}...", TAG, jsonFile.getAbsolutePath());
                if (!jsonFile.exists()) {
                    logger.debug("[{}] File not found: {}", TAG, jsonFile.getAbsolutePath());
                    throw new FileNotFoundException("File not found: " + jsonFile.getAbsolutePath());
                }
                logger.debug("[{}] File found: {}", TAG, jsonFile.getAbsolutePath());

                try {
                    // Read the JSON array
                    logger.debug("[{}] Reading JSON array...", TAG);
                    JsonNode arrayNode = mapper.readTree(jsonFile);
                    if (arrayNode.isArray()) {
                        logger.debug("[{}] JSON array found: size {}", TAG, arrayNode.size());

                        // Iterate over the JSON array
                        for (JsonNode node : arrayNode) {

                            // Get the key-value pair
                            String key = node.get(encoderName).asText();
                            float value = node.get(encoderName + "_encoded").floatValue();
                            logger.debug("[{}] Adding key-value pair: {} -> {}", TAG, key, value);

                            // Add the key-value pair to the map
                            encodingMappings.get(encoderName).put(key, value);

                        }
                        logger.debug("[{}] Encoding mappings loaded for {}", TAG, encoderName);
                    } else {
                        logger.debug("[{}] JSON array not found", TAG);
                    }
                } catch (IOException e) {
                    logger.error("Failed to load encoding mappings for {}: {}", encoderName, e.getMessage());
                    throw e;
                }
            } 
            
        } catch (IOException e) {
            logger.error("Failed to load encoding mappings: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Load scalar parameters from JSON files
     * 
     * @throws IOException
     */
    private void loadScalerParameters() throws IOException {
 
        try {

            // Load the JSON file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(encodingMappingDir + "/scaler_params.json"));
            
            // Load parameters from JSON
            String[] columns = mapper.convertValue(root.get("columns"), String[].class);
            float[] mean = mapper.convertValue(root.get("mean"), float[].class);
            float[] std = mapper.convertValue(root.get("std"), float[].class);

            // Add parameters to the map
            for (int i = 0; i < columns.length; i++) {

                // Initialize the scalar parameters map if it doesn't exist
                Map<String, Float> scalarParams = new HashMap<>();

                // Add the scalar parameters
                scalarParams.put("mean", mean[i]);
                scalarParams.put("std", std[i]);

                // Add the scalar parameters to the map
                scalarParameters.put(columns[i], scalarParams);
            }
            
        } catch (IOException e) {
            logger.error("Failed to load scaler parameters: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Load winsorization parameters from JSON files
     * 
     * @throws IOException
     */
    private void loadWinsorParams() throws IOException{
        try {

            // Load the JSON file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(encodingMappingDir + "/winsor_params.json"));
            
            // Load parameters from JSON
            root.fields().forEachRemaining(entry -> {

                // Get the key-value pair
                String feature = entry.getKey();
                JsonNode boundsNode = entry.getValue();
                
                // Add the winsor bounds to the map
                Map<String, Float> bounds = new HashMap<>();
                bounds.put("lower_bound", boundsNode.get("lower_bound").floatValue());
                bounds.put("upper_bound", boundsNode.get("upper_bound").floatValue());
                winsorBounds.put(feature, bounds);

            });
        } catch (IOException e) {
            logger.error("Failed to load winsorization parameters: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Load Box-Cox parameters from JSON files
     * 
     * @throws IOException
     */
    private void loadBoxcoxParameters() throws IOException {
 
        try {

            // Load the JSON file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(encodingMappingDir + "/boxcox_params.json"));
            
            // Load parameters from JSON
            root.fields().forEachRemaining(entry -> {

                // Get the key-value pair
                String feature = entry.getKey();
                JsonNode boxcoxNode = entry.getValue();
                
                // Add the Box-Cox parameters to the map
                Map<String, Float> boxcoxParam = new HashMap<>();
                boxcoxParam.put("min_value", boxcoxNode.get("min_value").floatValue());
                boxcoxParam.put("lambda", boxcoxNode.get("lambda").floatValue());
                boxcoxParams.put(feature, boxcoxParam);
            });
            
        } catch (IOException e) {
            logger.error("Failed to load scalar parameters: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Apply Box-Cox transformation to a feature value
     * 
     * @param featureName
     * @param value
     * @return
     */
    private float applyBoxCox(String featureName, float value) {

        // Get Box-Cox parameters
        Map<String, Float> params = boxcoxParams.get(featureName);
        if (params == null) {
            return value;
        }
    
        // Shift value if needed
        float shiftedValue = value;
        if (params.get("min_value").floatValue() <= 0) {
            shiftedValue = value - params.get("min_value").floatValue() + 1;
        }
    
        // Apply Box-Cox transformation
        if (Math.abs(params.get("lambda").floatValue()) < 1e-10) {
            return (float) Math.log(shiftedValue);
        } else {
            return (float) ((Math.pow(shiftedValue, params.get("lambda").floatValue()) - 1) / params.get("lambda").floatValue());
        }
    }

    /**
     * Update the transaction status
     * 
     * @param transaction
     * @param fraudDetected
     * @throws Exception
     */
    private void updateTransactionStatus(Transaction transaction, boolean fraudDetected) throws Exception { 
        // Update the transaction with the fraud detection result
        logger.debug("[{}] Updating transaction status: fraudDetected={}", TAG, fraudDetected);
        transaction.setFraudDetected(fraudDetected);

        // Save the transaction state
        logger.debug("[{}] Getting transaction state...", TAG);
        Set<TransactionState> transactionStats =  transaction.getTransactionStates();
        logger.debug("[{}] Found {} transaction states", TAG, transactionStats.size());

        // Find the latest transaction state
        logger.debug("[{}] Finding latest transaction state...", TAG);
        TransactionState currentState = null;
        for (TransactionState state : transactionStats) {
            if (state.getId().getState().equals("PENDING")) {
                currentState = state;
                break;
            }
        }

        // Update the latest transaction state if found
        if (currentState != null) {
            logger.debug("[{}] Found latest transaction state: {}", TAG, currentState.getId().getState());

            // Update the latest transaction state
            logger.debug("[{}] Updating latest transaction state...", TAG);
            currentState.setDeletedAt(new Date());
            TransactionState obsoletedState = transactionStateRepository.save(currentState);
            logger.debug("[{}] obsolete state: {}", TAG, obsoletedState.getId().getState());
            transactionStateRepository.flush();
            logger.debug("[{}] obsolete state flushed", TAG);

            // Create a new transaction state
            TransactionState newState = new TransactionState();
            TransactionStateId newStateId = new TransactionStateId();
            newStateId.setId(transaction.getId());
            newState.setId(newStateId);
            newState.setCreatedAt(new Date());
            newState.setUpdatedAt(new Date());
            if (fraudDetected) {
                newStateId.setState("ONHOLD");
            } else {
                newStateId.setState("ACCEPTED");
            }

            // Save the new transaction state
            TransactionState savedTransactionState = transactionStateRepository.save(newState);
            if (savedTransactionState == null) {
                logger.error("[{}] Failed to save transaction state", TAG);
                throw new Exception("Failed to save transaction state");
            }
            transactionStateRepository.flush();
            transaction.getTransactionStates().add(savedTransactionState);
            
        } else {
            logger.error("[{}] Failed to find latest transaction state", TAG);
            throw new Exception("Failed to find latest transaction state");
        }

    }
}