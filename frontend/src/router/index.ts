import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useMenuStore } from '@/stores/menu'

const moduleRoutes: RouteRecordRaw[] = []
const moduleMenus: any[] = []

const modules = import.meta.glob('@modules/*/frontend/src/index.ts', {
  eager: true,
  import: 'default'
})

Object.values(modules).forEach((mod: any) => {
  if (mod?.routes?.length) {
    mod.routes.forEach((route: RouteRecordRaw) => {
      moduleRoutes.push(route)
    })
  }
  if (mod?.menus?.length) {
    mod.menus.forEach((menu: any) => {
      moduleMenus.push(menu)
    })
  }
})

const userModuleChildren: RouteRecordRaw[] = moduleRoutes.map(route => ({
  ...route,
  path: route.path.startsWith('/') ? route.path.slice(1) : route.path
}))

const adminModuleChildren: RouteRecordRaw[] = moduleRoutes.map(route => ({
  ...route,
  name: route.name ? `Admin${String(route.name)}` : undefined,
  path: route.path.startsWith('/') ? route.path.slice(1) : route.path
}))

const baseRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/UserLayout.vue'),
    meta: { requiresAuth: true, isUser: true },
    children: [
      {
        path: '',
        redirect: '/home'
      },
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'User', requiresAuth: true }
      },
      ...userModuleChildren
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { requiresAuth: true, isAdmin: true, permission: 'admin-access' },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled', requiresAuth: true }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'User', requiresAuth: true, permission: 'user-menu' }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled', requiresAuth: true, permission: 'role-menu' }
      },
      {
        path: 'module',
        name: 'Module',
        component: () => import('@/views/module/index.vue'),
        meta: { title: '模块管理', icon: 'Grid', requiresAuth: true, permission: 'module-menu' }
      },
      {
        path: 'config',
        name: 'Config',
        component: () => import('@/views/config/index.vue'),
        meta: { title: '系统配置', icon: 'Setting', requiresAuth: true, permission: 'config-menu' }
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/log/index.vue'),
        meta: { title: '日志管理', icon: 'Document', requiresAuth: true, permission: 'log-menu' }
      },
      ...adminModuleChildren
    ]
  }
]

const routes: RouteRecordRaw[] = [...baseRoutes]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const defaultTitle = '家庭助手'

router.beforeEach(async (to, from, next) => {
  const menuStore = useMenuStore()
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth === false) {
    next()
  } else if (!token) {
    next('/login')
  } else {
    const modulePath = to.path.match(/^\/(admin\/)?([^/]+)/)?.[2] || ''
    const isModuleRoute = Object.keys(modules).some((key: string) =>
      key.includes(`/${modulePath}/`)
    )
    if (isModuleRoute && menuStore.loaded && !menuStore.dynamicMenus.some(m => to.path.includes(m.path))) {
      next(from.path || '/')
      return
    }
    next()
  }
})

router.afterEach((to) => {
  const title = to.meta?.title as string | undefined
  document.title = title ? `${title} - ${defaultTitle}` : defaultTitle
})

export { moduleRoutes, moduleMenus }
export default router
