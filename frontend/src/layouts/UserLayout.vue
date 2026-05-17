<template>
  <div class="user-app">
    <canvas ref="starCanvas" class="star-canvas"></canvas>

    <header class="header">
      <div class="shimmer-border"></div>
      <div class="header-content">
        <div class="logo">
          <div class="logo-icon">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <linearGradient id="logoGrad1" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#667eea"/>
                  <stop offset="50%" stop-color="#764ba2"/>
                  <stop offset="100%" stop-color="#f093fb"/>
                </linearGradient>
                <linearGradient id="logoGrad2" x1="0%" y1="100%" x2="100%" y2="0%">
                  <stop offset="0%" stop-color="#a8edea"/>
                  <stop offset="100%" stop-color="#fed6e3"/>
                </linearGradient>
                <filter id="glow">
                  <feGaussianBlur stdDeviation="2" result="blur"/>
                  <feMerge>
                    <feMergeNode in="blur"/>
                    <feMergeNode in="SourceGraphic"/>
                  </feMerge>
                </filter>
              </defs>
              <g filter="url(#glow)">
                <path d="M24 4L42 14V34L24 44L6 34V14L24 4Z" stroke="url(#logoGrad1)" stroke-width="1.5" fill="none"/>
                <path d="M24 12L35 18.5V29.5L24 36L13 29.5V18.5L24 12Z" stroke="url(#logoGrad2)" stroke-width="1" fill="rgba(102,126,234,0.08)"/>
                <circle cx="24" cy="24" r="4" fill="url(#logoGrad1)"/>
                <path d="M24 20V16M24 28V32M20 24H16M28 24H32" stroke="url(#logoGrad1)" stroke-width="1.5" stroke-linecap="round"/>
              </g>
            </svg>
            <div class="logo-shimmer"></div>
          </div>
          <span class="logo-text">{{ systemConfig.platformName || '家庭助手' }}</span>
        </div>

        <nav class="header-nav">
          <button
            class="nav-home-btn"
            :class="{ active: route.path === '/home' }"
            @click="router.push('/home')"
          >
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </button>
        </nav>

        <div class="user-section">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" class="user-avatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item v-if="isAdmin" command="admin">管理后台</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <footer class="footer">
      <div class="footer-glow"></div>
      <p>© 2026 {{ systemConfig.platformName || '家庭助手' }} · 让生活更美好</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, UserFilled, ArrowDown } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const menuStore = useMenuStore()
const starCanvas = ref<HTMLCanvasElement | null>(null)
let animFrame = 0

const systemConfig = reactive({
  platformName: '',
  logo: '',
  themeColor: ''
})

onMounted(async () => {
  menuStore.loadModuleMenus()
  initStarfield()
  await loadSystemConfig()
})

const loadSystemConfig = async () => {
  try {
    const res: any = await request.get('/v1/configs')
    if (res && res.data) {
      systemConfig.platformName = res.data.platformName || ''
      systemConfig.logo = res.data.logo || ''
      systemConfig.themeColor = res.data.themeColor || '#409eff'
    }
  } catch (e) {
  }
}

onUnmounted(() => {
  if (animFrame) cancelAnimationFrame(animFrame)
})

const isAdmin = computed(() => userStore.userInfo?.roles?.includes('SUPER_ADMIN'))

const initStarfield = () => {
  const canvas = starCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  const resize = () => {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
  resize()
  window.addEventListener('resize', resize)

  interface Star {
    x: number; y: number; r: number; a: number; da: number; s: number; c: string
  }

  const stars: Star[] = []
  const count = Math.floor((window.innerWidth * window.innerHeight) / 6000)
  const palette = ['#ffffff', '#e8eaed', '#c9d1ff', '#b4c5ff', '#a8bfff']

  for (let i = 0; i < count; i++) {
    stars.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      r: Math.random() * 1.6 + 0.3,
      a: Math.random() * 0.7 + 0.3,
      da: (Math.random() - 0.5) * 0.02,
      s: Math.random() * 0.15 + 0.03,
      c: palette[Math.floor(Math.random() * palette.length)]
    })
  }

  let lastTime = performance.now()

  const draw = (time: number) => {
    const dt = time - lastTime
    lastTime = time

    ctx.clearRect(0, 0, canvas.width, canvas.height)

    for (const st of stars) {
      st.a += st.da
      if (st.a > 1) { st.a = 1; st.da *= -1 }
      if (st.a < 0.25) { st.a = 0.25; st.da *= -1 }

      st.y += st.s * dt * 0.06
      if (st.y > canvas.height + 10) {
        st.y = -5
        st.x = Math.random() * canvas.width
      }

      ctx.beginPath()
      ctx.arc(st.x, st.y, st.r, 0, Math.PI * 2)
      ctx.fillStyle = st.c
      ctx.globalAlpha = st.a
      ctx.fill()
    }

    ctx.globalAlpha = 1
    animFrame = requestAnimationFrame(draw)
  }

  animFrame = requestAnimationFrame(draw)
}

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出当前账号吗？', '退出确认', {
        confirmButtonText: '确认退出',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await userStore.userLogout()
      ElMessage.success('已退出登录')
    } catch { }
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'admin') {
    router.push('/admin')
  }
}
</script>

