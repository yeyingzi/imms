import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { MenuItem } from '@/types/module'
import request from '@/utils/request'

export const useMenuStore = defineStore('menu', () => {
  const dynamicMenus = ref<MenuItem[]>([])
  const loaded = ref(false)

  interface ModuleInfo {
    id: number
    moduleKey: string
    name: string
    status: number
  }

  const loadModuleMenus = async () => {
    if (loaded.value) return
    try {
      const modules = import.meta.glob('@modules/*/frontend/src/index.ts', {
        eager: true,
        import: 'default'
      })

      let enabledKeys: Set<string> | null = null
      try {
        const res = await request.get<any, { data: ModuleInfo[] }>('/v1/modules')
        if (res.data && Array.isArray(res.data)) {
          enabledKeys = new Set(
            res.data.filter((m: ModuleInfo) => m.status !== 0).map((m: ModuleInfo) => m.moduleKey)
          )
        }
      } catch {
      }

      Object.values(modules).forEach((moduleConfig: any) => {
        if (!moduleConfig || !moduleConfig.menus) return

        const key = moduleConfig.key || ''
        if (enabledKeys && key && !enabledKeys.has(key)) {
          return
        }

        moduleConfig.menus.forEach((menu: MenuItem) => {
          const exists = dynamicMenus.value.some(m => m.path === menu.path)
          if (!exists) {
            dynamicMenus.value.push(menu)
          }
        })
      })

      loaded.value = true
    } catch (error) {
      console.error('[Menu] 加载菜单失败:', error)
    }
  }

  const getFilteredMenus = (permissions: string[]) => {
    return dynamicMenus.value.filter(menu => {
      if (!menu.permission) return true
      return permissions.includes(menu.permission)
    })
  }

  const registerMenus = (menus: MenuItem[]) => {
    menus.forEach(menu => {
      const exists = dynamicMenus.value.some(m => m.path === menu.path)
      if (!exists) {
        dynamicMenus.value.push(menu)
      }
    })
  }

  const unregisterMenus = (moduleKey: string) => {
    dynamicMenus.value = dynamicMenus.value.filter(menu => {
      return !menu.path.startsWith(`/${moduleKey}`)
    })
  }

  const refreshMenus = () => {
    loaded.value = false
    dynamicMenus.value = []
    return loadModuleMenus()
  }

  const getAllMenus = () => {
    return dynamicMenus.value
  }

  return {
    dynamicMenus,
    loaded,
    loadModuleMenus,
    registerMenus,
    unregisterMenus,
    refreshMenus,
    getAllMenus,
    getFilteredMenus
  }
})
