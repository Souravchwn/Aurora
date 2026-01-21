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
 * GNews provider implementation using real API calls with virtual threads
 */
@Component
@Slf4j
public class GNewsProvider extends AbstractNewsProvider {
    
    private static final String PROVIDER_NAME = ApplicationConstants.PROVIDER_GNEWS;
    private static final int PRIORITY = 2; // Lower priority than NewsAPI
    
    private final NewsProviderConfig config;
    private final ObjectMapper objectMapper;
    private final ProviderConfig providerConfig;
    
    @Autowired
    public GNewsProvider(WebClient webClient, NewsProviderConfig config, ObjectMapper objectMapper) {
        super(webClient);
        this.config = config;
        this.objectMapper = objectMapper;
        this.providerConfig = new GNewsConfig();
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
        String apiKey = config.getApikeys().get("gnews");
        boolean hasValidKey = apiKey != null && !apiKey.isEmpty() && !"your_gnews_key_here".equals(apiKey);
        boolean providerEnabled = config.getProviders() != null && 
                                config.getProviders().containsKey("gnews") && 
                                config.getProviders().get("gnews").isEnabled();
        return hasValidKey && providerEnabled;
    }
    
    @Override
    public ProviderConfig getConfig() {
        return providerConfig;
    }
    
    @Override
    protected String buildUrl(String country, String language, String category, String keyword, int page, int pageSize) {
        String apiKey = config.getApikeys().get("gnews");
        String baseUrl = config.getProviders().get("gnews").getBaseUrl();
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/top-headlines")
                .queryParam("apikey", apiKey)
                .queryParam("max", Math.min(pageSize, 100));
        
        // Add filters only if they are supported
        if (isSupported(country, ApplicationConstants.SUPPORTED_COUNTRIES)) {
            builder.queryParam("country", country.toLowerCase());
        }
        
        if (isSupported(language, ApplicationConstants.SUPPORTED_LANGUAGES)) {
            builder.queryParam("lang", language.toLowerCase());
        }
        
        if (isSupported(category, ApplicationConstants.SUPPORTED_CATEGORIES)) {
            // Map some categories for GNews compatibility
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
            JsonNode errorNode = root.get("error");
            if (errorNode != null) {
                String errorMessage = errorNode.asText();
                throw new NewsProviderException(PROVIDER_NAME, "API_ERROR", 
                        "GNews API error: " + errorMessage);
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
            log.error("Error parsing GNews response", e);
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
            
            String description = getTextValue(articleNode, "description");
            String imageUrl = getTextValue(articleNode, "image");
            String publishedAtStr = getTextValue(articleNode, "publishedAt");
            
            JsonNode sourceNode = articleNode.get("source");
            String source = sourceNode != null ? getTextValue(sourceNode, "name") : PROVIDER_NAME;
            
            LocalDateTime publishedAt = parsePublishedDate(publishedAtStr);
            
            return createArticle(title, description, url, source, publishedAt, imageUrl, 
                               country, language, category);
                               
        } catch (Exception e) {
            log.warn("Error parsing individual article from GNews", e);
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
        // GNews has different category names
        return switch (category.toLowerCase()) {
            case "general" -> "world";
            case "entertainment" -> "entertainment";
            case "health" -> "health";
            case "science" -> "science";
            case "sports" -> "sports";
            case "technology" -> "technology";
            case "business" -> "business";
            default -> "world";
        };
    }
    
    /**
     * GNews specific configuration
     */
    private class GNewsConfig implements ProviderConfig {
        @Override
        public String getBaseUrl() {
            return config.getProviders().get("gnews").getBaseUrl();
        }
        
        @Override
        public int getTimeout() {
            return config.getProviders().get("gnews").getTimeout();
        }
        
        @Override
        public int getMaxArticles() {
            return config.getProviders().get("gnews").getMaxArticles();
        }
        
        @Override
        public String getApiKey() {
            return config.getApikeys().get("gnews");
        }
    }
}