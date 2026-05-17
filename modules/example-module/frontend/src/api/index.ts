import request from '@/utils/request'

export interface ExampleItem {
  id?: number
  name: string
  description?: string
  status?: number
  createdAt?: string
  updatedAt?: string
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export const exampleApi = {
  getList: (params?: { pageNum?: number; pageSize?: number; keyword?: string }) => {
    return request.get<PageResult<ExampleItem>>('/v1/example-module/list', { params })
  },

  getById: (id: number) => {
    return request.get<ExampleItem>(`/v1/example-module/${id}`)
  },

  create: (data: Omit<ExampleItem, 'id'>) => {
    return request.post('/v1/example-module', data)
  },

  update: (id: number, data: Partial<ExampleItem>) => {
    return request.put(`/v1/example-module/${id}`, data)
  },

  delete: (id: number) => {
    return request.delete(`/v1/example-module/${id}`)
  }
}