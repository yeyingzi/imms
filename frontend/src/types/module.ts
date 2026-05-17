import type { RouteRecordRaw } from 'vue-router'

export interface MenuItem {
  name: string
  icon?: string
  path: string
  permission?: string
  description?: string
  children?: MenuItem[]
}

export interface Permission {
  code: string
  name: string
  type: 'menu' | 'button'
}

export interface ModuleConfig {
  key: string
  name: string
  version: string
  routes: RouteRecordRaw[]
  menus: MenuItem[]
  permissions: string[]
}

export interface ModuleInfo {
  moduleKey: string
  name: string
  version: string
  author: string
  description: string
  icon: string
  status: 'disabled' | 'enabled' | 'uninstalled'
  dependencies: string[]
  permissions: Permission[]
  menus: MenuItem[]
}
