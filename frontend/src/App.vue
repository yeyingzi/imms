<template>
  <router-view v-slot="{ Component }">
    <transition :name="transitionName" :mode="transitionName ? 'out-in' : undefined" @before-enter="onBeforeEnter" @after-enter="onAfterEnter">
      <div class="layout-wrapper">
        <component :is="Component" />
      </div>
    </transition>
  </router-view>
  <GlobalLoading />
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElNotification } from 'element-plus'
import GlobalLoading from '@/components/GlobalLoading.vue'

const router = useRouter()
const userStore = useUserStore()
const transitionName = ref('layout-crossfade')

let sessionTimer: ReturnType<typeof setInterval> | null = null

const decodeTokenExp = (token: string): number | null => {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.exp ? payload.exp * 1000 : null
  } catch {
    return null
  }
}

const checkSessionExpiry = () => {
  const token = localStorage.getItem('token')
  if (!token) return

  const expTime = decodeTokenExp(token)
  if (!expTime) return

  const now = Date.now()
  const remaining = expTime - now

  if (remaining <= 0) {
    localStorage.removeItem('token')
    userStore.userLogout()
    ElNotification({
      title: '会话已过期',
      message: '登录已过期，请重新登录',
      type: 'warning',
      duration: 5000
    })
  }
}

const setupTransitionHooks = () => {
  router.beforeEach((to, from) => {
    const toIsAdmin = to.path.startsWith('/admin')
    const fromIsAdmin = from.path.startsWith('/admin')

    if (toIsAdmin !== fromIsAdmin) {
      transitionName.value = 'layout-slide'
    } else if (to.path.startsWith('/admin')) {
      transitionName.value = ''
    } else {
      transitionName.value = 'layout-crossfade'
    }
  })
}

const onBeforeEnter = () => {
  const layout = document.querySelector('.layout-wrapper')
  if (layout) {
    layout.classList.add('entering')
  }
}

const onAfterEnter = () => {
  const layout = document.querySelector('.layout-wrapper')
  if (layout) {
    layout.classList.remove('entering')
  }
}

onMounted(() => {
  setupTransitionHooks()

  if (localStorage.getItem('token')) {
    sessionTimer = setInterval(checkSessionExpiry, 60000)
  }
})

onUnmounted(() => {
  if (sessionTimer) {
    clearInterval(sessionTimer)
  }
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body,
#app {
  height: 100%;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial,
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.layout-wrapper {
  min-height: 100vh;
}

.layout-wrapper.entering {
  animation: entering 0.3s ease-out;
}

@keyframes entering {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.layout-slide-enter-active,
.layout-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.layout-slide-enter-from {
  opacity: 0;
  transform: translateX(30px);
  clip-path: inset(0 0 0 100%);
}

.layout-slide-enter-to {
  opacity: 1;
  transform: translateX(0);
  clip-path: inset(0 0 0 0);
}

.layout-slide-leave-from {
  opacity: 1;
  transform: translateX(0);
  clip-path: inset(0 0 0 0);
}

.layout-slide-leave-to {
  opacity: 0;
  transform: translateX(-30px);
  clip-path: inset(0 100% 0 0);
}

.layout-crossfade-enter-active,
.layout-crossfade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.layout-crossfade-enter-from {
  opacity: 0;
  transform: scale(0.98);
}

.layout-crossfade-enter-to {
  opacity: 1;
  transform: scale(1);
}

.layout-crossfade-leave-from {
  opacity: 1;
  transform: scale(1);
}

.layout-crossfade-leave-to {
  opacity: 0;
  transform: scale(0.98);
}

.el-notification {
  border-radius: 8px;
}
</style>
