<template>
  <div class="login-container">
    <canvas ref="starCanvas" class="star-canvas"></canvas>

    <div class="login-box">
      <div class="login-header">
        <div class="logo-circle">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <linearGradient id="logoGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stop-color="#667eea"/>
                <stop offset="50%" stop-color="#764ba2"/>
                <stop offset="100%" stop-color="#f093fb"/>
              </linearGradient>
              <linearGradient id="logoGradInner" x1="0%" y1="100%" x2="100%" y2="0%">
                <stop offset="0%" stop-color="#a8edea"/>
                <stop offset="100%" stop-color="#fed6e3"/>
              </linearGradient>
              <filter id="glow">
                <feGaussianBlur stdDeviation="2.5" result="blur"/>
                <feMerge>
                  <feMergeNode in="blur"/>
                  <feMergeNode in="SourceGraphic"/>
                </feMerge>
              </filter>
            </defs>
            <g filter="url(#glow)">
              <path d="M24 4L42 14V34L24 44L6 34V14L24 4Z" stroke="url(#logoGrad)" stroke-width="1.8" fill="none"/>
              <path d="M24 12L35 18.5V29.5L24 36L13 29.5V18.5L24 12Z" stroke="url(#logoGradInner)" stroke-width="1.2" fill="rgba(102,126,234,0.06)"/>
              <circle cx="24" cy="24" r="4.5" fill="url(#logoGrad)"/>
              <path d="M24 19V15M24 29V33M20 24H15M28 24H33" stroke="url(#logoGrad)" stroke-width="1.8" stroke-linecap="round"/>
            </g>
          </svg>
        </div>
        <h2 class="platform-name">{{ platformName }}</h2>
        <p class="subtitle">模块化 · 可扩展 · 安全可靠</p>
      </div>

      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        @submit.prevent="handleLogin"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
            class="custom-input"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
            class="custom-input"
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <button
            type="button"
            class="login-button"
            :class="{ loading: loading }"
            :disabled="loading"
            @click="handleLogin"
          >
            <span v-if="!loading" class="btn-text">登 录</span>
            <span v-else class="btn-loading">
              <svg class="spinner" viewBox="0 0 24 24">
                <circle cx="12" cy="12" r="10" fill="none" stroke="currentColor" stroke-width="3" stroke-dasharray="31.4 31.4" stroke-linecap="round"/>
              </svg>
              登录中...
            </span>
          </button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span>Powered by</span>
        <strong>Platform V</strong>
      </div>
    </div>

    <div class="floating-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const platformName = ref('内网万用平台')
const starCanvas = ref<HTMLCanvasElement>()

const loginForm = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

let animationId: number | null = null

