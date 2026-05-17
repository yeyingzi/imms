import request from '@/utils/request'

export interface Config {
  id?: number
  configKey: string
  configValue: string
  description?: string
  createdAt?: string
  updatedAt?: string
}

export interface ConfigResponse {
  platformName: string
  logo: string
  themeColor: string
  loginTimeout: number
  passwordMinLength: number
  maxLoginFailures: number
  lockoutDuration: number
  logRetentionDays: number
  _descriptions?: Record<string, string>
}

export const getConfigList = () => {
  return request.get<ConfigResponse>('/v1/configs')
}

export const getConfig = (key: string) => {
  return request.get<string>(`/v1/configs/${key}`)
}

export const updateConfig = (configs: Partial<ConfigResponse>) => {
  return request.put('/v1/configs', configs)
}
