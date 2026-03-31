import { Search, X, SlidersHorizontal } from 'lucide-react'
import type { NewsFilters } from '../../types'

const COUNTRIES = [
  { value: '', label: 'All Countries' },
  { value: 'us', label: 'United States' },
  { value: 'gb', label: 'United Kingdom' },
  { value: 'ca', label: 'Canada' },
  { value: 'au', label: 'Australia' },
  { value: 'de', label: 'Germany' },
  { value: 'fr', label: 'France' },
  { value: 'in', label: 'India' },
  { value: 'jp', label: 'Japan' },
  { value: 'br', label: 'Brazil' },
  { value: 'it', label: 'Italy' },
]

const LANGUAGES = [
  { value: '', label: 'All Languages' },
  { value: 'en', label: 'English' },
  { value: 'de', label: 'Deutsch' },
  { value: 'fr', label: 'Français' },
  { value: 'it', label: 'Italiano' },
  { value: 'es', label: 'Español' },
  { value: 'pt', label: 'Português' },
  { value: 'ja', label: 'Japanese' },
  { value: 'ar', label: 'Arabic' },
  { value: 'hi', label: 'Hindi' },
]

const CATEGORIES = [
  { value: '', label: 'All Categories' },
  { value: 'general', label: '🌐 General' },
  { value: 'technology', label: '💻 Technology' },
  { value: 'business', label: '💼 Business' },
  { value: 'health', label: '🏥 Health' },
  { value: 'science', label: '🔬 Science' },
  { value: 'sports', label: '⚽ Sports' },
  { value: 'entertainment', label: '🎬 Entertainment' },
  { value: 'world', label: '🌍 World' },
  { value: 'nation', label: '🏛️ Nation' },
]

interface FilterPanelProps {
  filters: NewsFilters
  onChange: (filters: Partial<NewsFilters>) => void
  onReset: () => void
  totalResults?: number
}

export function FilterPanel({ filters, onChange, onReset, totalResults }: FilterPanelProps) {
  const hasActiveFilters =
    filters.country || filters.language || filters.category || filters.keyword

  return (
    <aside className="w-full space-y-5">
      <div className="flex items-center gap-2 pb-1">
        <SlidersHorizontal className="w-4 h-4 text-aurora-500" />
        <h2 className="font-semibold text-slate-700 dark:text-slate-200 text-sm">
          Filters
        </h2>
        {hasActiveFilters && (
          <button
            onClick={onReset}
            className="ml-auto text-xs text-slate-400 hover:text-red-400 transition-colors flex items-center gap-1"
          >
            <X className="w-3 h-3" />
            Reset
          </button>
        )}
      </div>

      {/* Keyword search */}
      <div>
        <label className="block text-xs font-medium text-slate-500 dark:text-slate-400 mb-1.5">
          Search
        </label>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
          <input
            type="text"
            value={filters.keyword ?? ''}
            onChange={(e) => onChange({ keyword: e.target.value || undefined, page: 0 })}
            placeholder="Keywords…"
            className="w-full pl-9 pr-3 py-2.5 text-sm rounded-xl border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-800 text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-aurora-400 focus:border-transparent transition"
          />
          {filters.keyword && (
            <button
              onClick={() => onChange({ keyword: undefined, page: 0 })}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
            >
              <X className="w-3.5 h-3.5" />
            </button>
          )}
        </div>
      </div>

      {/* Category */}
      <div>
        <label className="block text-xs font-medium text-slate-500 dark:text-slate-400 mb-1.5">
          Category
        </label>
        <select
          value={filters.category ?? ''}
          onChange={(e) => onChange({ category: e.target.value || undefined, page: 0 })}
          className="w-full px-3 py-2.5 text-sm rounded-xl border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-aurora-400 focus:border-transparent transition"
        >
          {CATEGORIES.map((c) => (
            <option key={c.value} value={c.value}>
              {c.label}
            </option>
          ))}
        </select>
      </div>

      {/* Country */}
      <div>
        <label className="block text-xs font-medium text-slate-500 dark:text-slate-400 mb-1.5">
          Country
        </label>
        <select
          value={filters.country ?? ''}
          onChange={(e) => onChange({ country: e.target.value || undefined, page: 0 })}
          className="w-full px-3 py-2.5 text-sm rounded-xl border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-aurora-400 focus:border-transparent transition"
        >
          {COUNTRIES.map((c) => (
            <option key={c.value} value={c.value}>
              {c.label}
            </option>
          ))}
        </select>
      </div>

      {/* Language */}
      <div>
        <label className="block text-xs font-medium text-slate-500 dark:text-slate-400 mb-1.5">
          Language
        </label>
        <select
          value={filters.language ?? ''}
          onChange={(e) => onChange({ language: e.target.value || undefined, page: 0 })}
          className="w-full px-3 py-2.5 text-sm rounded-xl border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-aurora-400 focus:border-transparent transition"
        >
          {LANGUAGES.map((l) => (
            <option key={l.value} value={l.value}>
              {l.label}
            </option>
          ))}
        </select>
      </div>

      {totalResults !== undefined && (
        <p className="text-xs text-slate-400 text-center">
          {totalResults.toLocaleString()} article{totalResults !== 1 ? 's' : ''} found
        </p>
      )}
    </aside>
  )
}
