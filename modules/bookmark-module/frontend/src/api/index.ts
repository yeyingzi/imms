import request from '@/utils/request'

export interface Bookmark {
  id?: number
  title: string
  url: string
  description?: string
  icon?: string
  createdBy?: string
  isPrivate?: number
  createdAt?: string
  updatedAt?: string
}

export interface BookmarkQuery {
  page?: number
  pageSize?: number
  keyword?: string
  currentUser?: string
  mineOnly?: boolean
}

export const bookmarkApi = {
  getBookmarkList(params: BookmarkQuery) {
    return request.get('/v1/bookmarks', { params })
  },

  createBookmark(data: Bookmark) {
    return request.post('/v1/bookmarks', data)
  },

  updateBookmark(id: number, data: Bookmark) {
    return request.put(`/v1/bookmarks/${id}`, data)
  },

  deleteBookmark(id: number) {
    return request.delete(`/v1/bookmarks/${id}`)
  },

  togglePrivacy(id: number) {
    return request.put(`/v1/bookmarks/${id}/privacy`)
  }
}