const initStars = () => {
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
    x: number
    y: number
    size: number
    speedX: number
    speedY: number
    opacity: number
    twinkleSpeed: number
    twinklePhase: number
  }

  const stars: Star[] = []
  const starCount = Math.floor((canvas.width * canvas.height) / 8000)

  for (let i = 0; i < starCount; i++) {
    stars.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      size: Math.random() * 2 + 0.5,
      speedX: (Math.random() - 0.5) * 0.15,
      speedY: (Math.random() - 0.5) * 0.15,
      opacity: Math.random() * 0.6 + 0.2,
      twinkleSpeed: Math.random() * 0.02 + 0.005,
      twinklePhase: Math.random() * Math.PI * 2
    })
  }

  let time = 0
  const animate = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    time += 0.016

    stars.forEach(star => {
      star.x += star.speedX
      star.y += star.speedY
      star.twinklePhase += star.twinkleSpeed

      if (star.x < -10) star.x = canvas.width + 10
      if (star.x > canvas.width + 10) star.x = -10
      if (star.y < -10) star.y = canvas.height + 10
      if (star.y > canvas.height + 10) star.y = -10

      const twinkle = Math.sin(star.twinklePhase) * 0.3 + 0.7
      const finalOpacity = star.opacity * twinkle

      ctx.beginPath()
      ctx.arc(star.x, star.y, star.size, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(200, 210, 255, ${finalOpacity})`
      ctx.fill()

      if (star.size > 1.5) {
        ctx.beginPath()
        ctx.arc(star.x, star.y, star.size * 2.5, 0, Math.PI * 2)
        ctx.fillStyle = `rgba(160, 170, 255, ${finalOpacity * 0.08})`
        ctx.fill()
      }
    })

    animationId = requestAnimationFrame(animate)
  }
  animate()
}

const handleLogin = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res: any = await login(loginForm)
        localStorage.setItem('token', res.data.token)
        userStore.token = res.data.token
        userStore.userInfo = res.data.user
        userStore.permissions = res.data.user.permissions || []
        localStorage.setItem('user_cache', JSON.stringify(res.data.user))
        ElMessage.success('登录成功')
        router.push('/home')
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(async () => {
  initStars()
  try {
    const res: any = await request.get('/v1/configs')
    if (res?.data?.platformName) {
      platformName.value = res.data.platformName
    }
  } catch (e) {}
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
})
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #0c0d1a 0%, #131628 30%, #1a1a3e 60%, #0f1029 100%);
  overflow: hidden;
}

.star-canvas {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.login-box {
  position: relative;
  z-index: 10;
  width: 420px;
  padding: 48px 44px 40px;
  background: rgba(16, 20, 42, 0.72);
  backdrop-filter: blur(28px);
  border-radius: 24px;
  border: 1px solid rgba(102, 126, 234, 0.12);
  box-shadow:
    0 32px 64px rgba(0, 0, 0, 0.4),
    0 8px 24px rgba(102, 126, 234, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.04);
  animation: box-appear 0.65s cubic-bezier(0.22, 1, 0.36, 1);
}

@keyframes box-appear {
  from {
    opacity: 0;
    transform: translateY(32px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-circle {
  width: 72px;
  height: 72px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, rgba(15, 15, 35, 0.85), rgba(25, 25, 55, 0.8));
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(102, 126, 234, 0.25);
  box-shadow:
    0 8px 32px rgba(102, 126, 234, 0.2),
    0 0 48px rgba(118, 75, 162, 0.1);
  animation: logo-pulse 3s ease-in-out infinite;
}

@keyframes logo-pulse {
  0%, 100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(102, 126, 234, 0); }
  50% { transform: scale(1.05); box-shadow: 0 0 24px rgba(102, 126, 234, 0.15); }
}

.logo-circle svg {
  width: 42px;
  height: 42px;
}

.platform-name {
  margin: 0 0 10px;
  font-size: 26px;
  font-weight: 700;
  color: #e8eaed;
  letter-spacing: 1px;
  text-shadow: 0 2px 16px rgba(102, 126, 234, 0.2);
}

.subtitle {
  margin: 0;
  font-size: 13px;
  color: rgba(180, 190, 220, 0.45);
  letter-spacing: 3px;
}

.login-form {
  --el-input-bg-color: rgba(22, 28, 52, 0.65);
  --el-input-border-color: rgba(102, 126, 234, 0.14);
  --el-input-text-color: #c9d1ff;
  --el-input-placeholder-color: rgba(150, 165, 200, 0.35);
  --el-input-hover-border-color: rgba(102, 126, 234, 0.35);
  --el-input-focus-border-color: #667eea;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.login-form :deep(.el-input__wrapper) {
  background: var(--el-input-bg-color);
  border: 1px solid var(--el-input-border-color);
  border-radius: 12px;
  box-shadow: none;
  transition: all 0.3s ease;
  padding: 4px 14px;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: var(--el-input-hover-border-color);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--el-input-focus-border-color);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.login-form :deep(.el-input__inner) {
  color: var(--el-input-text-color);
  font-size: 14px;
  caret-color: #667eea;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: var(--el-input-placeholder-color);
}

.login-form :deep(.el-input__prefix .el-icon) {
  color: rgba(140, 155, 195, 0.45);
  font-size: 17px;
}

.login-form :deep(.el-input__suffix .el-icon) {
  color: rgba(140, 155, 195, 0.45);
}

.login-button {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.22, 1, 0.36, 1);
  letter-spacing: 4px;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 60%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.12),
    transparent
  );
  transition: left 0.6s ease;
}

.login-button:hover:not(:disabled)::before {
  left: 150%;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 32px rgba(102, 126, 234, 0.35);
}

.login-button:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
}

.login-button.loading {
  pointer-events: none;
  opacity: 0.85;
}

.btn-text {
  position: relative;
  z-index: 2;
}

.btn-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  position: relative;
  z-index: 2;
}

.spinner {
  width: 20px;
  height: 20px;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.login-footer {
  text-align: center;
  margin-top: 28px;
  font-size: 12px;
  color: rgba(140, 155, 195, 0.3);
  letter-spacing: 1px;
}

.login-footer strong {
  color: rgba(160, 170, 210, 0.45);
  margin-left: 4px;
  font-weight: 600;
}

.floating-orbs {
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
  overflow: hidden;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.25;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.4), transparent 70%);
  top: -120px;
  right: -80px;
  animation: orb-float-1 12s ease-in-out infinite;
}

.orb-2 {
  width: 320px;
  height: 320px;
  background: radial-gradient(circle, rgba(168, 85, 247, 0.3), transparent 70%);
  bottom: -80px;
  left: -60px;
  animation: orb-float-2 15s ease-in-out infinite;
}

.orb-3 {
  width: 240px;
  height: 240px;
  background: radial-gradient(circle, rgba(118, 75, 162, 0.25), transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: orb-float-3 10s ease-in-out infinite;
}

@keyframes orb-float-1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(-30px, 20px) scale(1.08); }
  66% { transform: translate(20px, -15px) scale(0.95); }
}

@keyframes orb-float-2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(25px, -20px) scale(1.05); }
  66% { transform: translate(-20px, 15px) scale(0.92); }
}

@keyframes orb-float-3 {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.2; }
  50% { transform: translate(-50%, -50%) scale(1.2); opacity: 0.3; }
}

@media (max-width: 480px) {
  .login-box {
    width: 92%;
    padding: 36px 28px 32px;
    border-radius: 20px;
  }

  .platform-name {
    font-size: 22px;
  }

  .logo-circle {
    width: 54px;
    height: 54px;
  }

  .orb-1, .orb-2 {
    opacity: 0.15;
  }
}
</style>
