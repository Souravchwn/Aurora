package com.sourav.aurora.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourav.aurora.config.NewsProviderConfig;
import com.sourav.aurora.constants.ApplicationConstants;
import com.sourav.aurora.exception.NewsProviderException;
import com.sourav.aurora.model.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * NewsAPI provider implementation using real API calls with virtual threads
 */
@Component
@Slf4j
public class NewsApiProvider extends AbstractNewsProvider {
    
    private static final String PROVIDER_NAME = ApplicationConstants.PROVIDER_NEWSAPI;
    private static final int PRIORITY = 1; // High priority
    
    private final NewsProviderConfig config;
    private final ObjectMapper objectMapper;
    private final ProviderConfig providerConfig;
    
    @Autowired
    public NewsApiProvider(WebClient webClient, NewsProviderConfig config, ObjectMapper objectMapper) {
        super(webClient);
        this.config = config;
        this.objectMapper = objectMapper;
        this.providerConfig = new NewsApiConfig();
    }
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public int getPriority() {
        return PRIORITY;
    }
    
    @Override
    public boolean isEnabled() {
        String apiKey = config.getApikeys().get("newsapi");
        boolean hasValidKey = apiKey != null && !apiKey.isEmpty() && !"your_newsapi_key_here".equals(apiKey);
        boolean providerEnabled = config.getProviders() != null && 
                                config.getProviders().containsKey("newsapi") && 
                                config.getProviders().get("newsapi").isEnabled();
        return hasValidKey && providerEnabled;
    }
    
    @Override
    public ProviderConfig getConfig() {
        return providerConfig;
    }
    
    @Override
    protected String buildUrl(String country, String language, String category, String keyword, int page, int pageSize) {
        String apiKey = config.getApikeys().get("newsapi");
        String baseUrl = config.getProviders().get("newsapi").getBaseUrl();
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/top-headlines")
                .queryParam("apiKey", apiKey)
                .queryParam("pageSize", Math.min(pageSize, 100))
                .queryParam("page", page);
        
        // Add filters only if they are supported
        if (isSupported(country, ApplicationConstants.SUPPORTED_COUNTRIES)) {
            builder.queryParam("country", country.toLowerCase());
        }
        
        if (isSupported(language, ApplicationConstants.SUPPORTED_LANGUAGES)) {
            builder.queryParam("language", language.toLowerCase());
        }
        
        if (isSupported(category, ApplicationConstants.SUPPORTED_CATEGORIES)) {
            // Map some categories for NewsAPI compatibility
            String mappedCategory = mapCategory(category);
            builder.queryParam("category", mappedCategory);
        }
        
        if (keyword != null && keyword.trim().length() >= ApplicationConstants.MIN_KEYWORD_LENGTH) {
            builder.queryParam("q", keyword.trim());
        }
        
        return builder.build().toUriString();
    }
    
    @Override
    protected List<Article> parseResponse(String response, String country, String language, String category) {
        List<Article> articles = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(response);
            
            // Check for API errors
            String status = getTextValue(root, "status");
            if (!"ok".equals(status)) {
                String errorCode = getTextValue(root, "code");
                String errorMessage = getTextValue(root, "message");
                throw new NewsProviderException(PROVIDER_NAME, errorCode, 
                        "NewsAPI error: " + errorMessage);
            }
            
            JsonNode articlesNode = root.get("articles");
            if (articlesNode != null && articlesNode.isArray()) {
                for (JsonNode articleNode : articlesNode) {
                    Article article = parseArticle(articleNode, country, language, category);
                    if (article != null) {
                        articles.add(article);
                    }
                }
            }
            
        } catch (NewsProviderException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error parsing NewsAPI response", e);
            throw new NewsProviderException(PROVIDER_NAME, "PARSE_ERROR", 
                    ApplicationConstants.ERROR_PARSING_RESPONSE, e);
        }
        
        return articles;
    }
    
    private Article parseArticle(JsonNode articleNode, String country, String language, String category) {
        try {
            String title = getTextValue(articleNode, "title");
            String url = getTextValue(articleNode, "url");
            
            // Skip articles with missing essential fields
            if (title == null || url == null || title.trim().isEmpty() || url.trim().isEmpty()) {
                return null;
            }
            
            // Skip removed articles
            if (title.contains("[Removed]") || url.contains("[Removed]")) {
                return null;
            }
            
            String description = getTextValue(articleNode, "description");
            String imageUrl = getTextValue(articleNode, "urlToImage");
            String publishedAtStr = getTextValue(articleNode, "publishedAt");
            
            JsonNode sourceNode = articleNode.get("source");
            String source = sourceNode != null ? getTextValue(sourceNode, "name") : PROVIDER_NAME;
            
            LocalDateTime publishedAt = parsePublishedDate(publishedAtStr);
            
            return createArticle(title, description, url, source, publishedAt, imageUrl, 
                               country, language, category);
                               
        } catch (Exception e) {
            log.warn("Error parsing individual article from NewsAPI", e);
            return null;
        }
    }
    
    private LocalDateTime parsePublishedDate(String publishedAtStr) {
        if (publishedAtStr == null || publishedAtStr.trim().isEmpty()) {
            return LocalDateTime.now();
        }
        
        try {
            return LocalDateTime.parse(publishedAtStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("Could not parse published date: {}", publishedAtStr);
            return LocalDateTime.now();
        }
    }
    
    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asText() : null;
    }
    
    private String mapCategory(String category) {
        // NewsAPI has specific category names
        return switch (category.toLowerCase()) {
            case "world", "nation" -> "general";
            default -> category.toLowerCase();
        };
    }
    
    /**
     * NewsAPI specific configuration
     */
    private class NewsApiConfig implements ProviderConfig {
        @Override
        public String getBaseUrl() {
            return config.getProviders().get("newsapi").getBaseUrl();
        }
        
        @Override
        public int getTimeout() {
            return config.getProviders().get("newsapi").getTimeout();
        }
        
        @Override
        public int getMaxArticles() {
            return config.getProviders().get("newsapi").getMaxArticles();
        }
        
        @Override
        public String getApiKey() {
            return config.getApikeys().get("newsapi");
        }
    }
}