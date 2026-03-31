package com.sourav.aurora.repository;

import com.sourav.aurora.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    Optional<Article> findByUrl(String url);
    
    List<Article> findByFetchedAtAfter(LocalDateTime dateTime);
    
    @Query("SELECT a FROM Article a WHERE " +
           "(:country IS NULL OR a.country = :country) AND " +
           "(:language IS NULL OR a.language = :language) AND " +
           "(:category IS NULL OR a.category = :category) AND " +
           "(:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY a.publishedAt DESC, a.fetchedAt DESC")
    Page<Article> findArticlesWithFilters(
            @Param("country") String country,
            @Param("language") String language, 
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.fetchedAt >= :startOfDay ORDER BY a.publishedAt DESC")
    List<Article> findTodaysArticles(@Param("startOfDay") LocalDateTime startOfDay);
    
    @Query("SELECT DISTINCT a.country FROM Article a WHERE a.country IS NOT NULL ORDER BY a.country")
    List<String> findDistinctCountries();
    
    @Query("SELECT DISTINCT a.language FROM Article a WHERE a.language IS NOT NULL ORDER BY a.language")
    List<String> findDistinctLanguages();
    
    @Query("SELECT DISTINCT a.category FROM Article a WHERE a.category IS NOT NULL ORDER BY a.category")
    List<String> findDistinctCategories();
    
    @Query("SELECT DISTINCT a.source FROM Article a ORDER BY a.source")
    List<String> findDistinctSources();
    
    @Modifying
    @Query("DELETE FROM Article a WHERE a.fetchedAt < :cutoffDate")
    int deleteByFetchedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}