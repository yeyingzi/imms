export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface LoginForm {
  username: string
  password: string
}

export interface UserInfo {
  id: number
  username: string
  realName: string
  roles: string[]
  permissions: string[]
  avatar?: string
  phone?: string
  email?: string
}

export interface LoginResponse {
  token: string
  refreshToken: string
  user: UserInfo
}

export interface PasswordChangeForm {
  oldPassword: string
  newPassword: string
}

export interface User {
  id: number
  username: string
  realName: string
  phone?: string
  email?: string
  avatar?: string
  status: number
  createdAt: string
  updatedAt?: string
}

export interface Role {
  id: number
  name: string
  code: string
  description?: string
  createdAt: string
  updatedAt?: string
}

export interface Permission {
  id: number
  name: string
  code: string
  type: number
  path?: string
  parentId: number
  sortOrder: number
  createdAt: string
}

export interface Module {
  id: number
  moduleKey: string
  name: string
  version: string
  description?: string
  author?: string
  icon?: string
  status: number
  createdAt: string
  updatedAt?: string
}

export interface LoginLog {
  id: number
  userId?: number
  username: string
  loginType: number
  status: number
  errorMsg?: string
  ipAddress?: string
  userAgent?: string
  createdAt: string
}

export interface OperationLog {
  id: number
  userId?: number
  username: string
  module: string
  action: string
  description?: string
  ipAddress?: string
  createdAt: string
}

export interface Config {
  id: number
  configKey: string
  configValue: string
  description?: string
  createdAt: string
  updatedAt?: string
}

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
