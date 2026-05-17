import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useLoadingStore } from '@/stores/loading'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

let loadingStore: ReturnType<typeof useLoadingStore> | null = null

const getLoadingStore = () => {
  if (!loadingStore) {
    try {
      loadingStore = useLoadingStore()
    } catch (e) {
      return null
    }
  }
  return loadingStore
}

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`
    }

    const loading = config.headers?.['X-Show-Loading'] !== false
    if (loading) {
      const store = getLoadingStore()
      if (store) store.startLoading()
    }

    return config
  },
  (error) => {
    const store = getLoadingStore()
    if (store) store.stopLoading()
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    const store = getLoadingStore()
    if (store) store.stopLoading()
    return response.data
  },
  (error) => {
    const store = getLoadingStore()
    if (store) store.stopLoading()
    console.error('Response error:', error)

    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('user_cache')
          router.push('/login')
          break
        case 403:
          ElMessage.error('无权限访问')
          break
        case 404:
          ElMessage.error('请求资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(error.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }

    return Promise.reject(error)
  }
)

export default request
