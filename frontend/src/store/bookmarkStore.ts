import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { Article } from '../types'

interface BookmarkState {
  bookmarks: Article[]
  isBookmarked: (id: number) => boolean
  addBookmark: (article: Article) => void
  removeBookmark: (id: number) => void
  toggleBookmark: (article: Article) => void
  clearBookmarks: () => void
}

export const useBookmarkStore = create<BookmarkState>()(
  persist(
    (set, get) => ({
      bookmarks: [],
      isBookmarked: (id) => get().bookmarks.some((b) => b.id === id),
      addBookmark: (article) =>
        set((s) => ({ bookmarks: [article, ...s.bookmarks] })),
      removeBookmark: (id) =>
        set((s) => ({ bookmarks: s.bookmarks.filter((b) => b.id !== id) })),
      toggleBookmark: (article) => {
        if (get().isBookmarked(article.id)) {
          get().removeBookmark(article.id)
        } else {
          get().addBookmark(article)
        }
      },
      clearBookmarks: () => set({ bookmarks: [] }),
    }),
    { name: 'aurora-bookmarks' }
  )
)
