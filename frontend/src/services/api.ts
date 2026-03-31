import axios from 'axios'
import type { NewsResponse, NewsFilters, ProviderStatus, HealthStatus, MetricsData, Article } from '../types'

const BASE = '/api'

const http = axios.create({
  baseURL: BASE,
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.response.use(
  (res) => res,
  (err) => {
    const message = err.response?.data?.message ?? err.message ?? 'Unknown error'
    return Promise.reject(new Error(message))
  }
)

export const newsApi = {
  getNews: (filters: NewsFilters) =>
    http
      .get<NewsResponse>('/news', { params: filters })
      .then((r) => r.data),

  searchNews: (keyword: string, page = 0, size = 20) =>
    http
      .get<NewsResponse>('/news/search', { params: { keyword, page, size } })
      .then((r) => r.data),

  getTodaysNews: () =>
    http.get<Article[]>('/news/today').then((r) => r.data),

  refreshNews: (filters?: Partial<Pick<NewsFilters, 'country' | 'language' | 'category' | 'keyword'>>) =>
    http.post('/news/refresh', null, { params: filters }).then((r) => r.data),

  clearCache: () =>
    http.post('/news/cache/clear').then((r) => r.data),

  getProviders: () =>
    http.get('/providers').then((r) => r.data),

  getProvidersStatus: () =>
    http.get<ProviderStatus>('/providers/status').then((r) => r.data),

  getHealth: () =>
    http.get<HealthStatus>('/health').then((r) => r.data),

  getMetrics: () =>
    http.get<MetricsData>('/metrics').then((r) => r.data),
}
