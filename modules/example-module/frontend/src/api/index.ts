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

export const exampleModuleApi = {
  getList: (params?: any) => {
    return request.get<any, { data: PageResult<ExampleItem> }>('/v1/example-module/list', { params })
  },

  getById: (id: number) => {
    return request.get<any, { data: ExampleItem }>(`/v1/example-module/${id}`)
  },

  create: (data: ExampleItem) => {
    return request.post<any, { data: any }>('/v1/example-module', data)
  },

  update: (id: number, data: ExampleItem) => {
    return request.put<any, { data: any }>(`/v1/example-module/${id}`, data)
  },

  delete: (id: number) => {
    return request.delete<any, { data: any }>(`/v1/example-module/${id}`)
  }
}
