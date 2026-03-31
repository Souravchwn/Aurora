import { Link, useLocation } from 'react-router-dom'
import {
  Moon,
  Sun,
  Bookmark,
  Zap,
  RefreshCw,
  LayoutGrid,
  LayoutList,
  Menu,
} from 'lucide-react'
import { useUIStore } from '../../store/uiStore'
import { useBookmarkStore } from '../../store/bookmarkStore'
import { useRefreshNews } from '../../hooks/useNews'
import { Button } from '../ui/Button'

export function Header() {
  const { darkMode, toggleDarkMode, viewMode, setViewMode, toggleSidebar } = useUIStore()
  const { bookmarks } = useBookmarkStore()
  const location = useLocation()
  const refresh = useRefreshNews()

  return (
    <header className="sticky top-0 z-40 w-full">
      {/* Aurora gradient background */}
      <div className="bg-aurora-gradient dark:bg-aurora-dark">
        <div className="max-w-screen-2xl mx-auto px-4 sm:px-6 h-16 flex items-center gap-3">
          {/* Sidebar toggle */}
          <button
            onClick={toggleSidebar}
            className="p-2 rounded-xl text-white/80 hover:text-white hover:bg-white/10 transition"
            aria-label="Toggle sidebar"
          >
            <Menu className="w-5 h-5" />
          </button>

          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 flex-shrink-0">
            <div className="w-8 h-8 rounded-xl bg-white/20 backdrop-blur flex items-center justify-center">
              <Zap className="w-4.5 h-4.5 text-white" fill="white" />
            </div>
            <span className="text-xl font-bold text-white tracking-tight">Aurora</span>
          </Link>

          {/* Nav */}
          <nav className="hidden sm:flex items-center gap-1 ml-4">
            {[
              { to: '/', label: 'Feed' },
              { to: '/bookmarks', label: 'Bookmarks' },
              { to: '/providers', label: 'Providers' },
            ].map(({ to, label }) => (
              <Link
                key={to}
                to={to}
                className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-all ${
                  location.pathname === to
                    ? 'bg-white/25 text-white'
                    : 'text-white/70 hover:text-white hover:bg-white/10'
                }`}
              >
                {label}
                {label === 'Bookmarks' && bookmarks.length > 0 && (
                  <span className="ml-1.5 inline-flex items-center justify-center w-4 h-4 text-xs rounded-full bg-white/30 text-white">
                    {bookmarks.length > 99 ? '99+' : bookmarks.length}
                  </span>
                )}
              </Link>
            ))}
          </nav>

          <div className="ml-auto flex items-center gap-2">
            {/* View mode toggle — only on home */}
            {location.pathname === '/' && (
              <div className="hidden sm:flex items-center rounded-xl bg-white/10 p-0.5 gap-0.5">
                <button
                  onClick={() => setViewMode('grid')}
                  className={`p-1.5 rounded-lg transition ${
                    viewMode === 'grid'
                      ? 'bg-white/20 text-white'
                      : 'text-white/60 hover:text-white'
                  }`}
                  aria-label="Grid view"
                >
                  <LayoutGrid className="w-4 h-4" />
                </button>
                <button
                  onClick={() => setViewMode('list')}
                  className={`p-1.5 rounded-lg transition ${
                    viewMode === 'list'
                      ? 'bg-white/20 text-white'
                      : 'text-white/60 hover:text-white'
                  }`}
                  aria-label="List view"
                >
                  <LayoutList className="w-4 h-4" />
                </button>
              </div>
            )}

            {/* Refresh */}
            {location.pathname === '/' && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => refresh.mutate(undefined)}
                loading={refresh.isPending}
                className="text-white/80 hover:text-white hover:bg-white/10"
                icon={<RefreshCw className="w-4 h-4" />}
              >
                <span className="hidden sm:inline">Refresh</span>
              </Button>
            )}

            {/* Bookmarks — mobile link */}
            <Link
              to="/bookmarks"
              className="sm:hidden relative p-2 rounded-xl text-white/80 hover:text-white hover:bg-white/10 transition"
            >
              <Bookmark className="w-5 h-5" />
              {bookmarks.length > 0 && (
                <span className="absolute top-1 right-1 w-2 h-2 bg-teal-400 rounded-full" />
              )}
            </Link>

            {/* Dark mode */}
            <button
              onClick={toggleDarkMode}
              className="p-2 rounded-xl text-white/80 hover:text-white hover:bg-white/10 transition"
              aria-label="Toggle dark mode"
            >
              {darkMode ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
            </button>
          </div>
        </div>
      </div>
    </header>
  )
}
