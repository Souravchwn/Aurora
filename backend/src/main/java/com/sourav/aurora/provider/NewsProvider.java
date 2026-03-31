package com.sourav.aurora.provider;

import com.sourav.aurora.model.Article;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for news providers following Strategy pattern
 */
public interface NewsProvider {
    
    /**
     * Get the name of this news provider
     */
    String getProviderName();
    
    /**
     * Check if this provider is enabled and configured
     */
    boolean isEnabled();
    
    /**
     * Get provider priority (lower number = higher priority)
     */
    int getPriority();
    
    /**
     * Fetch news articles asynchronously using virtual threads
     */
    CompletableFuture<List<Article>> fetchNews(String country, String language, String category, String keyword);
    
    /**
     * Fetch news articles with pagination
     */
    CompletableFuture<List<Article>> fetchNews(String country, String language, String category, String keyword, int page, int pageSize);
    
    /**
     * Get supported countries by this provider
     */
    List<String> getSupportedCountries();
    
    /**
     * Get supported languages by this provider
     */
    List<String> getSupportedLanguages();
    
    /**
     * Get supported categories by this provider
     */
    List<String> getSupportedCategories();
    
    /**
     * Get provider health status
     */
    boolean isHealthy();
    
    /**
     * Get last successful fetch timestamp
     */
    long getLastSuccessfulFetch();
    
    /**
     * Get provider configuration
     */
    ProviderConfig getConfig();
    
    /**
     * Provider configuration interface
     */
    interface ProviderConfig {
        String getBaseUrl();
        int getTimeout();
        int getMaxArticles();
        String getApiKey();
    }
}