package com.sourav.aurora.service;

import com.sourav.aurora.constants.ApplicationConstants;
import com.sourav.aurora.dto.ArticleDto;
import com.sourav.aurora.dto.NewsResponse;
import com.sourav.aurora.exception.NewsProviderException;
import com.sourav.aurora.model.Article;
import com.sourav.aurora.provider.NewsProvider;
import com.sourav.aurora.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * News service implementing business logic with proper separation of concerns
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {
    
    private final ArticleRepository articleRepository;
    private final NewsProviderService providerService;
    private final SummarizerService summarizerService;
    
    @Cacheable(value = ApplicationConstants.CACHE_NEWS, 
               key = "#country + '_' + #language + '_' + #category + '_' + #keyword + '_' + #page + '_' + #size")
    public NewsResponse getNews(String country, String language, String category, String keyword, int page, int size) {
        log.info("Fetching news with filters - country: {}, language: {}, category: {}, keyword: {}, page: {}, size: {}", 
                country, language, category, keyword, page, size);
        
        // Validate and sanitize parameters
        page = Math.max(0, page);
        size = Math.min(Math.max(1, size), ApplicationConstants.MAX_SIZE);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt", "fetchedAt"));
        Page<Article> articlesPage = articleRepository.findArticlesWithFilters(country, language, category, keyword, pageable);
        
        List<ArticleDto> articleDtos = articlesPage.getContent().stream()
                .map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
        
        log.info(ApplicationConstants.INFO_CACHE_HIT + " - {} articles returned", articleDtos.size());
        
        return NewsResponse.builder()
                .articles(articleDtos)
                .totalPages(articlesPage.getTotalPages())
                .totalElements(articlesPage.getTotalElements())
                .currentPage(page)
                .pageSize(size)
                .hasNext(articlesPage.hasNext())
                .hasPrevious(articlesPage.hasPrevious())
                .availableCountries(articleRepository.findDistinctCountries())
                .availableLanguages(articleRepository.findDistinctLanguages())
                .availableCategories(articleRepository.findDistinctCategories())
                .availableSources(articleRepository.findDistinctSources())
                .build();
    }
    
    public List<ArticleDto> getTodaysNews() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Article> articles = articleRepository.findTodaysArticles(startOfDay);
        
        log.info("Retrieved {} articles for today", articles.size());
        
        return articles.stream()
                .map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @CacheEvict(value = ApplicationConstants.CACHE_NEWS, allEntries = true)
    @Transactional
    @Async("newsProviderExecutor")
    public CompletableFuture<Void> refreshNews(String country, String language, String category, String keyword) {
        return CompletableFuture.runAsync(() -> {
            log.info("Refreshing news from all providers with filters - country: {}, language: {}, category: {}, keyword: {}", 
                    country, language, category, keyword);
            
            List<NewsProvider> enabledProviders = providerService.getEnabledProviders();
            
            if (enabledProviders.isEmpty()) {
                log.warn(ApplicationConstants.WARN_NO_ARTICLES_FOUND + " - No enabled providers");
                return;
            }
            
            List<CompletableFuture<List<Article>>> futures = new ArrayList<>();
            
            for (NewsProvider provider : enabledProviders) {
                log.info("Fetching from provider: {} (priority: {})", provider.getProviderName(), provider.getPriority());
                CompletableFuture<List<Article>> future = provider.fetchNews(country, language, category, keyword)
                        .exceptionally(throwable -> {
                            if (throwable instanceof NewsProviderException) {
                                log.error("Provider {} failed: {}", provider.getProviderName(), throwable.getMessage());
                            } else {
                                log.error("Unexpected error from provider {}: {}", provider.getProviderName(), throwable.getMessage());
                            }
                            return new ArrayList<>();
                        });
                futures.add(future);
            }
            
            // Wait for all providers to complete with timeout
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            
            try {
                allFutures.join(); // Wait for completion
                
                List<Article> allArticles = new ArrayList<>();
                int successfulProviders = 0;
                
                for (int i = 0; i < futures.size(); i++) {
                    try {
                        List<Article> articles = futures.get(i).get();
                        if (!articles.isEmpty()) {
                            allArticles.addAll(articles);
                            successfulProviders++;
                        }
                    } catch (Exception e) {
                        log.error("Error getting articles from provider {}: {}", 
                                enabledProviders.get(i).getProviderName(), e.getMessage());
                    }
                }
                
                log.info("Fetched {} articles from {} successful providers out of {} total", 
                        allArticles.size(), successfulProviders, enabledProviders.size());
                
                if (!allArticles.isEmpty()) {
                    // Process and save articles
                    saveArticles(allArticles, country, language, category);
                } else {
                    log.warn(ApplicationConstants.WARN_NO_ARTICLES_FOUND);
                }
                
            } catch (Exception e) {
                log.error(ApplicationConstants.ERROR_NEWS_REFRESH_FAILED, e);
                throw new RuntimeException(ApplicationConstants.ERROR_NEWS_REFRESH_FAILED, e);
            }
        });
    }
    
    @Transactional
    private void saveArticles(List<Article> articles, String country, String language, String category) {
        int savedCount = 0;
        int duplicateCount = 0;
        int errorCount = 0;
        
        for (Article article : articles) {
            try {
                // Validate article
                if (!isValidArticle(article)) {
                    errorCount++;
                    continue;
                }
                
                // Check if article already exists
                if (articleRepository.findByUrl(article.getUrl()).isPresent()) {
                    duplicateCount++;
                    continue;
                }
                
                // Set filter values if not already set
                enrichArticle(article, country, language, category);
                
                // Generate summary
                if (article.getDescription() != null && !article.getDescription().isEmpty()) {
                    String summary = summarizerService.generateSummary(article.getDescription());
                    article.setSummary(summary);
                }
                
                articleRepository.save(article);
                savedCount++;
                
            } catch (Exception e) {
                log.error("Error saving article: {}", article.getUrl(), e);
                errorCount++;
            }
        }
        
        log.info("Article processing completed - Saved: {}, Duplicates: {}, Errors: {}", 
                savedCount, duplicateCount, errorCount);
    }
    
    private boolean isValidArticle(Article article) {
        return article != null &&
               article.getTitle() != null && !article.getTitle().trim().isEmpty() &&
               article.getUrl() != null && !article.getUrl().trim().isEmpty() &&
               article.getTitle().length() <= ApplicationConstants.MAX_TITLE_LENGTH &&
               article.getUrl().length() <= ApplicationConstants.MAX_URL_LENGTH &&
               (article.getDescription() == null || article.getDescription().length() <= ApplicationConstants.MAX_DESCRIPTION_LENGTH);
    }
    
    private void enrichArticle(Article article, String country, String language, String category) {
        if (article.getCountry() == null && country != null) {
            article.setCountry(country);
        }
        if (article.getLanguage() == null && language != null) {
            article.setLanguage(language);
        }
        if (article.getCategory() == null && category != null) {
            article.setCategory(category);
        }
        
        // Set defaults if still null
        if (article.getCountry() == null) {
            article.setCountry(ApplicationConstants.DEFAULT_COUNTRY);
        }
        if (article.getLanguage() == null) {
            article.setLanguage(ApplicationConstants.DEFAULT_LANGUAGE);
        }
        if (article.getCategory() == null) {
            article.setCategory(ApplicationConstants.DEFAULT_CATEGORY);
        }
    }
    
    public List<String> getActiveProviders() {
        return providerService.getEnabledProviders().stream()
                .map(NewsProvider::getProviderName)
                .collect(Collectors.toList());
    }
    
    public List<String> getAllProviders() {
        return providerService.getAllProviders().stream()
                .map(provider -> {
                    String status = provider.isEnabled() ? 
                            (provider.isHealthy() ? "enabled" : "enabled (unhealthy)") : 
                            "disabled";
                    return provider.getProviderName() + " (" + status + ")";
                })
                .collect(Collectors.toList());
    }
    
    public Map<String, Object> getProviderHealthStatus() {
        return providerService.getProviderHealthStatus();
    }
    
    public NewsResponse searchNews(String query, int page, int size) {
        if (query == null || query.trim().length() < ApplicationConstants.MIN_KEYWORD_LENGTH) {
            throw new IllegalArgumentException("Search query must be at least " + 
                    ApplicationConstants.MIN_KEYWORD_LENGTH + " characters long");
        }
        
        return getNews(null, null, null, query.trim(), page, size);
    }
    
    @CacheEvict(value = ApplicationConstants.CACHE_NEWS, allEntries = true)
    public void clearCache() {
        log.info("News cache cleared manually");
    }
}