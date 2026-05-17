import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getUserInfo, logout } from '@/api/auth'
import type { LoginForm, UserInfo, LoginResponse } from '@/api/auth'
import router from '@/router'

const TOKEN_KEY = 'token'
const USER_KEY = 'user_cache'

function loadCachedUser(): UserInfo | null {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

function saveCachedUser(user: UserInfo | null) {
  if (user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  } else {
    localStorage.removeItem(USER_KEY)
  }
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const userInfo = ref<UserInfo | null>(loadCachedUser())
  const permissions = ref<string[]>(userInfo.value?.permissions || [])

  const userLogin = async (loginForm: LoginForm) => {
    const res = await login(loginForm) as LoginResponse
    token.value = res.token
    userInfo.value = res.user
    permissions.value = res.user.permissions || []
    localStorage.setItem(TOKEN_KEY, res.token)
    saveCachedUser(res.user)
    return res
  }

  const fetchUserInfo = async () => {
    const res = await getUserInfo() as UserInfo
    userInfo.value = res
    permissions.value = res.permissions || []
    saveCachedUser(res)
    return res
  }

  const userLogout = async () => {
    try {
      await logout()
      token.value = ''
      userInfo.value = null
      permissions.value = []
      localStorage.removeItem(TOKEN_KEY)
      saveCachedUser(null)
      router.push('/login')
    } catch (error) {
      console.error('Logout failed:', error)
    }
  }

  const hasPermission = (permission: string): boolean => {
    return permissions.value.includes(permission)
  }

  return {
    token,
    userInfo,
    permissions,
    userLogin,
    fetchUserInfo,
    userLogout,
    hasPermission
  }
})
