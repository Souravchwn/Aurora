package com.sourav.aurora.service;

import com.sourav.aurora.config.NewsProviderConfig;
import com.sourav.aurora.constants.ApplicationConstants;
import com.sourav.aurora.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Scheduler service for automatic news operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsSchedulerService {
    
    private final NewsService newsService;
    private final NewsProviderService providerService;
    private final ArticleRepository articleRepository;
    private final NewsProviderConfig config;
    
    /**
     * Automatically refresh news from all providers
     * Runs based on configuration (default: every 30 minutes)
     */
    @Scheduled(cron = "${news.scheduler.refresh.cron:0 */30 * * * ?}")
    @ConditionalOnProperty(name = "news.scheduler.refresh.enabled", havingValue = "true", matchIfMissing = true)
    public void refreshNewsAutomatically() {
        log.info("Starting automatic news refresh");
        
        try {
            // Refresh news with default parameters to get general news
            newsService.refreshNews(
                ApplicationConstants.DEFAULT_COUNTRY,
                ApplicationConstants.DEFAULT_LANGUAGE,
                ApplicationConstants.DEFAULT_CATEGORY,
                null
            );
            
            // Also refresh some popular categories
            String[] popularCategories = {"technology", "business", "health", "sports"};
            for (String category : popularCategories) {
                try {
                    newsService.refreshNews(
                        ApplicationConstants.DEFAULT_COUNTRY,
                        ApplicationConstants.DEFAULT_LANGUAGE,
                        category,
                        null
                    );
                    
                    // Small delay between requests to avoid rate limiting
                    Thread.sleep(1000);
                    
                } catch (Exception e) {
                    log.warn("Failed to refresh news for category {}: {}", category, e.getMessage());
                }
            }
            
            log.info("Automatic news refresh completed successfully");
            
        } catch (Exception e) {
            log.error("Error during automatic news refresh", e);
        }
    }
    
    /**
     * Clean up old news articles
     * Runs based on configuration (default: daily at 2 AM)
     */
    @Scheduled(cron = "${news.scheduler.cleanup.cron:0 0 2 * * ?}")
    @ConditionalOnProperty(name = "news.scheduler.cleanup.enabled", havingValue = "true", matchIfMissing = true)
    @Transactional
    public void cleanupOldNews() {
        log.info("Starting cleanup of old news articles");
        
        try {
            int retentionDays = config.getScheduler().getCleanup().getRetentionDays();
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            
            // Count articles to be deleted
            long countBefore = articleRepository.count();
            
            // Delete old articles
            int deletedCount = articleRepository.deleteByFetchedAtBefore(cutoffDate);
            
            long countAfter = articleRepository.count();
            
            log.info("Cleanup completed: deleted {} old articles (older than {} days). " +
                    "Articles before: {}, after: {}", 
                    deletedCount, retentionDays, countBefore, countAfter);
            
        } catch (Exception e) {
            log.error("Error during news cleanup", e);
        }
    }
    
    /**
     * Health check for news providers
     * Runs every hour to monitor provider health
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void checkProviderHealth() {
        log.debug("Checking news provider health");
        
        try {
            providerService.logProviderStatus();
            
            if (!providerService.hasHealthyProviders()) {
                log.warn("No healthy news providers available!");
            }
            
            var stats = providerService.getProviderStatistics();
            log.info("Provider statistics: {}", stats);
            
        } catch (Exception e) {
            log.error("Error during provider health check", e);
        }
    }
    
    /**
     * Refresh trending topics and keywords
     * Runs every 2 hours to get trending news
     */
    @Scheduled(fixedRate = 7200000) // Every 2 hours
    public void refreshTrendingNews() {
        log.info("Refreshing trending news topics");
        
        try {
            // Popular trending keywords
            String[] trendingKeywords = {
                "AI", "technology", "climate", "economy", "health", 
                "politics", "innovation", "science", "energy", "finance"
            };
            
            for (String keyword : trendingKeywords) {
                try {
                    newsService.refreshNews(null, "en", null, keyword);
                    
                    // Small delay to avoid rate limiting
                    Thread.sleep(2000);
                    
                } catch (Exception e) {
                    log.warn("Failed to refresh trending news for keyword {}: {}", keyword, e.getMessage());
                }
            }
            
            log.info("Trending news refresh completed");
            
        } catch (Exception e) {
            log.error("Error during trending news refresh", e);
        }
    }
    
    /**
     * Cache warming - preload popular news combinations
     * Runs every 4 hours to warm up the cache
     */
    @Scheduled(fixedRate = 14400000) // Every 4 hours
    public void warmupCache() {
        log.info("Starting cache warmup");
        
        try {
            // Popular country/language combinations
            String[][] popularCombinations = {
                {"us", "en"}, {"gb", "en"}, {"ca", "en"}, {"au", "en"},
                {"de", "de"}, {"fr", "fr"}, {"it", "it"}, {"es", "es"}
            };
            
            for (String[] combo : popularCombinations) {
                try {
                    // Preload first page of general news
                    newsService.getNews(combo[0], combo[1], "general", null, 0, 20);
                    
                    Thread.sleep(500); // Small delay
                    
                } catch (Exception e) {
                    log.warn("Failed to warm cache for {}/{}: {}", combo[0], combo[1], e.getMessage());
                }
            }
            
            log.info("Cache warmup completed");
            
        } catch (Exception e) {
            log.error("Error during cache warmup", e);
        }
    }
}