import request from '@/utils/request'

export interface User {
  id: number
  username: string
  realName?: string
  phone?: string
  email?: string
  avatar?: string
  status?: number
  createdAt?: string
  updatedAt?: string
}

export const getUserList = (params?: { pageNum?: number; pageSize?: number; keyword?: string }) => {
  return request.get<{ list: User[]; total: number; pageNum: number; pageSize: number }>('/v1/users', { params })
}

export const getUser = (id: number) => {
  return request.get<User>(`/v1/users/${id}`)
}

export const createUser = (data: Partial<User>) => {
  return request.post('/v1/users', data)
}

export const updateUser = (id: number, data: Partial<User>) => {
  return request.put(`/v1/users/${id}`, data)
}

export const deleteUser = (id: number) => {
  return request.delete(`/v1/users/${id}`)
}

export const updateUserStatus = (id: number, status: number) => {
  return request.put(`/v1/users/${id}/status`, { status })
}

export const assignRoles = (id: number, roleIds: number[]) => {
  return request.put(`/v1/users/${id}/roles`, { roleIds })
}
