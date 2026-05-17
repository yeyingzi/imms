import type { RouteRecordRaw } from 'vue-router'

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
  'example-module:create',
  'example-module:edit',
  'example-module:delete'
]

export default {
  key: 'example-module',
  name: '示例模块',
  version: '2.0.0',
  routes,
  menus,
  permissions
}