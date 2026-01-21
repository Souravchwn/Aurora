package com.sourav.aurora.constants;

/**
 * API endpoint constants for Aurora News Service
 */
public final class ApiConstants {
    
    private ApiConstants() {
        // Utility class
    }
    
    // Base API path
    public static final String API_BASE_PATH = "/api";
    
    // News endpoints
    public static final String NEWS_BASE = API_BASE_PATH + "/news";
    public static final String NEWS_TODAY = NEWS_BASE + "/today";
    public static final String NEWS_REFRESH = NEWS_BASE + "/refresh";
    public static final String NEWS_SEARCH = NEWS_BASE + "/search";
    
    // Provider endpoints
    public static final String PROVIDERS_BASE = API_BASE_PATH + "/providers";
    public static final String PROVIDERS_ACTIVE = PROVIDERS_BASE + "/active";
    public static final String PROVIDERS_STATUS = PROVIDERS_BASE + "/status";
    
    // Health and monitoring
    public static final String HEALTH = API_BASE_PATH + "/health";
    public static final String METRICS = API_BASE_PATH + "/metrics";
    
    // Request parameters
    public static final String PARAM_COUNTRY = "country";
    public static final String PARAM_LANGUAGE = "language";
    public static final String PARAM_CATEGORY = "category";
    public static final String PARAM_KEYWORD = "keyword";
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_SIZE = "size";
    public static final String PARAM_SORT = "sort";
    
    // Default values
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
    public static final String DEFAULT_SORT = "publishedAt,desc";
    
    // Headers
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String CONTENT_TYPE_JSON = "application/json";
    
    // CORS
    public static final String CORS_ALLOWED_ORIGINS = "*";
    public static final String CORS_ALLOWED_METHODS = "GET,POST,PUT,DELETE,OPTIONS";
    public static final String CORS_ALLOWED_HEADERS = "Content-Type,Authorization,X-Requested-With";
}