import request from '@/utils/request'

export interface DashboardStats {
  userCount: number
  moduleCount: number
  operationLogCount: number
  loginLogCount: number
  onlineUserCount: number
}

export interface SystemInfo {
  platformName: string
  version: string
  env: string
  javaVersion: string
  osName: string
  uptime: string
}

export const getDashboardStats = () => {
  return request.get<DashboardStats>('/v1/dashboard/stats')
}

export const getSystemInfo = () => {
  return request.get<SystemInfo>('/v1/dashboard/system-info')
}
