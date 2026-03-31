package com.sourav.aurora.controller;

import com.sourav.aurora.constants.ApiConstants;
import com.sourav.aurora.constants.ApplicationConstants;
import com.sourav.aurora.dto.ArticleDto;
import com.sourav.aurora.dto.NewsResponse;
import com.sourav.aurora.service.NewsService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for Aurora News Service following MVC pattern
 */
@RestController
@RequestMapping(ApiConstants.API_BASE_PATH)
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(
    origins = ApiConstants.CORS_ALLOWED_ORIGINS,
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = ApiConstants.CORS_ALLOWED_HEADERS
)
public class NewsController {
    
    private final NewsService newsService;
    
    @GetMapping("/news")
    public ResponseEntity<NewsResponse> getNews(
            @RequestParam(name = ApiConstants.PARAM_COUNTRY, required = false) String country,
            @RequestParam(name = ApiConstants.PARAM_LANGUAGE, required = false) String language,
            @RequestParam(name = ApiConstants.PARAM_CATEGORY, required = false) String category,
            @RequestParam(name = ApiConstants.PARAM_KEYWORD, required = false) String keyword,
            @RequestParam(name = ApiConstants.PARAM_PAGE, defaultValue = "0") 
            @Min(0) int page,
            @RequestParam(name = ApiConstants.PARAM_SIZE, defaultValue = "20") 
            @Min(1) @Max(100) int size) {
        
        log.info("GET {} - country: {}, language: {}, category: {}, keyword: {}, page: {}, size: {}", 
                ApiConstants.NEWS_BASE, country, language, category, keyword, page, size);
        
        NewsResponse response = newsService.getNews(country, language, category, keyword, page, size);
        
        log.info(ApplicationConstants.SUCCESS_NEWS_FETCHED + " - {} articles returned", response.getArticles().size());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/news/today")
    public ResponseEntity<List<ArticleDto>> getTodaysNews() {
        log.info("GET {}", ApiConstants.NEWS_TODAY);
        
        List<ArticleDto> articles = newsService.getTodaysNews();
        
        log.info(ApplicationConstants.SUCCESS_NEWS_FETCHED + " - {} today's articles", articles.size());
        return ResponseEntity.ok(articles);
    }
    
    @GetMapping("/news/search")
    public ResponseEntity<NewsResponse> searchNews(
            @RequestParam(name = ApiConstants.PARAM_KEYWORD, required = true) String keyword,
            @RequestParam(name = ApiConstants.PARAM_PAGE, defaultValue = "0") 
            @Min(0) int page,
            @RequestParam(name = ApiConstants.PARAM_SIZE, defaultValue = "20") 
            @Min(1) @Max(100) int size) {
        
        log.info("GET {} - keyword: {}, page: {}, size: {}", 
                ApiConstants.NEWS_SEARCH, keyword, page, size);
        
        NewsResponse response = newsService.searchNews(keyword, page, size);
        
        log.info("Search completed - {} articles found for keyword: {}", response.getTotalElements(), keyword);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/news/refresh")
    public ResponseEntity<Map<String, Object>> refreshNews(
            @RequestParam(name = ApiConstants.PARAM_COUNTRY, required = false) String country,
            @RequestParam(name = ApiConstants.PARAM_LANGUAGE, required = false) String language,
            @RequestParam(name = ApiConstants.PARAM_CATEGORY, required = false) String category,
            @RequestParam(name = ApiConstants.PARAM_KEYWORD, required = false) String keyword) {
        
        log.info("POST {} - country: {}, language: {}, category: {}, keyword: {}", 
                ApiConstants.NEWS_REFRESH, country, language, category, keyword);
        
        try {
            CompletableFuture<Void> refreshFuture = newsService.refreshNews(country, language, category, keyword);
            
            return ResponseEntity.ok(Map.of(
                    "status", ApplicationConstants.STATUS_SUCCESS,
                    "message", ApplicationConstants.SUCCESS_NEWS_REFRESHED,
                    "timestamp", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            log.error(ApplicationConstants.ERROR_NEWS_REFRESH_FAILED, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", ApplicationConstants.STATUS_ERROR,
                            "message", ApplicationConstants.ERROR_NEWS_REFRESH_FAILED + ": " + e.getMessage(),
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }
    
    @PostMapping("/news/cache/clear")
    public ResponseEntity<Map<String, Object>> clearCache() {
        log.info("POST /api/news/cache/clear");
        
        try {
            newsService.clearCache();
            return ResponseEntity.ok(Map.of(
                    "status", ApplicationConstants.STATUS_SUCCESS,
                    "message", ApplicationConstants.SUCCESS_CACHE_CLEARED,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error(ApplicationConstants.ERROR_CACHE_OPERATION, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", ApplicationConstants.STATUS_ERROR,
                            "message", ApplicationConstants.ERROR_CACHE_OPERATION,
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }
    
    @GetMapping("/providers")
    public ResponseEntity<Map<String, Object>> getProviders() {
        log.info("GET {}", ApiConstants.PROVIDERS_BASE);
        
        List<String> activeProviders = newsService.getActiveProviders();
        List<String> allProviders = newsService.getAllProviders();
        
        log.info(ApplicationConstants.SUCCESS_PROVIDERS_LOADED + " - {} active, {} total", 
                activeProviders.size(), allProviders.size());
        
        return ResponseEntity.ok(Map.of(
                "active", activeProviders,
                "all", allProviders,
                "timestamp", LocalDateTime.now(),
                "status", ApplicationConstants.STATUS_SUCCESS
        ));
    }
    
    @GetMapping("/providers/active")
    public ResponseEntity<List<String>> getActiveProviders() {
        log.info("GET {}", ApiConstants.PROVIDERS_ACTIVE);
        
        List<String> activeProviders = newsService.getActiveProviders();
        return ResponseEntity.ok(activeProviders);
    }
    
    @GetMapping("/providers/status")
    public ResponseEntity<Map<String, Object>> getProvidersStatus() {
        log.info("GET {}", ApiConstants.PROVIDERS_STATUS);
        
        List<String> allProviders = newsService.getAllProviders();
        List<String> activeProviders = newsService.getActiveProviders();
        
        return ResponseEntity.ok(Map.of(
                "providers", allProviders,
                "activeCount", activeProviders.size(),
                "totalCount", allProviders.size(),
                "timestamp", LocalDateTime.now()
        ));
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("GET {}", ApiConstants.HEALTH);
        
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Aurora News Service",
                "version", "2.0",
                "timestamp", LocalDateTime.now(),
                "providers", newsService.getActiveProviders().size()
        ));
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        log.info("GET {}", ApiConstants.METRICS);
        
        // Basic metrics - can be enhanced with Micrometer
        return ResponseEntity.ok(Map.of(
                "activeProviders", newsService.getActiveProviders().size(),
                "totalProviders", newsService.getAllProviders().size(),
                "timestamp", LocalDateTime.now(),
                "uptime", java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime()
        ));
    }
}