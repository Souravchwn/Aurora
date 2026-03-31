import { formatDistanceToNow, format, isToday, isYesterday } from 'date-fns'

export function formatPublishedAt(dateStr: string | null): string {
  if (!dateStr) return 'Unknown date'
  try {
    const date = new Date(dateStr)
    if (isToday(date)) return formatDistanceToNow(date, { addSuffix: true })
    if (isYesterday(date)) return 'Yesterday'
    return format(date, 'MMM d, yyyy')
  } catch {
    return dateStr
  }
}

export function estimateReadTime(text: string | null): string {
  if (!text) return '1 min read'
  const words = text.trim().split(/\s+/).length
  const minutes = Math.max(1, Math.ceil(words / 200))
  return `${minutes} min read`
}

export const CATEGORY_CONFIG: Record<
  string,
  { label: string; color: string; bg: string; darkBg: string }
> = {
  technology: {
    label: 'Tech',
    color: 'text-blue-600',
    bg: 'bg-blue-100',
    darkBg: 'dark:bg-blue-900/30 dark:text-blue-300',
  },
  business: {
    label: 'Business',
    color: 'text-amber-600',
    bg: 'bg-amber-100',
    darkBg: 'dark:bg-amber-900/30 dark:text-amber-300',
  },
  health: {
    label: 'Health',
    color: 'text-green-600',
    bg: 'bg-green-100',
    darkBg: 'dark:bg-green-900/30 dark:text-green-300',
  },
  science: {
    label: 'Science',
    color: 'text-purple-600',
    bg: 'bg-purple-100',
    darkBg: 'dark:bg-purple-900/30 dark:text-purple-300',
  },
  sports: {
    label: 'Sports',
    color: 'text-orange-600',
    bg: 'bg-orange-100',
    darkBg: 'dark:bg-orange-900/30 dark:text-orange-300',
  },
  entertainment: {
    label: 'Entertainment',
    color: 'text-pink-600',
    bg: 'bg-pink-100',
    darkBg: 'dark:bg-pink-900/30 dark:text-pink-300',
  },
  general: {
    label: 'General',
    color: 'text-slate-600',
    bg: 'bg-slate-100',
    darkBg: 'dark:bg-slate-700/40 dark:text-slate-300',
  },
  world: {
    label: 'World',
    color: 'text-indigo-600',
    bg: 'bg-indigo-100',
    darkBg: 'dark:bg-indigo-900/30 dark:text-indigo-300',
  },
  nation: {
    label: 'Nation',
    color: 'text-teal-600',
    bg: 'bg-teal-100',
    darkBg: 'dark:bg-teal-900/30 dark:text-teal-300',
  },
}

export function getCategoryConfig(category: string | null) {
  if (!category) return CATEGORY_CONFIG.general
  return CATEGORY_CONFIG[category.toLowerCase()] ?? CATEGORY_CONFIG.general
}
