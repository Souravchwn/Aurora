package com.sourav.aurora.exception;

/**
 * Exception thrown when news provider operations fail
 */
public class NewsProviderException extends RuntimeException {
    
    private final String providerName;
    private final String errorCode;
    
    public NewsProviderException(String providerName, String message) {
        super(message);
        this.providerName = providerName;
        this.errorCode = "PROVIDER_ERROR";
    }
    
    public NewsProviderException(String providerName, String message, Throwable cause) {
        super(message, cause);
        this.providerName = providerName;
        this.errorCode = "PROVIDER_ERROR";
    }
    
    public NewsProviderException(String providerName, String errorCode, String message) {
        super(message);
        this.providerName = providerName;
        this.errorCode = errorCode;
    }
    
    public NewsProviderException(String providerName, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.providerName = providerName;
        this.errorCode = errorCode;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}