<style scoped>
.user-app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow-x: hidden;
  background: #070b14;
}

.star-canvas {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.header {
  background: rgba(10, 16, 30, 0.78);
  backdrop-filter: blur(28px) saturate(160%);
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow:
    0 1px 0 rgba(102, 126, 234, 0.05),
    0 8px 40px rgba(0, 0, 0, 0.35);
}

.shimmer-border {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  overflow: hidden;
}

.shimmer-border::after {
  content: '';
  position: absolute;
  left: -100%;
  width: 50%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(102, 126, 234, 0.55),
    rgba(168, 85, 247, 0.45),
    transparent
  );
  animation: shimmer-slide 4s ease-in-out infinite;
}

@keyframes shimmer-slide {
  0% { left: -50%; }
  100% { left: 150%; }
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
  height: 68px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, rgba(15, 15, 35, 0.9), rgba(25, 25, 55, 0.85));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow:
    0 4px 20px rgba(102, 126, 234, 0.3),
    0 0 30px rgba(118, 75, 162, 0.15);
  position: relative;
  overflow: hidden;
}

.logo-icon svg {
  width: 28px;
  height: 28px;
  position: relative;
  z-index: 2;
}

.logo-shimmer {
  position: absolute;
  top: -50%;
  left: -60%;
  width: 30%;
  height: 200%;
  background: linear-gradient(
    105deg,
    transparent 35%,
    rgba(255, 255, 255, 0.3) 50%,
    transparent 65%
  );
  transform: skewX(-20deg);
  animation: icon-shimmer 3s ease-in-out infinite;
}

@keyframes icon-shimmer {
  0% { transform: translateX(-150%) skewX(-20deg); }
  50% { transform: translateX(300%) skewX(-20deg); }
  100% { transform: translateX(300%) skewX(-20deg); opacity: 0; }
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(
    135deg,
    #667eea 0%,
    #764ba2 33%,
    #f093fb 66%,
    #667eea 100%
  );
  background-size: 240% 240%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradient-flow 6s ease infinite;
}

@keyframes gradient-flow {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.header-nav {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.nav-home-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  background: transparent;
  border: 1px solid rgba(102, 126, 234, 0.12);
  border-radius: 10px;
  color: rgba(180, 190, 220, 0.6);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  letter-spacing: 1px;
}

.nav-home-btn:hover {
  color: #c9d1ff;
  border-color: rgba(102, 126, 234, 0.3);
  background: rgba(102, 126, 234, 0.08);
}

.nav-home-btn.active {
  color: #e8eaed;
  border-color: rgba(102, 126, 234, 0.28);
  background: rgba(102, 126, 234, 0.12);
}

.user-section {
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 16px 6px 6px;
  border-radius: 50px;
  background: rgba(102, 126, 234, 0.08);
  cursor: pointer;
  transition: all 0.35s ease;
  border: 1px solid rgba(102, 126, 234, 0.08);
  position: relative;
  overflow: hidden;
}

.user-info:hover {
  border-color: rgba(102, 126, 234, 0.22);
  background: rgba(102, 126, 234, 0.12);
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 14px rgba(102, 126, 234, 0.35);
  position: relative;
  z-index: 2;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: #e8eaed;
  position: relative;
  z-index: 2;
}

.main-content {
  flex: 1;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  padding: 36px 40px;
  position: relative;
  z-index: 1;
}

.footer {
  text-align: center;
  padding: 28px;
  color: rgba(201, 209, 255, 0.38);
  font-size: 14px;
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.footer-glow {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 400px;
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(102, 126, 234, 0.3),
    rgba(168, 85, 247, 0.25),
    transparent
  );
  animation: footer-pulse 4s ease-in-out infinite;
}

@keyframes footer-pulse {
  0%, 100% { opacity: 0.35; width: 200px; }
  50% { opacity: 1; width: 520px; }
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
    height: 60px;
  }

  .logo-text {
    font-size: 18px;
  }

  .header-nav {
    display: none;
  }

  .username {
    display: none;
  }

  .user-avatar {
    width: 28px !important;
    height: 28px !important;
  }

  .main-content {
    padding: 20px 12px;
  }

  .footer {
    padding: 16px;
    font-size: 12px;
  }
}
</style>
