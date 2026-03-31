package com.sourav.aurora.dto;

import com.sourav.aurora.model.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;
    private String title;
    private String description;
    private String summary;
    private String url;
    private String source;
    private String category;
    private String country;
    private String language;
    private LocalDateTime publishedAt;
    private LocalDateTime fetchedAt;
    private String imageUrl;
    
    public static ArticleDto fromEntity(Article article) {
        return ArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .description(article.getDescription())
                .summary(article.getSummary())
                .url(article.getUrl())
                .source(article.getSource())
                .category(article.getCategory())
                .country(article.getCountry())
                .language(article.getLanguage())
                .publishedAt(article.getPublishedAt())
                .fetchedAt(article.getFetchedAt())
                .imageUrl(article.getImageUrl())
                .build();
    }
}