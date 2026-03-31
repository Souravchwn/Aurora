# Aurora — Intelligent News Aggregator

A full-stack news aggregation platform with a **React + Vite** frontend and **Spring Boot** backend, containerized with Docker Compose.

---

## Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 18, TypeScript, Vite 5, Tailwind CSS v3 |
| State | TanStack Query v5 (server), Zustand (client) |
| Backend | Java 21+, Spring Boot 3.5, Virtual Threads |
| Database | H2 in-memory (dev) |
| Packaging | Docker, nginx, multi-stage builds |

---

## Quick Start — Docker

```bash
# 1. Copy env file and fill in your API keys
cp .env.example .env

# 2. Start everything
docker compose up --build

# Frontend → http://localhost:3000
# Backend API → http://localhost:8080/api
```

---

## Local Development

### Backend

```bash
cd backend
./mvnw spring-boot:run
# or set env vars:
NEWSAPI_KEY=your_key GNEWS_KEY=your_key ./mvnw spring-boot:run
```

Backend runs at `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm run dev
# Dev server with proxy to localhost:8080
```

Frontend runs at `http://localhost:5173`

---

## API Keys

| Provider | Free Tier | Register |
|----------|-----------|---------|
| NewsAPI | 100 req/day | https://newsapi.org/register |
| GNews | 100 req/day | https://gnews.io/ |

---

## Project Structure

```
Aurora/
├── backend/                     # Spring Boot application
│   ├── src/
│   │   ├── main/java/com/sourav/aurora/
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── service/         # Business logic + scheduling
│   │   │   ├── provider/        # NewsAPI & GNews (Strategy pattern)
│   │   │   ├── model/           # JPA entities
│   │   │   ├── repository/      # Spring Data JPA
│   │   │   ├── dto/             # Request/response DTOs
│   │   │   ├── config/          # Virtual threads, cache, providers
│   │   │   ├── constants/       # API & app constants
│   │   │   └── exception/       # Global error handling
│   │   └── resources/
│   │       └── application.yaml
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                    # React + Vite application
│   ├── src/
│   │   ├── components/
│   │   │   ├── layout/          # Header, Layout
│   │   │   ├── news/            # ArticleCard, ArticleGrid, FilterPanel
│   │   │   ├── providers/       # ProviderStatus widget
│   │   │   └── ui/              # Button, Badge, Skeleton
│   │   ├── hooks/               # useNews, useProviders (React Query)
│   │   ├── pages/               # HomePage, BookmarksPage, ProvidersPage
│   │   ├── services/            # Axios API client
│   │   ├── store/               # Zustand stores (bookmarks, UI)
│   │   ├── types/               # TypeScript interfaces
│   │   └── utils/               # Formatting, category config
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## API Reference

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/news` | GET | Paginated news feed |
| `/api/news/search` | GET | Keyword search |
| `/api/news/today` | GET | Today's articles |
| `/api/news/refresh` | POST | Trigger provider refresh |
| `/api/news/cache/clear` | POST | Clear in-memory cache |
| `/api/providers` | GET | All providers + status |
| `/api/providers/status` | GET | Provider health detail |
| `/api/health` | GET | Service health check |
| `/api/metrics` | GET | Runtime metrics |

**Query parameters for `/api/news`**: `country`, `language`, `category`, `keyword`, `page`, `size`

```bash
# Examples
curl "http://localhost:8080/api/news?country=us&category=technology&page=0&size=20"
curl "http://localhost:8080/api/news/search?keyword=AI"
curl -X POST "http://localhost:8080/api/news/refresh"
```

---

## Features

**Frontend**
- Aurora-themed gradient UI with dark mode
- Grid / list view toggle
- Per-article bookmarks (persisted in localStorage)
- Article share (Web Share API with clipboard fallback)
- Skeleton loading states
- Trending topic quick-filters
- Provider health widget
- Reading time estimates
- Toast notifications

**Backend**
- Multi-provider architecture (Strategy + Template Method patterns)
- Java Virtual Threads for concurrent fetching
- Smart caching with TTL (Spring Cache)
- Auto-refresh scheduler (every 30 min)
- Nightly cleanup of old articles
- Paginated, filterable API
- Global exception handling

---

## Suggested Next Features

Here are features to show off more skills:

| Feature | Skills demonstrated |
|---------|-------------------|
| **OpenAI/Claude summarization** | LLM API integration, prompt engineering |
| **User auth (JWT)** | Spring Security, stateless auth, React auth context |
| **PostgreSQL persistence** | Production DB, Flyway migrations, connection pooling |
| **Email digests** | Spring Mail / SendGrid, scheduled jobs |
| **Sentiment analysis** | NLP, external API integration, badge UI |
| **Reading history** | IndexedDB, analytics dashboard |
| **Push notifications** | Service workers, Web Push API |
| **CI/CD pipeline** | GitHub Actions, Docker Hub, automated testing |
| **Redis cache** | Distributed caching, cache invalidation patterns |

---

## Development Notes

- The H2 console is available at `http://localhost:8080/h2-console` (JDBC: `jdbc:h2:mem:aurora`, user: `sa`)
- The Vite dev server proxies `/api/*` to `localhost:8080` — no CORS issues in dev
- In Docker, nginx proxies `/api/*` to the `backend` service — single-origin, no CORS needed

---

## License

MIT — see [LICENSE](LICENSE)
