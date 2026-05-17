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

const routes: RouteRecordRaw[] = [
  {
    path: '/bookmark-module',
    name: 'BookmarkModule',
    component: () => import('./views/Index.vue'),
    meta: {
      title: '网址收藏合集',
      icon: 'Collection',
      permission: 'bookmark-module:view'
    }
  }
]

const menus = [
  {
    name: '网址收藏',
    icon: 'Collection',
    path: '/bookmark-module',
    permission: 'bookmark-module:view',
    description: '共享网址收藏管理，支持搜索和隐私保护'
  }
]

const permissions = [
  'bookmark-module:view',
  'bookmark-module:create',
  'bookmark-module:edit',
  'bookmark-module:delete'
]

const moduleConfig = {
  key: 'bookmark-module',
  name: '网址收藏合集',
  version: '1.0.0',
  routes,
  menus,
  permissions
}

export default moduleConfig
