package com.sourav.aurora.provider;

import com.sourav.aurora.constants.ApplicationConstants;
import com.sourav.aurora.exception.NewsProviderException;
import com.sourav.aurora.model.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract base class for news providers implementing Template Method pattern
 */
@Slf4j
public abstract class AbstractNewsProvider implements NewsProvider {
    
    protected final WebClient webClient;
    protected final AtomicLong lastSuccessfulFetch = new AtomicLong(0);
    protected volatile boolean healthy = true;
    
    protected AbstractNewsProvider(WebClient webClient) {
        this.webClient = webClient;
    }
    
    @Override
    public CompletableFuture<List<Article>> fetchNews(String country, String language, String category, String keyword) {
        return fetchNews(country, language, category, keyword, 1, getConfig().getMaxArticles());
    }
    
    @Override
    public CompletableFuture<List<Article>> fetchNews(String country, String language, String category, String keyword, int page, int pageSize) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!isEnabled()) {
                    log.warn("Provider {} is disabled", getProviderName());
                    return new ArrayList<>();
                }
                
                String url = buildUrl(country, language, category, keyword, page, pageSize);
                log.info("Fetching news from {}: {}", getProviderName(), url);
                
                String response = webClient.get()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofMillis(getConfig().getTimeout()))
                        .onErrorResume(WebClientResponseException.class, ex -> {
                            log.error("HTTP error from {}: {} - {}", getProviderName(), ex.getStatusCode(), ex.getResponseBodyAsString());
                            return Mono.error(new NewsProviderException(getProviderName(), "HTTP_ERROR", 
                                    "HTTP " + ex.getStatusCode() + ": " + ex.getResponseBodyAsString(), ex));
                        })
                        .onErrorResume(Exception.class, ex -> {
                            log.error("Network error from {}: {}", getProviderName(), ex.getMessage());
                            return Mono.error(new NewsProviderException(getProviderName(), "NETWORK_ERROR", 
                                    ApplicationConstants.ERROR_NETWORK_TIMEOUT, ex));
                        })
                        .block();
                
                List<Article> articles = parseResponse(response, country, language, category);
                
                // Update health status
                healthy = true;
                lastSuccessfulFetch.set(System.currentTimeMillis());
                
                log.info("Successfully fetched {} articles from {}", articles.size(), getProviderName());
                return articles;
                
            } catch (Exception e) {
                healthy = false;
                log.error("Error fetching news from {}: {}", getProviderName(), e.getMessage(), e);
                
                if (e instanceof NewsProviderException) {
                    throw e;
                }
                
                throw new NewsProviderException(getProviderName(), ApplicationConstants.ERROR_NEWS_FETCH_FAILED, e);
            }
        });
    }
    
    @Override
    public boolean isHealthy() {
        // Consider provider unhealthy if last successful fetch was more than 2 hours ago
        long twoHoursAgo = System.currentTimeMillis() - (2 * 60 * 60 * 1000);
        return healthy && lastSuccessfulFetch.get() > twoHoursAgo;
    }
    
    @Override
    public long getLastSuccessfulFetch() {
        return lastSuccessfulFetch.get();
    }
    
    @Override
    public List<String> getSupportedCountries() {
        return Arrays.asList(ApplicationConstants.SUPPORTED_COUNTRIES);
    }
    
    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(ApplicationConstants.SUPPORTED_LANGUAGES);
    }
    
    @Override
    public List<String> getSupportedCategories() {
        return Arrays.asList(ApplicationConstants.SUPPORTED_CATEGORIES);
    }
    
    /**
     * Template method for building API URL - to be implemented by concrete providers
     */
    protected abstract String buildUrl(String country, String language, String category, String keyword, int page, int pageSize);
    
    /**
     * Template method for parsing API response - to be implemented by concrete providers
     */
    protected abstract List<Article> parseResponse(String response, String country, String language, String category);
    
    /**
     * Helper method to validate and filter supported values
     */
    protected boolean isSupported(String value, String[] supportedValues) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return Arrays.asList(supportedValues).contains(value.toLowerCase());
    }
    
    /**
     * Helper method to create Article with common fields
     */
    protected Article createArticle(String title, String description, String url, String source, 
                                  LocalDateTime publishedAt, String imageUrl, String country, 
                                  String language, String category) {
        return Article.builder()
                .title(title)
                .description(description)
                .url(url)
                .source(source != null ? source : getProviderName())
                .publishedAt(publishedAt)
                .imageUrl(imageUrl)
                .country(country)
                .language(language)
                .category(category)
                .fetchedAt(LocalDateTime.now())
                .build();
    }
}