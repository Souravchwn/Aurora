# 🌌 Aurora – Smart News Digest Reader 2.0

Aurora is an intelligent news aggregation and summarization system built with Java 25 and Spring Boot. Filter news by **country**, **language**, **category**, and **keyword**, and view AI-generated summaries.

---

## 🚀 Features

- **Real API Integration**: Live data from NewsAPI and GNews with proper error handling
- **Java 25 Virtual Threads**: High-performance concurrent processing for API calls
- **Automatic Scheduling**: Auto-refresh news every 30 minutes, cleanup old articles daily
- **Advanced Architecture**: MVC pattern, Strategy pattern, Template method pattern
- **Constants Management**: Centralized API endpoints and application messages
- **Comprehensive Error Handling**: Global exception handling with proper HTTP status codes
- **Provider Health Monitoring**: Real-time provider status and health checks
- **Smart Caching**: In-memory caching with TTL and cache warming
- **Responsive UI**: Tailwind CSS frontend with dark mode and real-time search
- **Production Ready**: Proper logging, metrics, validation, and configuration

---

## 🧩 Tech Stack

- **Backend**: Java 25 LTS, Spring Boot 3.5+, Spring Data JPA
- **Database**: H2 in-memory database
- **Frontend**: Tailwind CSS, Vanilla JavaScript
- **Concurrency**: Virtual Threads (Project Loom)
- **Build**: Maven
- **Libraries**: Lombok, Jackson, Spring Cache

---

## 🧭 Setup Instructions

### 1. Prerequisites

- Java 25 LTS installed
- Maven 3.6+ installed
- API keys from news providers (optional for testing)

### 2. Clone and Build

```bash
git clone https://github.com/<your-username>/aurora.git
cd aurora
mvn clean compile
```

### 3. Configure API Keys (Optional)

Edit `src/main/resources/application.yaml`:

```yaml
news:
  apikeys:
    newsapi: "YOUR_NEWSAPI_KEY" # Get from https://newsapi.org
    gnews: "YOUR_GNEWS_KEY" # Get from https://gnews.io
```

### 4. Run Application

```bash
mvn spring-boot:run
```

### 5. Access the Application

