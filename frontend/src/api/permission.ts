import request from '@/utils/request'

export interface Permission {
  id?: number
  name: string
  code: string
  type?: number
  path?: string
  parentId?: number
  sortOrder?: number
  createdAt?: string
}

export const getPermissionList = () => {
  return request.get<Permission[]>('/v1/permissions')
}

export const getPermission = (id: number) => {
  return request.get<Permission>(`/v1/permissions/${id}`)
}

export const createPermission = (data: Permission) => {
  return request.post('/v1/permissions', data)
}

export const updatePermission = (id: number, data: Permission) => {
  return request.put(`/v1/permissions/${id}`, data)
}

export const deletePermission = (id: number) => {
  return request.delete(`/v1/permissions/${id}`)
}
