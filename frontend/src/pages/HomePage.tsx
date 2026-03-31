import { useState, useCallback, useRef } from 'react'
import { ChevronLeft, ChevronRight, TrendingUp } from 'lucide-react'
import { Layout } from '../components/layout/Layout'
import { FilterPanel } from '../components/news/FilterPanel'
import { ArticleGrid } from '../components/news/ArticleGrid'
import { ProviderStatusWidget } from '../components/providers/ProviderStatus'
import { Button } from '../components/ui/Button'
import { useNews, useRefreshNews } from '../hooks/useNews'
import { useUIStore } from '../store/uiStore'
import type { NewsFilters } from '../types'

const DEFAULT_FILTERS: NewsFilters = { page: 0, size: 18 }

const TRENDING_TAGS = ['AI', 'Technology', 'Climate', 'Economy', 'Health', 'Space', 'Finance', 'Politics']

export function HomePage() {
  const [filters, setFilters] = useState<NewsFilters>(DEFAULT_FILTERS)
  const { viewMode } = useUIStore()
  const topRef = useRef<HTMLDivElement>(null)

  const { data, isLoading, error, isFetching } = useNews(filters)
  const refresh = useRefreshNews()
  const isDbEmpty = !isLoading && (data?.totalElements ?? 0) === 0 && !filters.keyword && !filters.category && !filters.country && !filters.language

  const updateFilters = useCallback((partial: Partial<NewsFilters>) => {
    setFilters((prev) => ({ ...prev, ...partial }))
    topRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }, [])

  const resetFilters = useCallback(() => {
    setFilters(DEFAULT_FILTERS)
  }, [])

  const goToPage = (page: number) => {
    updateFilters({ page })
    topRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  const sidebar = (
    <div className="space-y-4">
      <FilterPanel
        filters={filters}
        onChange={updateFilters}
        onReset={resetFilters}
        totalResults={data ? Number(data.totalElements) : undefined}
      />
      <ProviderStatusWidget />

      {/* Trending tags */}
      <div className="rounded-2xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 p-4 space-y-3">
        <div className="flex items-center gap-2">
          <TrendingUp className="w-4 h-4 text-teal-500" />
          <h3 className="text-sm font-semibold text-slate-700 dark:text-slate-200">
            Trending
          </h3>
        </div>
        <div className="flex flex-wrap gap-2">
          {TRENDING_TAGS.map((tag) => (
            <button
              key={tag}
              onClick={() => updateFilters({ keyword: tag, page: 0 })}
              className={`px-2.5 py-1 text-xs rounded-lg border transition-all ${
                filters.keyword === tag
                  ? 'border-aurora-400 bg-aurora-50 dark:bg-aurora-900/20 text-aurora-600 dark:text-aurora-400'
                  : 'border-slate-200 dark:border-slate-600 text-slate-500 dark:text-slate-400 hover:border-aurora-300 hover:text-aurora-500'
              }`}
            >
              #{tag}
            </button>
          ))}
        </div>
      </div>
    </div>
  )

  return (
    <Layout sidebar={sidebar}>
      <div ref={topRef} className="space-y-5">
        {/* Page header */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 dark:text-white">
              {filters.category
                ? `${filters.category.charAt(0).toUpperCase() + filters.category.slice(1)} News`
                : filters.keyword
                  ? `"${filters.keyword}"`
                  : 'Latest News'}
            </h1>
            {data && !isLoading && (
              <p className="text-sm text-slate-400 mt-0.5">
                {data.totalElements.toLocaleString()} articles · Page {data.currentPage + 1} of{' '}
                {data.totalPages || 1}
              </p>
            )}
          </div>
          {isFetching && !isLoading && (
            <span className="text-xs text-slate-400 animate-pulse">Updating…</span>
          )}
        </div>

        {/* Article grid */}
        <ArticleGrid
          articles={data?.articles ?? []}
          isLoading={isLoading}
          error={error ? (error as Error).message : null}
          viewMode={viewMode}
          isEmpty={isDbEmpty}
          onRefresh={() => refresh.mutate(undefined)}
        />

        {/* Pagination */}
        {data && data.totalPages > 1 && (
          <div className="flex items-center justify-center gap-3 pt-4">
            <Button
              variant="secondary"
              size="sm"
              onClick={() => goToPage(data.currentPage - 1)}
              disabled={!data.hasPrevious}
              icon={<ChevronLeft className="w-4 h-4" />}
            >
              Prev
            </Button>

            <div className="flex items-center gap-1">
              {Array.from({ length: Math.min(5, data.totalPages) }, (_, i) => {
                const pageNum =
                  data.totalPages <= 5
                    ? i
                    : data.currentPage <= 2
                      ? i
                      : data.currentPage >= data.totalPages - 3
                        ? data.totalPages - 5 + i
                        : data.currentPage - 2 + i
                return (
                  <button
                    key={pageNum}
                    onClick={() => goToPage(pageNum)}
                    className={`w-8 h-8 text-sm rounded-lg transition-all ${
                      pageNum === data.currentPage
                        ? 'bg-aurora-500 text-white font-semibold'
                        : 'text-slate-500 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-700'
                    }`}
                  >
                    {pageNum + 1}
                  </button>
                )
              })}
            </div>

            <Button
              variant="secondary"
              size="sm"
              onClick={() => goToPage(data.currentPage + 1)}
              disabled={!data.hasNext}
              icon={<ChevronRight className="w-4 h-4" />}
            >
              Next
            </Button>
          </div>
        )}
      </div>
    </Layout>
  )
}
