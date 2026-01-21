# **Aurora – Smart News Digest Reader 2.0 (SRS)**

## 1. Introduction

### 1.1 Purpose

Aurora is an intelligent web-based news aggregator that fetches, summarizes, and delivers news from multiple providers, allowing users to filter by language, country, category, or keyword. It leverages **Java 25 virtual threads**, **plugin-based provider architecture**, **AI summarization**, and a **static Tailwind CSS UI** for performance and usability.

### 1.2 Scope

- Aggregate news from multiple APIs (NewsAPI, GNews, etc.)
- Filter news by **country**, **language**, **category**, and **keyword**
- AI or heuristic-based summarization of article descriptions
- Serve responsive static HTML + Tailwind frontend
- In-memory caching using H2 database
- Plugin-based provider system for easy extensibility

### 1.3 Definitions

- **Virtual Thread** – Lightweight thread in Java 25
- **Provider Plugin** – Modular code that fetches from a specific news API
- **Summarizer** – Module that produces a concise summary of an article
- **Cache TTL** – Time-to-live for cached news data

---

## 2. Overall Description

### 2.1 Product Perspective

Aurora is a standalone backend system with a lightweight static frontend. It can be integrated into other platforms via REST endpoints.

### 2.2 Product Features

- Multi-provider news aggregation
- Filtering by country, language, category, and keyword
- AI-based summarization
- In-memory caching for fast responses
- Plugin architecture for provider extensions
- Responsive Tailwind CSS UI

### 2.3 User Classes

- **General User** – Reads news and applies filters
- **Developer/Integrator** – Adds new providers, modifies summarizer
- **Analyst** – Reviews logs, metrics, and article performance

### 2.4 Operating Environment

- Java 25 LTS runtime
- Spring Boot 3.4+
- Web browser for frontend
- H2 in-memory database

### 2.5 Design & Implementation Constraints

- API keys configurable in `application.yml` or environment variables
- Summarizer fallback for API limits
- UI must be lightweight (<300ms load)
- JVM must support virtual threads

---

## 3. System Features

### 3.1 Multi-Provider Aggregation

- Fetch news concurrently from all registered providers
- Deduplicate articles by URL or `(title + source)`
- Persist articles with `fetchedAt` timestamp

### 3.2 Filtering & Search

- Filter by country, language, category, keyword
- Frontend reflects filters dynamically

### 3.3 Smart Summarization

- AI-based summarizer produces short summaries (~200-300 characters)
- Fallback: first 230 characters of description

### 3.4 Caching & Persistence

- Cache articles by `country::language::category::keyword`
- TTL configurable (default 60 min)
- Fallback to cached articles if API fails

### 3.5 Plugin Architecture

- `NewsProvider` interface for modular integration
- Providers auto-discovered as Spring components
- New providers can be added without modifying core logic

### 3.6 Responsive UI

- Tailwind CSS-based
- Filter panel, news cards
- Dark mode toggle

---

## 4. External Interface Requirements

### 4.1 User Interface

- Single-page `index.html` with filters and news cards
- Vanilla JS + Fetch API for backend calls
- Responsive on desktop & mobile

### 4.2 Software Interfaces

- News APIs (NewsAPI.org, GNews.io)
- Summarizer API (optional)

### 4.3 Communications Interfaces

- REST JSON responses
- Query parameters for filters
- CORS enabled

---

## 5. Data Model

### `news_articles`

| Field       | Type      | Description              |
| ----------- | --------- | ------------------------ |
| id          | BIGINT    | Primary key              |
| title       | VARCHAR   | Article title            |
| description | TEXT      | Full article description |
| url         | VARCHAR   | Original article link    |
| source      | VARCHAR   | Provider name            |
| category    | VARCHAR   | News category (optional) |
| country     | VARCHAR   | Country code filter      |
| language    | VARCHAR   | Language code            |
| publishedAt | TIMESTAMP | Original publish time    |
| fetchedAt   | TIMESTAMP | Time fetched by Aurora   |

### `user_preferences` (optional)

| Field              | Type      | Description              |
| ------------------ | --------- | ------------------------ |
| user_id            | BIGINT    | FK to user table         |
| preferred_lang     | VARCHAR   | Preferred language       |
| preferred_country  | VARCHAR   | Preferred country        |
| preferred_category | VARCHAR   | Preferred category       |
| last_accessed      | TIMESTAMP | Last updated preferences |

---

## 6. API Endpoints

| Endpoint            | Method | Description                   |
| ------------------- | ------ | ----------------------------- |
| `/api/news`         | GET    | Fetch news with filters       |
| `/api/news/today`   | GET    | Cached articles fetched today |
| `/api/news/refresh` | POST   | Clear cache / force refresh   |
| `/api/providers`    | GET    | List active news providers    |

---

## 7. Non-Functional Requirements

- Performance: Cached responses <150ms; first-time fetch <2s per provider
- Scalability: 1000+ concurrent requests with virtual threads
- Reliability: Fallback to cached content if APIs fail
- Security: API keys secured, endpoints optionally authenticated
- Maintainability: Modular provider system
- Extensibility: Easy to add new providers
- Usability: Responsive UI, clear filters

---

## 8. System Architecture

```
Frontend (Tailwind + HTML)
        │
Controller (Spring Boot REST)
        │
Service Layer (Aggregator + Summarizer)
        │
Provider Plugins (NewsAPI, GNews, etc.)
        │
In-Memory Database (H2)
```

- Virtual threads handle **API fetches concurrently**
- Preloading and caching improve response times

---

## 9. Future Enhancements

- AI mood detection for articles
- Daily email digest with top stories
- Voice summaries (TTS)
- Export PDF summaries
- Plugin-based analytics and trend visualization

---

## 10. Glossary

- **Digest:** Collection of summarized articles
- **TTL:** Time-to-Live for cached data
- **Virtual Thread:** Lightweight Java thread
- **Provider Plugin:** Module fetching news from API

---
