import { Bookmark, Trash2 } from 'lucide-react'
import { Layout } from '../components/layout/Layout'
import { ArticleCard } from '../components/news/ArticleCard'
import { Button } from '../components/ui/Button'
import { useBookmarkStore } from '../store/bookmarkStore'
import { useUIStore } from '../store/uiStore'

export function BookmarksPage() {
  const { bookmarks, clearBookmarks } = useBookmarkStore()
  const { viewMode } = useUIStore()

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 dark:text-white flex items-center gap-2">
              <Bookmark className="w-6 h-6 text-aurora-500" />
              Bookmarks
            </h1>
            <p className="text-sm text-slate-400 mt-0.5">
              {bookmarks.length} saved article{bookmarks.length !== 1 ? 's' : ''}
            </p>
          </div>
          {bookmarks.length > 0 && (
            <Button
              variant="danger"
              size="sm"
              onClick={clearBookmarks}
              icon={<Trash2 className="w-4 h-4" />}
            >
              Clear All
            </Button>
          )}
        </div>

        {bookmarks.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-32 text-center gap-4">
            <div className="w-16 h-16 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
              <Bookmark className="w-8 h-8 text-slate-300 dark:text-slate-600" />
            </div>
            <div>
              <p className="font-semibold text-slate-700 dark:text-slate-200">No bookmarks yet</p>
              <p className="text-sm text-slate-400 mt-1">
                Hover over any article and click the bookmark icon to save it here.
              </p>
            </div>
          </div>
        ) : (
          <div
            className={
              viewMode === 'grid'
                ? 'grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-5'
                : 'flex flex-col gap-3'
            }
          >
            {bookmarks.map((article) => (
              <ArticleCard key={article.id} article={article} viewMode={viewMode} />
            ))}
          </div>
        )}
      </div>
    </Layout>
  )
}
