import React from 'react'
import { Header } from './Header'
import { useUIStore } from '../../store/uiStore'

interface LayoutProps {
  children: React.ReactNode
  sidebar?: React.ReactNode
}

export function Layout({ children, sidebar }: LayoutProps) {
  const { sidebarOpen } = useUIStore()

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900 transition-colors duration-200">
      <Header />
      <div className="max-w-screen-2xl mx-auto px-4 sm:px-6 py-6">
        <div className={`flex gap-6 ${sidebar ? '' : ''}`}>
          {/* Sidebar */}
          {sidebar && (
            <aside
              className={`flex-shrink-0 transition-all duration-300 ${
                sidebarOpen ? 'w-64 opacity-100' : 'w-0 opacity-0 overflow-hidden'
              }`}
            >
              <div className="sticky top-24 space-y-4">{sidebar}</div>
            </aside>
          )}

          {/* Main content */}
          <main className="flex-1 min-w-0">{children}</main>
        </div>
      </div>
    </div>
  )
}
