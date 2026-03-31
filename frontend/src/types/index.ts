export interface Article {
  id: number
  title: string
  description: string | null
  summary: string | null
  url: string
  source: string
  category: string | null
  country: string | null
  language: string | null
  publishedAt: string | null
  fetchedAt: string
  imageUrl: string | null
}

export interface NewsResponse {
  articles: Article[]
  totalPages: number
  totalElements: number
  currentPage: number
  pageSize: number
  hasNext: boolean
  hasPrevious: boolean
  availableCountries: string[]
  availableLanguages: string[]
  availableCategories: string[]
  availableSources: string[]
}

export interface NewsFilters {
  country?: string
  language?: string
  category?: string
  keyword?: string
  page: number
  size: number
}

export interface ProviderStatus {
  providers: string[]
  activeCount: number
  totalCount: number
  timestamp: string
}

export interface HealthStatus {
  status: string
  service: string
  version: string
  timestamp: string
  providers: number
}

export interface MetricsData {
  activeProviders: number
  totalProviders: number
  timestamp: string
  uptime: number
}

export type Category =
  | 'business'
  | 'entertainment'
  | 'general'
  | 'health'
  | 'science'
  | 'sports'
  | 'technology'
  | 'world'
  | 'nation'

export type ViewMode = 'grid' | 'list'
