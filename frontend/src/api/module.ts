import request from '@/utils/request'

export interface Module {
  id: number
  moduleKey: string
  name: string
  version: string
  description?: string
  author?: string
  icon?: string
  status: number
  createdAt?: string
  updatedAt?: string
}

export const getModuleList = () => {
  return request.get<Module[]>('/v1/modules')
}

export const getModule = (id: number) => {
  return request.get<Module>(`/v1/modules/${id}`)
}

export const toggleModule = (id: number) => {
  return request.put(`/v1/modules/${id}/toggle`)
}
