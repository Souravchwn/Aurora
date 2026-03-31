import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { newsApi } from '../services/api'
import type { NewsFilters } from '../types'
import toast from 'react-hot-toast'

export function useNews(filters: NewsFilters) {
  return useQuery({
    queryKey: ['news', filters],
    queryFn: () => newsApi.getNews(filters),
    staleTime: 5 * 60 * 1000,
    placeholderData: (prev) => prev,
    // Poll every 5s while DB is empty (backend startup fetch in progress), stop once we have data
    refetchInterval: (query) =>
      (query.state.data?.totalElements ?? 0) === 0 ? 5000 : false,
  })
}

export function useSearchNews(keyword: string, page = 0, size = 20) {
  return useQuery({
    queryKey: ['news', 'search', keyword, page, size],
    queryFn: () => newsApi.searchNews(keyword, page, size),
    enabled: keyword.trim().length >= 2,
    staleTime: 2 * 60 * 1000,
  })
}

export function useTodaysNews() {
  return useQuery({
    queryKey: ['news', 'today'],
    queryFn: newsApi.getTodaysNews,
    staleTime: 10 * 60 * 1000,
  })
}

export function useRefreshNews() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: newsApi.refreshNews,
    onMutate: () => toast.loading('Refreshing news…', { id: 'refresh' }),
    onSuccess: () => {
      toast.success('News refreshed!', { id: 'refresh' })
      qc.invalidateQueries({ queryKey: ['news'] })
    },
    onError: (err: Error) => {
      toast.error(err.message, { id: 'refresh' })
    },
  })
}

export function useClearCache() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: newsApi.clearCache,
    onSuccess: () => {
      toast.success('Cache cleared')
      qc.invalidateQueries({ queryKey: ['news'] })
    },
    onError: (err: Error) => toast.error(err.message),
  })
}
