import request from '@/utils/request'

export interface Role {
  id: number
  name: string
  code: string
  description?: string
  createdAt?: string
  updatedAt?: string
}

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

export const getRoleList = (params?: { pageNum?: number; pageSize?: number }) => {
  return request.get<{ list: Role[]; total: number; pageNum: number; pageSize: number }>('/v1/roles', { params })
}

export const getRole = (id: number) => {
  return request.get<Role>(`/v1/roles/${id}`)
}

export const createRole = (data: { name: string; code: string; description?: string }) => {
  return request.post('/v1/roles', data)
}

export const updateRole = (id: number, data: { name: string; code: string; description?: string }) => {
  return request.put(`/v1/roles/${id}`, data)
}

export const deleteRole = (id: number) => {
  return request.delete(`/v1/roles/${id}`)
}

export const getRolePermissions = (id: number) => {
  return request.get<number[]>(`/v1/roles/${id}/permissions`)
}

export const getPermissionList = () => {
  return request.get<Permission[]>('/v1/roles/permissions')
}

export const assignRolePermissions = (id: number, permissionIds: number[]) => {
  return request.put(`/v1/roles/${id}/permissions`, { permissionIds })
}
