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
    path: '/example-module',
    name: 'ExampleModule',
    component: () => import('./views/Index.vue'),
    meta: {
      title: '示例模块',
      icon: 'Box',
      permission: 'example-module:view'
    }
  }
]

const menus = [
  {
    name: '示例模块',
    icon: 'Box',
    path: '/example-module',
    permission: 'example-module:view',
    description: '展示模块开发规范和最佳实践'
  }
]

const permissions = [
  'example-module:view',
  'example-module:list',
  'example-module:create',
  'example-module:edit',
  'example-module:delete'
]

const moduleConfig = {
  key: 'example-module',
  name: '示例模块',
  version: '1.0.0',
  routes,
  menus,
  permissions
}

export default moduleConfig