- **Main UI**: [http://localhost:8080](http://localhost:8080)
- **H2 Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:aurora`
  - Username: `sa`
  - Password: (empty)

---

## 📦 Project Structure

```
aurora/
├─ src/main/java/com/sourav/aurora/
│  ├─ controller/
│  │  └─ NewsController.java           # REST API endpoints with constants
│  ├─ service/
│  │  ├─ NewsService.java              # Main business logic
│  │  ├─ NewsProviderService.java      # Provider management service
│  │  ├─ NewsSchedulerService.java     # Automatic scheduling service
│  │  └─ SummarizerService.java        # Article summarization
│  ├─ provider/
│  │  ├─ NewsProvider.java             # Provider interface (Strategy pattern)
│  │  ├─ AbstractNewsProvider.java     # Template method pattern base
│  │  ├─ NewsApiProvider.java          # NewsAPI real implementation
│  │  └─ GNewsProvider.java            # GNews real implementation
│  ├─ model/
│  │  └─ Article.java                  # JPA entity with Lombok
│  ├─ repository/
│  │  └─ ArticleRepository.java        # JPA repository with custom queries
│  ├─ dto/
│  │  ├─ ArticleDto.java               # Data transfer objects
│  │  └─ NewsResponse.java
│  ├─ config/
│  │  ├─ VirtualThreadConfig.java      # Java 25 virtual threads + WebClient
│  │  ├─ CacheConfig.java              # Caching configuration
│  │  └─ NewsProviderConfig.java       # Provider configuration properties
│  ├─ constants/
│  │  ├─ ApiConstants.java             # API endpoint constants
│  │  └─ ApplicationConstants.java     # Application messages & constants
│  ├─ exception/
│  │  ├─ NewsProviderException.java    # Custom provider exceptions
│  │  └─ GlobalExceptionHandler.java   # Global error handling
│  └─ AuroraApplication.java           # Main application class
├─ src/main/resources/
│  ├─ static/
│  │  └─ index.html                    # Responsive frontend UI
│  └─ application.yaml                 # Comprehensive configuration
├─ src/test/
│  └─ java/com/sourav/aurora/
│     └─ AuroraApplicationTests.java   # Comprehensive tests
└─ pom.xml                             # Maven dependencies with WebFlux
```

---

## 🔗 API Endpoints

| Endpoint                | Method | Description                           | Parameters                                                   |
| ----------------------- | ------ | ------------------------------------- | ------------------------------------------------------------ |
| `/api/news`             | GET    | Fetch filtered news with pagination   | `country`, `language`, `category`, `keyword`, `page`, `size` |
| `/api/news/today`       | GET    | Get today's cached articles           | None                                                         |
| `/api/news/search`      | GET    | Search news by keyword                | `keyword` (required), `page`, `size`                         |
| `/api/news/refresh`     | POST   | Refresh news from providers (async)   | `country`, `language`, `category`, `keyword`                 |
| `/api/news/cache/clear` | POST   | Clear news cache                      | None                                                         |
| `/api/providers`        | GET    | List active/all providers with status | None                                                         |
| `/api/providers/active` | GET    | List only active providers            | None                                                         |
| `/api/providers/status` | GET    | Detailed provider health status       | None                                                         |
| `/api/health`           | GET    | Health check endpoint                 | None                                                         |
| `/api/metrics`          | GET    | Basic application metrics             | None                                                         |

### Example API Calls

```bash
# Get all news (first page)
curl "http://localhost:8080/api/news"

# Get US business news in English
curl "http://localhost:8080/api/news?country=us&category=business&language=en"

# Search for technology news
curl "http://localhost:8080/api/news?keyword=technology"

# Refresh news cache
curl -X POST "http://localhost:8080/api/news/refresh"
```

---

## 🎯 Usage Guide

### 1. First Run

- Start the application
- Click "Refresh News" to fetch initial articles
- Use filters to narrow down results

### 2. Filtering News

- **Country**: Select specific countries (US, GB, CA, etc.)
- **Language**: Filter by language (EN, DE, FR, etc.)
- **Category**: Choose categories (business, technology, sports, etc.)
- **Keyword**: Search within article titles and descriptions

### 3. Features

- **Dark Mode**: Toggle with the moon/sun icon
- **Pagination**: Navigate through multiple pages of results
- **Real-time Search**: Keyword search with 500ms debounce
- **Responsive Design**: Works on desktop and mobile

---

## 🔧 Configuration Options

### Application Properties

```yaml
# Database Configuration
spring:
  datasource:
    url: jdbc:h2:mem:aurora
  jpa:
    hibernate:
      ddl-auto: create-drop

# News Provider API Keys
news:
  apikeys:
    newsapi: "${NEWSAPI_KEY:your_newsapi_key_here}"
    gnews: "${GNEWS_KEY:your_gnews_key_here}"
  cache:
    ttl: 3600 # Cache TTL in seconds

# AI Summarization (Future feature)
summary:
  ai:
    enabled: false
    provider: "openai"
    key: "${OPENAI_KEY:your_openai_key_here}"
```

### Environment Variables

```bash
export NEWSAPI_KEY="your_actual_newsapi_key"
export GNEWS_KEY="your_actual_gnews_key"
export OPENAI_KEY="your_openai_key"  # Optional
```

---

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### Manual Testing

1. Start the application
2. Open [http://localhost:8080](http://localhost:8080)
3. Try different filter combinations
4. Test the refresh functionality
5. Check the H2 console for data persistence

---

## 🚀 Deployment

### Production Build

```bash
mvn clean package
java -jar target/aurora-0.0.1-SNAPSHOT.jar
```

### Docker (Optional)

```dockerfile
FROM openjdk:25-jdk-slim
COPY target/aurora-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

---

## 🔮 Future Enhancements

- [ ] AI-powered article summarization with OpenAI integration
- [ ] User preferences and personalization
- [ ] Email digest subscriptions
- [ ] Advanced analytics and trending topics
- [ ] Social media integration
- [ ] Mobile app development
- [ ] Multi-language UI support
- [ ] Article bookmarking and favorites

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- [NewsAPI](https://newsapi.org) for news data
- [GNews](https://gnews.io) for additional news sources
- [Tailwind CSS](https://tailwindcss.com) for styling
- [Spring Boot](https://spring.io/projects/spring-boot) for the framework
- Java Virtual Threads for high-performance concurrency
