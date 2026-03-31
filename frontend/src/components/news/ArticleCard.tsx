import React, { useState } from 'react'
import { Bookmark, BookmarkCheck, Share2, ExternalLink, Clock, Globe } from 'lucide-react'
import type { Article } from '../../types'
import { formatPublishedAt, estimateReadTime, getCategoryConfig } from '../../utils/format'
import { useBookmarkStore } from '../../store/bookmarkStore'
import toast from 'react-hot-toast'

interface ArticleCardProps {
  article: Article
  viewMode?: 'grid' | 'list'
}

export function ArticleCard({ article, viewMode = 'grid' }: ArticleCardProps) {
  const [imgError, setImgError] = useState(false)
  const { isBookmarked, toggleBookmark } = useBookmarkStore()
  const bookmarked = isBookmarked(article.id)
  const catConfig = getCategoryConfig(article.category)

  function handleBookmark(e: React.MouseEvent) {
    e.preventDefault()
    toggleBookmark(article)
    toast.success(bookmarked ? 'Removed from bookmarks' : 'Bookmarked!', {
      duration: 1500,
      icon: bookmarked ? '🗑️' : '🔖',
    })
  }

  async function handleShare(e: React.MouseEvent) {
    e.preventDefault()
    try {
      if (navigator.share) {
        await navigator.share({ title: article.title, url: article.url })
      } else {
        await navigator.clipboard.writeText(article.url)
        toast.success('Link copied to clipboard!')
      }
    } catch {
      // user cancelled share
    }
  }

  const displayText = article.summary || article.description

  if (viewMode === 'list') {
    return (
      <a
        href={article.url}
        target="_blank"
        rel="noopener noreferrer"
        className="group flex gap-4 p-4 rounded-2xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 hover:border-aurora-400 dark:hover:border-aurora-500 hover:shadow-lg hover:shadow-aurora-500/10 transition-all duration-200 animate-slide-up"
      >
        {article.imageUrl && !imgError && (
          <img
            src={article.imageUrl}
            alt=""
            onError={() => setImgError(true)}
            className="w-28 h-20 object-cover rounded-xl flex-shrink-0"
          />
        )}
        <div className="flex-1 min-w-0">
          <div className="flex flex-wrap gap-1.5 mb-1.5">
            <span
              className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${catConfig.bg} ${catConfig.color} ${catConfig.darkBg}`}
            >
              {catConfig.label}
            </span>
            {article.source && (
              <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs bg-slate-100 dark:bg-slate-700 text-slate-500 dark:text-slate-400">
                {article.source}
              </span>
            )}
          </div>
          <h3 className="font-semibold text-slate-900 dark:text-white line-clamp-2 group-hover:text-aurora-600 dark:group-hover:text-aurora-400 transition-colors">
            {article.title}
          </h3>
          <div className="flex items-center gap-3 mt-2 text-xs text-slate-400">
            <span className="flex items-center gap-1">
              <Clock className="w-3 h-3" />
              {estimateReadTime(displayText)}
            </span>
            <span>{formatPublishedAt(article.publishedAt)}</span>
          </div>
        </div>
        <div className="flex flex-col gap-1 flex-shrink-0">
          <button
            onClick={handleBookmark}
            className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
          >
            {bookmarked ? (
              <BookmarkCheck className="w-4 h-4 text-aurora-500" />
            ) : (
              <Bookmark className="w-4 h-4 text-slate-400" />
            )}
          </button>
          <button
            onClick={handleShare}
            className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
          >
            <Share2 className="w-4 h-4 text-slate-400" />
          </button>
        </div>
      </a>
    )
  }

  return (
    <a
      href={article.url}
      target="_blank"
      rel="noopener noreferrer"
      className="group flex flex-col rounded-2xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 hover:border-aurora-400 dark:hover:border-aurora-500 hover:shadow-xl hover:shadow-aurora-500/10 hover:-translate-y-1 transition-all duration-200 overflow-hidden animate-slide-up"
    >
      {/* Image */}
      <div className="relative h-48 bg-gradient-to-br from-aurora-100 to-teal-100 dark:from-aurora-950 dark:to-slate-800 overflow-hidden flex-shrink-0">
        {article.imageUrl && !imgError ? (
          <img
            src={article.imageUrl}
            alt={article.title}
            onError={() => setImgError(true)}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center opacity-30">
            <Globe className="w-16 h-16 text-aurora-400" />
          </div>
        )}
        {/* Overlay actions */}
        <div className="absolute top-2 right-2 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
          <button
            onClick={handleBookmark}
            className="p-1.5 rounded-lg bg-white/90 dark:bg-slate-800/90 backdrop-blur-sm shadow hover:scale-110 transition-transform"
            title={bookmarked ? 'Remove bookmark' : 'Bookmark'}
          >
            {bookmarked ? (
              <BookmarkCheck className="w-4 h-4 text-aurora-500" />
            ) : (
              <Bookmark className="w-4 h-4 text-slate-600" />
            )}
          </button>
          <button
            onClick={handleShare}
            className="p-1.5 rounded-lg bg-white/90 dark:bg-slate-800/90 backdrop-blur-sm shadow hover:scale-110 transition-transform"
            title="Share"
          >
            <Share2 className="w-4 h-4 text-slate-600" />
          </button>
        </div>
      </div>

      {/* Content */}
      <div className="flex flex-col flex-1 p-4 gap-2">
        {/* Category + Source badges */}
        <div className="flex flex-wrap gap-1.5">
          <span
            className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${catConfig.bg} ${catConfig.color} ${catConfig.darkBg}`}
          >
            {catConfig.label}
          </span>
          {article.source && (
            <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs bg-slate-100 dark:bg-slate-700 text-slate-500 dark:text-slate-400">
              {article.source}
            </span>
          )}
        </div>

        {/* Title */}
        <h3 className="font-semibold text-slate-900 dark:text-white line-clamp-2 leading-snug group-hover:text-aurora-600 dark:group-hover:text-aurora-400 transition-colors">
          {article.title}
        </h3>

        {/* Summary / Description */}
        {displayText && (
          <p className="text-sm text-slate-500 dark:text-slate-400 line-clamp-3 leading-relaxed flex-1">
            {displayText}
          </p>
        )}

        {/* Footer */}
        <div className="flex items-center justify-between pt-2 mt-auto border-t border-slate-100 dark:border-slate-700/60 text-xs text-slate-400">
          <div className="flex items-center gap-2">
            <Clock className="w-3 h-3" />
            <span>{estimateReadTime(displayText)}</span>
          </div>
          <div className="flex items-center gap-2">
            <span>{formatPublishedAt(article.publishedAt)}</span>
            <ExternalLink className="w-3 h-3" />
          </div>
        </div>
      </div>
    </a>
  )
}
