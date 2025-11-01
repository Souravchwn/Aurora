package com.sourav.aurora.service;

import com.sourav.aurora.constants.ApplicationConstants;
import com.sourav.aurora.provider.NewsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing news providers following Single Responsibility Principle
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsProviderService {
    
    private final List<NewsProvider> newsProviders;
    
    /**
     * Get all enabled and healthy providers sorted by priority
     */
    public List<NewsProvider> getEnabledProviders() {
        return newsProviders.stream()
                .filter(NewsProvider::isEnabled)
                .filter(NewsProvider::isHealthy)
                .sorted(Comparator.comparingInt(NewsProvider::getPriority))
                .collect(Collectors.toList());
    }
    
    /**
     * Get all providers regardless of status
     */
    public List<NewsProvider> getAllProviders() {
        return newsProviders.stream()
                .sorted(Comparator.comparingInt(NewsProvider::getPriority))
                .collect(Collectors.toList());
    }
    
    /**
     * Get provider by name
     */
    public NewsProvider getProviderByName(String name) {
        return newsProviders.stream()
                .filter(provider -> provider.getProviderName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get provider health status
     */
    public Map<String, Object> getProviderHealthStatus() {
        Map<String, Object> healthStatus = newsProviders.stream()
                .collect(Collectors.toMap(
                        NewsProvider::getProviderName,
                        provider -> Map.of(
                                "enabled", provider.isEnabled(),
                                "healthy", provider.isHealthy(),
                                "priority", provider.getPriority(),
                                "lastSuccessfulFetch", provider.getLastSuccessfulFetch()
                        )
                ));
        
        long enabledCount = newsProviders.stream().filter(NewsProvider::isEnabled).count();
        long healthyCount = newsProviders.stream().filter(p -> p.isEnabled() && p.isHealthy()).count();
        
        healthStatus.put("summary", Map.of(
                "total", newsProviders.size(),
                "enabled", enabledCount,
                "healthy", healthyCount
        ));
        
        return healthStatus;
    }
    
    /**
     * Get supported countries from all providers
     */
    public List<String> getAllSupportedCountries() {
        return newsProviders.stream()
                .filter(NewsProvider::isEnabled)
                .flatMap(provider -> provider.getSupportedCountries().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Get supported languages from all providers
     */
    public List<String> getAllSupportedLanguages() {
        return newsProviders.stream()
                .filter(NewsProvider::isEnabled)
                .flatMap(provider -> provider.getSupportedLanguages().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Get supported categories from all providers
     */
    public List<String> getAllSupportedCategories() {
        return newsProviders.stream()
                .filter(NewsProvider::isEnabled)
                .flatMap(provider -> provider.getSupportedCategories().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Check if any providers are available
     */
    public boolean hasEnabledProviders() {
        return newsProviders.stream().anyMatch(NewsProvider::isEnabled);
    }
    
    /**
     * Check if any providers are healthy
     */
    public boolean hasHealthyProviders() {
        return newsProviders.stream().anyMatch(provider -> provider.isEnabled() && provider.isHealthy());
    }
    
    /**
     * Get provider statistics
     */
    public Map<String, Object> getProviderStatistics() {
        long totalProviders = newsProviders.size();
        long enabledProviders = newsProviders.stream().filter(NewsProvider::isEnabled).count();
        long healthyProviders = newsProviders.stream().filter(p -> p.isEnabled() && p.isHealthy()).count();
        
        return Map.of(
                "total", totalProviders,
                "enabled", enabledProviders,
                "healthy", healthyProviders,
                "disabled", totalProviders - enabledProviders,
                "unhealthy", enabledProviders - healthyProviders
        );
    }
    
    /**
     * Log provider status for monitoring
     */
    public void logProviderStatus() {
        log.info("Provider Status Summary:");
        newsProviders.forEach(provider -> {
            String status = provider.isEnabled() ? 
                    (provider.isHealthy() ? "ENABLED/HEALTHY" : "ENABLED/UNHEALTHY") : 
                    "DISABLED";
            log.info("  {} (Priority: {}) - {}", provider.getProviderName(), provider.getPriority(), status);
        });
    }
}