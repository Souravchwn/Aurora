import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { ViewMode } from '../types'

interface UIState {
  darkMode: boolean
  viewMode: ViewMode
  sidebarOpen: boolean
  toggleDarkMode: () => void
  setViewMode: (mode: ViewMode) => void
  toggleSidebar: () => void
  setSidebarOpen: (open: boolean) => void
}

export const useUIStore = create<UIState>()(
  persist(
    (set) => ({
      darkMode: false,
      viewMode: 'grid',
      sidebarOpen: true,
      toggleDarkMode: () =>
        set((s) => {
          const next = !s.darkMode
          document.documentElement.classList.toggle('dark', next)
          return { darkMode: next }
        }),
      setViewMode: (mode) => set({ viewMode: mode }),
      toggleSidebar: () => set((s) => ({ sidebarOpen: !s.sidebarOpen })),
      setSidebarOpen: (open) => set({ sidebarOpen: open }),
    }),
    {
      name: 'aurora-ui',
      onRehydrateStorage: () => (state) => {
        if (state?.darkMode) {
          document.documentElement.classList.add('dark')
        }
      },
    }
  )
)
