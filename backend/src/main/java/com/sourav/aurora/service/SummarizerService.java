package com.sourav.aurora.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SummarizerService {
    
    @Value("${summary.ai.enabled:false}")
    private boolean aiEnabled;
    
    @Value("${summary.ai.key:}")
    private String aiApiKey;
    
    /**
     * Generate a summary for the given article description
     * Falls back to simple truncation if AI is not available
     */
    public String generateSummary(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "";
        }
        
        // For now, use simple fallback summarization
        // In the future, this can be enhanced with AI integration
        return generateFallbackSummary(description);
    }
    
    /**
     * Generate a simple fallback summary by taking the first 230 characters
     */
    private String generateFallbackSummary(String description) {
        if (description.length() <= 230) {
            return description;
        }
        
        String truncated = description.substring(0, 230);
        
        // Try to end at a word boundary
        int lastSpace = truncated.lastIndexOf(' ');
        if (lastSpace > 200) {
            truncated = truncated.substring(0, lastSpace);
        }
        
        return truncated + "...";
    }
    
    /**
     * Future method for AI-based summarization
     * This would integrate with OpenAI or other AI services
     */
    private String generateAiSummary(String description) {
        // TODO: Implement AI-based summarization
        // This would make API calls to OpenAI, Claude, or other AI services
        log.info("AI summarization not yet implemented, using fallback");
        return generateFallbackSummary(description);
    }
}