package com.sourav.aurora.constants;

/**
 * Application constants for Aurora News Service
 */
public final class ApplicationConstants {
    
    private ApplicationConstants() {
        // Utility class
    }
    
    // Success Messages
    public static final String SUCCESS_NEWS_FETCHED = "News articles fetched successfully";
    public static final String SUCCESS_NEWS_REFRESHED = "News refresh initiated successfully";
    public static final String SUCCESS_CACHE_CLEARED = "Cache cleared successfully";
    public static final String SUCCESS_PROVIDERS_LOADED = "News providers loaded successfully";
    
    // Error Messages
    public static final String ERROR_NEWS_FETCH_FAILED = "Failed to fetch news articles";
    public static final String ERROR_NEWS_REFRESH_FAILED = "Failed to refresh news";
    public static final String ERROR_PROVIDER_UNAVAILABLE = "News provider is currently unavailable";
    public static final String ERROR_INVALID_PARAMETERS = "Invalid request parameters provided";
    public static final String ERROR_API_KEY_MISSING = "API key is missing or invalid";
    public static final String ERROR_RATE_LIMIT_EXCEEDED = "API rate limit exceeded";
    public static final String ERROR_NETWORK_TIMEOUT = "Network timeout occurred";
    public static final String ERROR_PARSING_RESPONSE = "Failed to parse API response";
    public static final String ERROR_DATABASE_CONNECTION = "Database connection error";
    public static final String ERROR_CACHE_OPERATION = "Cache operation failed";
    public static final String ERROR_INTERNAL_SERVER = "Internal server error occurred";
    
    // Warning Messages
    public static final String WARN_NO_ARTICLES_FOUND = "No articles found for the given criteria";
    public static final String WARN_PROVIDER_DISABLED = "News provider is disabled";
    public static final String WARN_CACHE_MISS = "Cache miss, fetching from providers";
    public static final String WARN_PARTIAL_RESULTS = "Partial results returned due to provider errors";
    
    // Info Messages
    public static final String INFO_CACHE_HIT = "Results served from cache";
    public static final String INFO_PROVIDER_ENABLED = "News provider enabled";
    public static final String INFO_SCHEDULER_STARTED = "News scheduler started";
    public static final String INFO_CLEANUP_COMPLETED = "Old news cleanup completed";
    
    // Provider Names
    public static final String PROVIDER_NEWSAPI = "NewsAPI";
    public static final String PROVIDER_GNEWS = "GNews";
    public static final String PROVIDER_UNKNOWN = "Unknown";
    
    // Cache Keys
    public static final String CACHE_NEWS = "news";
    public static final String CACHE_PROVIDERS = "providers";
    public static final String CACHE_COUNTRIES = "countries";
    public static final String CACHE_LANGUAGES = "languages";
    public static final String CACHE_CATEGORIES = "categories";
    
    // Scheduler Names
    public static final String SCHEDULER_NEWS_REFRESH = "newsRefreshScheduler";
    public static final String SCHEDULER_CLEANUP = "newsCleanupScheduler";
    
    // Default Values
    public static final String DEFAULT_COUNTRY = "us";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_CATEGORY = "general";
    public static final int DEFAULT_SUMMARY_LENGTH = 230;
    public static final int DEFAULT_CACHE_TTL = 3600; // 1 hour
    public static final int DEFAULT_RETENTION_DAYS = 7;
    
    // Supported Values
    public static final String[] SUPPORTED_COUNTRIES = {
        "us", "gb", "ca", "au", "de", "fr", "it", "jp", "kr", "in", "br", "mx", "ru", "cn", "ae", "sa"
    };
    
    public static final String[] SUPPORTED_LANGUAGES = {
        "en", "de", "fr", "it", "es", "pt", "ru", "ja", "ko", "zh", "ar", "he", "hi", "nl", "no", "sv"
    };
    
    public static final String[] SUPPORTED_CATEGORIES = {
        "business", "entertainment", "general", "health", "science", "sports", "technology", "world", "nation"
    };
    
    // HTTP Status Messages
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_WARNING = "warning";
    public static final String STATUS_INFO = "info";
    
    // Date Formats
    public static final String DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATE_FORMAT_DISPLAY = "MMM dd, yyyy HH:mm";
    
    // Validation
    public static final int MIN_KEYWORD_LENGTH = 2;
    public static final int MAX_KEYWORD_LENGTH = 100;
    public static final int MAX_TITLE_LENGTH = 500;
    public static final int MAX_DESCRIPTION_LENGTH = 2000;
    public static final int MAX_URL_LENGTH = 1000;
}