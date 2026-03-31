package com.sourav.aurora;

import com.sourav.aurora.constants.ApplicationConstants;
import com.sourav.aurora.repository.ArticleRepository;
import com.sourav.aurora.service.NewsProviderService;
import com.sourav.aurora.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AuroraApplicationTests {

	@Autowired
	private NewsService newsService;
	
	@Autowired
	private NewsProviderService providerService;
	
	@Autowired
	private ArticleRepository articleRepository;

	@Test
	void contextLoads() {
		assertThat(newsService).isNotNull();
		assertThat(providerService).isNotNull();
		assertThat(articleRepository).isNotNull();
	}
	
	@Test
	void testProviders() {
		// Test that providers are loaded
		assertThat(newsService.getAllProviders()).isNotEmpty();
		assertThat(providerService.getAllProviders()).isNotEmpty();
	}
	
	@Test
	void testConstants() {
		// Test that constants are properly defined
		assertThat(ApplicationConstants.PROVIDER_NEWSAPI).isEqualTo("NewsAPI");
		assertThat(ApplicationConstants.PROVIDER_GNEWS).isEqualTo("GNews");
		assertThat(ApplicationConstants.DEFAULT_COUNTRY).isEqualTo("us");
		assertThat(ApplicationConstants.DEFAULT_LANGUAGE).isEqualTo("en");
	}
	
	@Test
	void testNewsServiceBasicFunctionality() {
		// Test basic news service functionality
		var response = newsService.getNews(null, null, null, null, 0, 10);
		assertThat(response).isNotNull();
		assertThat(response.getArticles()).isNotNull();
	}

}