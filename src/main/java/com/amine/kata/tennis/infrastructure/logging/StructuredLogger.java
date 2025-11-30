package com.amine.kata.tennis.infrastructure.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class StructuredLogger {
    
    private final Logger logger;
    
    public StructuredLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }
    
    public void logEvent(String event, String message, Map<String, Object> metadata) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", event);
        logData.put("message", message);
        logData.put("timestamp", Instant.now().toString());
        logData.put("correlationId", MDC.get("correlationId"));
        
        if (metadata != null) {
            logData.putAll(metadata);
        }
        
        logger.info("{}", logData);
    }
    
    public void logOperationStart(String operation, Map<String, Object> input) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", operation);
        metadata.put("status", "STARTED");
        metadata.put("input", input);
        
        logEvent("OPERATION_START", String.format("Début de l'opération: %s", operation), metadata);
    }
    
    public void logOperationSuccess(String operation, Map<String, Object> output) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", operation);
        metadata.put("status", "SUCCESS");
        metadata.put("output", output);
        
        logEvent("OPERATION_SUCCESS", String.format("Opération réussie: %s", operation), metadata);
    }
    
    public void logOperationError(String operation, String error, Exception exception) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", operation);
        metadata.put("status", "ERROR");
        metadata.put("error", error);
        metadata.put("exception", exception.getClass().getSimpleName());
        metadata.put("exceptionMessage", exception.getMessage());
        
        logEvent("OPERATION_ERROR", String.format("Erreur dans l'opération: %s - %s", operation, error), metadata);
    }
    
    public void logBusinessEvent(String event, String message, Map<String, Object> businessData) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("businessData", businessData);
        
        logEvent("BUSINESS_EVENT", message, metadata);
    }
    
    public void logApiRequest(String method, String endpoint, Map<String, Object> requestData) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("httpMethod", method);
        metadata.put("endpoint", endpoint);
        metadata.put("requestData", requestData);
        
        logEvent("API_REQUEST", String.format("%s %s", method, endpoint), metadata);
    }
    
    public void logApiResponse(String method, String endpoint, int statusCode, Map<String, Object> responseData) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("httpMethod", method);
        metadata.put("endpoint", endpoint);
        metadata.put("statusCode", statusCode);
        metadata.put("responseData", responseData);
        
        logEvent("API_RESPONSE", String.format("%s %s - Status: %d", method, endpoint, statusCode), metadata);
    }
    
    public void logApiError(String method, String endpoint, int statusCode, String error) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("httpMethod", method);
        metadata.put("endpoint", endpoint);
        metadata.put("statusCode", statusCode);
        metadata.put("error", error);
        
        logEvent("API_ERROR", String.format("%s %s - Erreur: %s", method, endpoint, error), metadata);
    }
}
