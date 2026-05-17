import request from '@/utils/request'

export interface OperationLog {
  id?: number
  userId?: number
  username?: string
  module?: string
  action?: string
  description?: string
  ipAddress?: string
  createdAt?: string
}

export interface LoginLog {
  id?: number
  userId?: number
  username?: string
  loginType?: number
  status?: number
  errorMsg?: string
  ipAddress?: string
  userAgent?: string
  createdAt?: string
}

export const getOperationLogList = (params?: { pageNum?: number; pageSize?: number; module?: string; username?: string; startDate?: string; endDate?: string }) => {
  return request.get<{ list: OperationLog[]; total: number; pageNum: number; pageSize: number }>('/v1/logs/operation', { params })
}

export const deleteOperationLog = (id: number) => {
  return request.delete(`/v1/logs/operation/${id}`)
}

export const getLoginLogList = (params?: { pageNum?: number; pageSize?: number; username?: string; status?: number; startDate?: string; endDate?: string }) => {
  return request.get<{ list: LoginLog[]; total: number; pageNum: number; pageSize: number }>('/v1/logs/login', { params })
}

export const deleteLoginLog = (id: number) => {
  return request.delete(`/v1/logs/login/${id}`)
}
