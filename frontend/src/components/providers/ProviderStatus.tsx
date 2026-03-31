import { Activity, CheckCircle2, XCircle, Zap } from 'lucide-react'
import { useProvidersStatus, useHealth } from '../../hooks/useProviders'
import { Skeleton } from '../ui/Skeleton'

export function ProviderStatusWidget() {
  const { data: status, isLoading: statusLoading } = useProvidersStatus()
  const { data: health, isLoading: healthLoading } = useHealth()

  const isUp = health?.status === 'UP'

  return (
    <div className="rounded-2xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 p-4 space-y-3">
      <div className="flex items-center gap-2">
        <Activity className="w-4 h-4 text-aurora-500" />
        <h3 className="text-sm font-semibold text-slate-700 dark:text-slate-200">
          System Status
        </h3>
      </div>

      {/* Service health */}
      <div className="flex items-center justify-between">
        <span className="text-xs text-slate-500 dark:text-slate-400">Service</span>
        {healthLoading ? (
          <Skeleton className="h-4 w-12" />
        ) : (
          <span
            className={`inline-flex items-center gap-1 text-xs font-medium ${
              isUp ? 'text-green-500' : 'text-red-500'
            }`}
          >
            {isUp ? <CheckCircle2 className="w-3.5 h-3.5" /> : <XCircle className="w-3.5 h-3.5" />}
            {isUp ? 'Healthy' : 'Down'}
          </span>
        )}
      </div>

      {/* Providers */}
      <div className="flex items-center justify-between">
        <span className="text-xs text-slate-500 dark:text-slate-400">Providers</span>
        {statusLoading ? (
          <Skeleton className="h-4 w-10" />
        ) : (
          <span className="inline-flex items-center gap-1 text-xs font-medium text-aurora-600 dark:text-aurora-400">
            <Zap className="w-3.5 h-3.5" />
            {status?.activeCount ?? 0} / {status?.totalCount ?? 0} active
          </span>
        )}
      </div>

      {/* Provider list */}
      {!statusLoading && status?.providers && (
        <div className="space-y-1 pt-1 border-t border-slate-100 dark:border-slate-700">
          {status.providers.map((p) => {
            const healthy = !p.toLowerCase().includes('unhealthy') && !p.toLowerCase().includes('disabled')
            return (
              <div key={p} className="flex items-center gap-2">
                <span
                  className={`w-1.5 h-1.5 rounded-full flex-shrink-0 ${
                    healthy ? 'bg-green-400' : 'bg-slate-300 dark:bg-slate-600'
                  }`}
                />
                <span className="text-xs text-slate-500 dark:text-slate-400 truncate">{p}</span>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
