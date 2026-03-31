import { Activity, CheckCircle2, XCircle, Clock, Server, Zap, RefreshCw } from 'lucide-react'
import { Layout } from '../components/layout/Layout'
import { Button } from '../components/ui/Button'
import { Skeleton } from '../components/ui/Skeleton'
import { useProvidersStatus, useHealth, useMetrics } from '../hooks/useProviders'
import { useRefreshNews, useClearCache } from '../hooks/useNews'
import { formatPublishedAt } from '../utils/format'

function formatUptime(ms: number): string {
  const s = Math.floor(ms / 1000)
  const h = Math.floor(s / 3600)
  const m = Math.floor((s % 3600) / 60)
  if (h > 0) return `${h}h ${m}m`
  return `${m}m ${s % 60}s`
}

export function ProvidersPage() {
  const { data: status, isLoading: statusLoading, refetch: refetchStatus } = useProvidersStatus()
  const { data: health, isLoading: healthLoading } = useHealth()
  const { data: metrics, isLoading: _metricsLoading } = useMetrics()
  const refresh = useRefreshNews()
  const clearCache = useClearCache()

  const isHealthy = health?.status === 'UP'

  return (
    <Layout>
      <div className="space-y-6 max-w-3xl">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white flex items-center gap-2">
            <Server className="w-6 h-6 text-aurora-500" />
            System & Providers
          </h1>
          <Button
            variant="secondary"
            size="sm"
            onClick={() => refetchStatus()}
            icon={<RefreshCw className="w-4 h-4" />}
          >
            Refresh
          </Button>
        </div>

        {/* Stats row */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          {[
            {
              label: 'Service',
              value: healthLoading ? null : isHealthy ? 'Healthy' : 'Down',
              icon: <Activity className="w-5 h-5" />,
              color: isHealthy ? 'text-green-500' : 'text-red-500',
              bg: isHealthy ? 'bg-green-50 dark:bg-green-900/20' : 'bg-red-50 dark:bg-red-900/20',
            },
            {
              label: 'Version',
              value: health?.version ?? null,
              icon: <Zap className="w-5 h-5" />,
              color: 'text-aurora-500',
              bg: 'bg-aurora-50 dark:bg-aurora-900/20',
            },
            {
              label: 'Active Providers',
              value: metrics ? `${metrics.activeProviders} / ${metrics.totalProviders}` : null,
              icon: <CheckCircle2 className="w-5 h-5" />,
              color: 'text-teal-500',
              bg: 'bg-teal-50 dark:bg-teal-900/20',
            },
            {
              label: 'Uptime',
              value: metrics ? formatUptime(metrics.uptime) : null,
              icon: <Clock className="w-5 h-5" />,
              color: 'text-slate-500',
              bg: 'bg-slate-50 dark:bg-slate-800',
            },
          ].map((stat) => (
            <div
              key={stat.label}
              className="rounded-2xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 p-4 space-y-2"
            >
              <div className={`w-8 h-8 rounded-xl flex items-center justify-center ${stat.bg} ${stat.color}`}>
                {stat.icon}
              </div>
              <p className="text-xs text-slate-500 dark:text-slate-400">{stat.label}</p>
              {stat.value === null ? (
                <Skeleton className="h-5 w-16" />
              ) : (
                <p className={`font-semibold ${stat.color}`}>{stat.value}</p>
              )}
            </div>
          ))}
        </div>

        {/* Provider list */}
        <div className="rounded-2xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 overflow-hidden">
          <div className="px-5 py-4 border-b border-slate-100 dark:border-slate-700">
            <h2 className="font-semibold text-slate-800 dark:text-slate-200">News Providers</h2>
            {status && (
              <p className="text-xs text-slate-400 mt-0.5">
                Last checked {formatPublishedAt(status.timestamp)}
              </p>
            )}
          </div>
          <div className="divide-y divide-slate-100 dark:divide-slate-700">
            {statusLoading
              ? Array.from({ length: 2 }).map((_, i) => (
                  <div key={i} className="flex items-center gap-4 px-5 py-4">
                    <Skeleton className="w-8 h-8 rounded-xl" />
                    <div className="space-y-1.5">
                      <Skeleton className="h-4 w-32" />
                      <Skeleton className="h-3 w-20" />
                    </div>
                  </div>
                ))
              : status?.providers.map((provider) => {
                  const isActive = !provider.toLowerCase().includes('disabled')
                  const isUnhealthy = provider.toLowerCase().includes('unhealthy')
                  const name = provider.split(' (')[0]
                  const statusLabel = provider.match(/\(([^)]+)\)/)?.[1] ?? 'active'

                  return (
                    <div key={provider} className="flex items-center gap-4 px-5 py-4">
                      <div
                        className={`w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0 ${
                          isActive && !isUnhealthy
                            ? 'bg-green-50 dark:bg-green-900/20'
                            : 'bg-slate-100 dark:bg-slate-700'
                        }`}
                      >
                        {isActive && !isUnhealthy ? (
                          <CheckCircle2 className="w-5 h-5 text-green-500" />
                        ) : (
                          <XCircle className="w-5 h-5 text-slate-400" />
                        )}
                      </div>
                      <div>
                        <p className="font-medium text-slate-800 dark:text-slate-200 text-sm">
                          {name}
                        </p>
                        <p
                          className={`text-xs ${
                            isActive && !isUnhealthy
                              ? 'text-green-500'
                              : isUnhealthy
                                ? 'text-amber-500'
                                : 'text-slate-400'
                          }`}
                        >
                          {statusLabel}
                        </p>
                      </div>
                    </div>
                  )
                })}
          </div>
        </div>

        {/* Actions */}
        <div className="rounded-2xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 p-5 space-y-4">
          <h2 className="font-semibold text-slate-800 dark:text-slate-200">Cache & Data</h2>
          <div className="flex flex-wrap gap-3">
            <Button
              variant="primary"
              size="md"
              onClick={() => refresh.mutate(undefined)}
              loading={refresh.isPending}
              icon={<RefreshCw className="w-4 h-4" />}
            >
              Refresh All News
            </Button>
            <Button
              variant="secondary"
              size="md"
              onClick={() => clearCache.mutate()}
              loading={clearCache.isPending}
              icon={<XCircle className="w-4 h-4" />}
            >
              Clear Cache
            </Button>
          </div>
          <p className="text-xs text-slate-400">
            Refresh triggers a background fetch from all active providers. Cache clears the in-memory
            news cache and forces fresh queries.
          </p>
        </div>
      </div>
    </Layout>
  )
}
