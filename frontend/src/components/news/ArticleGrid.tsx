import { AlertCircle, Newspaper, RefreshCw } from 'lucide-react'
import type { Article } from '../../types'
import { ArticleCard } from './ArticleCard'
import { ArticleCardSkeleton } from '../ui/Skeleton'
import type { ViewMode } from '../../types'

interface ArticleGridProps {
  articles: Article[]
  isLoading: boolean
  error?: string | null
  viewMode: ViewMode
  isEmpty?: boolean  // true when no articles exist yet (startup)
  onRefresh?: () => void
}

export function ArticleGrid({ articles, isLoading, error, viewMode, isEmpty, onRefresh }: ArticleGridProps) {
  if (error) {
    return (
      <div className="flex flex-col items-center justify-center py-24 text-center gap-4">
        <div className="w-14 h-14 rounded-2xl bg-red-100 dark:bg-red-900/30 flex items-center justify-center">
          <AlertCircle className="w-7 h-7 text-red-500" />
        </div>
        <div>
          <p className="font-semibold text-slate-800 dark:text-slate-200">Failed to load news</p>
          <p className="text-sm text-slate-400 mt-1">{error}</p>
        </div>
      </div>
    )
  }

  if (isLoading) {
    return (
      <div
        className={
          viewMode === 'grid'
            ? 'grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-5'
            : 'flex flex-col gap-3'
        }
      >
        {Array.from({ length: 9 }).map((_, i) => (
          <ArticleCardSkeleton key={i} />
        ))}
      </div>
    )
  }

  if (!articles.length) {
    // Startup / empty DB state
    if (isEmpty) {
      return (
        <div className="flex flex-col items-center justify-center py-24 text-center gap-4">
          <div className="w-14 h-14 rounded-2xl bg-aurora-50 dark:bg-aurora-900/20 flex items-center justify-center">
            <RefreshCw className="w-7 h-7 text-aurora-400 animate-spin" style={{ animationDuration: '3s' }} />
          </div>
          <div>
            <p className="font-semibold text-slate-800 dark:text-slate-200">Fetching latest news…</p>
            <p className="text-sm text-slate-400 mt-1">
              Backend is loading articles from providers. This takes a few seconds.
            </p>
          </div>
          {onRefresh && (
            <button
              onClick={onRefresh}
              className="mt-1 px-4 py-2 text-sm font-medium rounded-xl bg-aurora-500 text-white hover:bg-aurora-600 transition flex items-center gap-2"
            >
              <RefreshCw className="w-4 h-4" />
              Refresh now
            </button>
          )}
        </div>
      )
    }

    return (
      <div className="flex flex-col items-center justify-center py-24 text-center gap-4">
        <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
          <Newspaper className="w-7 h-7 text-slate-400" />
        </div>
        <div>
          <p className="font-semibold text-slate-800 dark:text-slate-200">No articles found</p>
          <p className="text-sm text-slate-400 mt-1">
            Try adjusting your filters or refresh the feed
          </p>
        </div>
      </div>
    )
  }

  return (
    <div
      className={
        viewMode === 'grid'
          ? 'grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-5'
          : 'flex flex-col gap-3'
      }
    >
      {articles.map((article) => (
        <ArticleCard key={article.id} article={article} viewMode={viewMode} />
      ))}
    </div>
  )
}
