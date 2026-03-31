package com.sourav.aurora.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class VirtualThreadConfig {
    
    @Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        log.info("Configuring Virtual Thread Executor for Java 25");
        
        try {
            // Use virtual threads if available (Java 21+)
            return java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();
        } catch (Exception e) {
            log.warn("Virtual threads not available, falling back to regular thread pool", e);
            
            // Fallback to regular thread pool
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(10);
            executor.setMaxPoolSize(50);
            executor.setQueueCapacity(100);
            executor.setThreadNamePrefix("Aurora-");
            executor.initialize();
            return executor;
        }
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }
    
    @Bean(name = "newsProviderExecutor")
    public Executor newsProviderExecutor() {
        log.info("Configuring News Provider Executor with Virtual Threads");
        
        try {
            return java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();
        } catch (Exception e) {
            log.warn("Virtual threads not available for news providers, using thread pool", e);
            
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(5);
            executor.setMaxPoolSize(20);
            executor.setQueueCapacity(50);
            executor.setThreadNamePrefix("NewsProvider-");
            executor.initialize();
            return executor;
        }
    }
    
    @Bean
    public com.fasterxml.jackson.databind.ObjectMapper objectMapper() {
        return new com.fasterxml.jackson.databind.ObjectMapper()
                .findAndRegisterModules()
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}