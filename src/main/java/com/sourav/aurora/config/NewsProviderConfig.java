package com.sourav.aurora.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration properties for news providers
 */
@Configuration
@ConfigurationProperties(prefix = "news")
@Data
public class NewsProviderConfig {
    
    private Map<String, String> apikeys;
    private Cache cache = new Cache();
    private Scheduler scheduler = new Scheduler();
    private Map<String, ProviderSettings> providers;
    
    @Data
    public static class Cache {
        private int ttl = 3600; // 1 hour
    }
    
    @Data
    public static class Scheduler {
        private Cleanup cleanup = new Cleanup();
        private Refresh refresh = new Refresh();
        
        @Data
        public static class Cleanup {
            private boolean enabled = true;
            private String cron = "0 0 2 * * ?"; // Daily at 2 AM
            private int retentionDays = 7;
        }
        
        @Data
        public static class Refresh {
            private boolean enabled = true;
            private String cron = "0 */30 * * * ?"; // Every 30 minutes
        }
    }
    
    @Data
    public static class ProviderSettings {
        private boolean enabled = true;
        private String baseUrl;
        private int timeout = 30000; // 30 seconds
        private int maxArticles = 100;
    }
}