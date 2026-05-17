<template>
  <div class="admin-layout">
    <aside class="sidebar" :class="{ 'is-collapsed': isCollapse }">
      <div class="logo">
        <h1 v-if="!isCollapse">管理后台</h1>
        <el-icon v-else :size="24" color="white"><HomeFilled /></el-icon>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        class="sidebar-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-menu-item index="/admin/user">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/role">
          <el-icon><UserFilled /></el-icon>
          <template #title>角色管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/module">
          <el-icon><Grid /></el-icon>
          <template #title>模块管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/config">
          <el-icon><Setting /></el-icon>
          <template #title>系统配置</template>
        </el-menu-item>

        <el-menu-item index="/admin/log">
          <el-icon><Document /></el-icon>
          <template #title>日志管理</template>
        </el-menu-item>
      </el-menu>
    </aside>

    <div class="main-wrapper">
      <header class="header">
        <div class="header-left">
          <el-button text @click="toggleCollapse">
            <el-icon v-if="isCollapse"><Expand /></el-icon>
            <el-icon v-else><Fold /></el-icon>
          </el-button>
        </div>

        <div class="header-right">
          <el-button text @click="goToUserHome">
            <el-icon><HomeFilled /></el-icon>
            用户界面
          </el-button>

          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" class="user-avatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="main-content">
        <router-view v-slot="{ Component }">
          <component :is="Component" />
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const menuStore = useMenuStore()

onMounted(() => {
  menuStore.loadModuleMenus()
})

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleMenuSelect = (index: string) => {
  router.push(index)
}

const goToUserHome = () => {
  router.push('/home')
}

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    await userStore.userLogout()
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 220px;
  background: #1d1d2e;
  transition: width 0.28s ease;
  flex-shrink: 0;
  overflow: hidden;
}

.sidebar.is-collapsed {
  width: 64px;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #141422;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;
}

.logo h1 {
  color: #e8eaed;
  font-size: 16px;
  margin: 0;
  font-weight: 600;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
  background: transparent !important;
}

.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.65) !important;
  height: 48px;
  line-height: 48px;
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.08) !important;
  color: #ffffff !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #409eff !important;
  color: #ffffff !important;
}

.sidebar-menu :deep(.el-menu-item .el-icon),
.sidebar-menu :deep(.el-sub-menu__title .el-icon) {
  color: inherit !important;
  font-size: 18px;
}

.sidebar-menu :deep(.el-sub-menu .el-menu) {
  background: #1a1a2e !important;
}

.sidebar-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 52px !important;
  min-width: auto;
}

.sidebar-menu.el-menu--collapse {
  width: 64px;
}

.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

.header {
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  flex-shrink: 0;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f5f5f5;
}

.user-avatar {
  background: #409eff;
}

.username {
  font-size: 14px;
  color: #333;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  background: #f5f6fa;
  padding: 20px;
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1001;
    width: 220px !important;
  }

  .sidebar.is-collapsed {
    transform: translateX(-100%);
    width: 0 !important;
  }

  .main-wrapper {
    margin-left: 0;
  }

  .header {
    padding: 0 12px;
  }

  .username {
    display: none;
  }

  .main-content {
    padding: 12px;
  }
}
</style>
