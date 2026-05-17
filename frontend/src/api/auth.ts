import request from '@/utils/request'

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
  avatar?: string | null
  phone?: string
  email?: string
}

export interface LoginResponse {
  token: string
  user: UserInfo
}

export interface PasswordChangeForm {
  oldPassword: string
  newPassword: string
}

export const login = (data: LoginForm) => {
  return request.post<LoginResponse>('/v1/auth/login', data)
}

export const logout = () => {
  return request.post('/v1/auth/logout')
}

export const getUserInfo = () => {
  return request.get<UserInfo>('/v1/auth/userinfo')
}

export const changePassword = (data: PasswordChangeForm) => {
  return request.put('/v1/auth/password', data)
}
