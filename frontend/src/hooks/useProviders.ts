import { useQuery } from '@tanstack/react-query'
import { newsApi } from '../services/api'

export function useProvidersStatus() {
  return useQuery({
    queryKey: ['providers', 'status'],
    queryFn: newsApi.getProvidersStatus,
    refetchInterval: 60 * 1000, // refresh every minute
    staleTime: 30 * 1000,
  })
}

export function useHealth() {
  return useQuery({
    queryKey: ['health'],
    queryFn: newsApi.getHealth,
    refetchInterval: 30 * 1000,
    staleTime: 20 * 1000,
  })
}

export function useMetrics() {
  return useQuery({
    queryKey: ['metrics'],
    queryFn: newsApi.getMetrics,
    refetchInterval: 60 * 1000,
    staleTime: 30 * 1000,
  })
}
