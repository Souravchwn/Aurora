package com.sourav.aurora.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "news_articles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(nullable = false, unique = true, length = 1000)
    private String url;
    
    @Column(nullable = false)
    private String source;
    
    private String category;
    
    @Column(length = 10)
    private String country;
    
    @Column(length = 10)
    private String language;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;
    
    @Column(name = "image_url", length = 1000)
    private String imageUrl;
    
    @PrePersist
    protected void onCreate() {
        if (fetchedAt == null) {
            fetchedAt = LocalDateTime.now();
        }
    }
}