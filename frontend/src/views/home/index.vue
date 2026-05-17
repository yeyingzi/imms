<template>
  <div class="home">
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-gradient"></div>
        <div class="hero-shimmer-line"></div>
        <div
          v-for="i in 4"
          :key="i"
          class="hero-orb"
          :style="{ '--delay': `${(i - 1) * 0.8}s`, '--size': 120 + i * 60 + 'px', '--x': i % 2 === 0 ? 'auto' : i * 15 + '%', '--y': i % 2 === 0 ? i * 18 + '%' : 'auto' } as any"
        ></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="greeting">{{ greeting }}</span>
          <span class="name">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
        </h1>
        <p class="hero-subtitle">{{ currentDate }}</p>
        <div class="hero-stats">
          <div class="stat-item" v-for="(s, idx) in statItems" :key="idx">
            <div class="stat-shimmer-wrap">
              <span class="stat-value">{{ s.value }}</span>
              <div class="stat-shimmer"></div>
            </div>
            <span class="stat-label">{{ s.label }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="modules-section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><Grid /></el-icon>
          我的应用
        </h2>
        <p class="section-description">点击卡片快速进入应用</p>
      </div>

      <template v-if="!menuStore.loaded">
        <div class="search-bar skeleton-search">
          <el-skeleton :rows="1" animated />
        </div>
        <div class="modules-grid">
          <div v-for="i in 4" :key="`sk-${i}`" class="module-card skeleton-card">
            <div class="card-content">
              <el-skeleton :rows="3" animated />
            </div>
          </div>
        </div>
      </template>

      <template v-else>

      <div class="search-bar" v-if="dynamicMenus.length > 0">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索应用..."
          clearable
          prefix-icon="Search"
        />
      </div>

      <div class="modules-grid" v-if="filteredMenus.length > 0">
        <div
          v-for="(menu, index) in pagedFilteredMenus"
          :key="menu.path"
          class="module-card"
          :style="{ animationDelay: `${index * 0.08}s` }"
          @click="goToModule(menu.path)"
          @mouseenter="hoveredCard = index"
          @mouseleave="hoveredCard = -1"
        >
          <div class="card-shimmer-border">
            <div class="card-shimmer-bar" :class="{ active: hoveredCard === index }"></div>
          </div>
          <div class="card-content">
            <div class="card-icon-wrapper">
              <el-icon class="card-icon">
                <component :is="menu.icon || 'Folder'" />
              </el-icon>
              <div class="icon-glow"></div>
            </div>
            <h3 class="card-title">{{ menu.name }}</h3>
            <p class="card-description">{{ getCardDescription(menu) }}</p>
          </div>
          <div class="card-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <div v-if="filteredMenus.length > pageSize" class="pagination-wrap">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="filteredMenus.length"
          layout="prev, pager, next"
          :background="false"
          @current-change="onPageChange"
        />
      </div>

      <div v-if="dynamicMenus.length === 0" class="empty-state">
        <div class="empty-icon">
          <el-icon><FolderDelete /></el-icon>
        </div>
        <h3>暂无应用</h3>
        <p>管理员还没有为您分配任何应用</p>
      </div>
      </template>
    </section>

    <section class="quick-actions">
      <div
        v-for="action in actionList"
        :key="action.key"
        class="action-card"
        :class="action.variant || ''"
        @click="action.handler()"
      >
        <div class="action-shine"></div>
        <div class="action-icon">
          <el-icon><component :is="action.icon" /></el-icon>
        </div>
        <div class="action-content">
          <h4>{{ action.title }}</h4>
          <p>{{ action.desc }}</p>
        </div>
        <el-icon class="action-arrow"><ArrowRight /></el-icon>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const menuStore = useMenuStore()

const currentTime = ref(new Date())
let timer: number | null = null
const hoveredCard = ref(-1)
const currentPage = ref(1)
const pageSize = 8
const searchKeyword = ref('')

const greeting = computed(() => {
  const hour = currentTime.value.getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const currentDate = computed(() => {
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  }
  return currentTime.value.toLocaleDateString('zh-CN', options)
})

const timeString = computed(() => {
  return currentTime.value.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
})

const statItems = computed(() => [
  { value: dynamicMenus.value.length, label: '可用模块' },
  { value: timeString.value, label: '当前时间' }
])

const dynamicMenus = computed(() => menuStore.dynamicMenus)

const filteredMenus = computed(() => {
  if (!searchKeyword.value.trim()) return dynamicMenus.value
  const keyword = searchKeyword.value.toLowerCase().trim()
  return dynamicMenus.value.filter(menu =>
    menu.name.toLowerCase().includes(keyword) ||
    (menu.description && menu.description.toLowerCase().includes(keyword))
  )
})

const pagedFilteredMenus = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredMenus.value.slice(start, start + pageSize)
})

const isAdmin = computed(() => userStore.userInfo?.roles?.includes('SUPER_ADMIN'))

const actionList = reactive([
  { key: 'profile', title: '个人中心', desc: '管理个人信息', icon: 'User', variant: '', handler: () => router.push('/profile') },
  ...(isAdmin.value ? [{ key: 'admin', title: '管理后台', desc: '系统管理入口', icon: 'Setting', variant: 'admin', handler: () => router.push('/admin') }] : []),
  { key: 'logout', title: '退出登录', desc: '安全退出当前账号', icon: 'SwitchButton', variant: 'logout', handleLogout: null, handler: async () => {
    try {
      await ElMessageBox.confirm('确定要退出当前账号吗？', '退出确认', {
        confirmButtonText: '确认退出',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await userStore.userLogout()
      ElMessage.success('已退出登录')
    } catch { }
  } }
])

const getCardDescription = (menu: any) => {
  return menu.description || '点击进入应用'
}

const goToModule = (path: string) => router.push(path)

const onPageChange = (page: number) => {
  currentPage.value = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  menuStore.loadModuleMenus()
  timer = window.setInterval(() => { currentTime.value = new Date() }, 1000)
})

onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<style scoped>
.home {
  padding-bottom: 80px;
  position: relative;
  z-index: 1;
}

.hero {
  position: relative;
  border-radius: 24px;
  padding: 52px;
  margin-bottom: 56px;
  overflow: hidden;
  box-shadow:
    0 20px 60px rgba(102, 126, 234, 0.15),
    0 8px 30px rgba(118, 75, 162, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.hero-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.hero-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.35) 0%,
    rgba(118, 75, 162, 0.28) 40%,
    rgba(168, 85, 247, 0.22) 70%,
    rgba(102, 126, 234, 0.32) 100%
  );
  background-size: 300% 300%;
  animation: hero-gradient-shift 10s ease infinite;
}

@keyframes hero-gradient-shift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.hero-shimmer-line {
  position: absolute;
  top: 0;
  left: -80%;
  width: 40%;
  height: 100%;
  background: linear-gradient(
    105deg,
    transparent 20%,
    rgba(255, 255, 255, 0.05) 45%,
    rgba(255, 255, 255, 0.09) 50%,
    rgba(255, 255, 255, 0.05) 55%,
    transparent 80%
  );
  transform: skewX(-16deg);
  animation: hero-sweep 5s ease-in-out infinite;
}

@keyframes hero-sweep {
  0% { left: -80%; opacity: 0; }
  15% { opacity: 1; }
  85% { opacity: 1; }
  100% { left: 140%; opacity: 0; }
}

.hero-orb {
  position: absolute;
  width: var(--size);
  height: var(--size);
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.055) 0%, transparent 65%);
  right: var(--x);
  bottom: var(--y);
  animation: orb-float 7s ease-in-out var(--delay) infinite;
}

@keyframes orb-float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(15px, -20px) scale(1.05); }
  66% { transform: translate(-10px, 10px) scale(0.95); }
}

.hero-content {
  position: relative;
  z-index: 2;
}

.hero-title {
  margin: 0 0 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.greeting {
  font-size: 20px;
  font-weight: 400;
  color: rgba(232, 234, 237, 0.8);
}

.name {
  font-size: 36px;
  font-weight: 700;
  color: #f0f2ff;
  text-shadow: 0 2px 24px rgba(102, 126, 234, 0.25);
}

.hero-subtitle {
  margin: 0 0 32px;
  font-size: 16px;
  color: rgba(201, 209, 255, 0.6);
}

.hero-stats {
  display: flex;
  gap: 36px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-shimmer-wrap {
  position: relative;
  display: inline-block;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #f0f2ff;
  text-shadow: 0 2px 16px rgba(102, 126, 234, 0.18);
  position: relative;
  z-index: 2;
}

.stat-shimmer {
  position: absolute;
  bottom: 2px;
  left: 0;
  width: 100%;
  height: 6px;
  background: linear-gradient(90deg, transparent, rgba(165,180,252,0.45), transparent);
  border-radius: 3px;
  animation: stat-glow 3s ease-in-out infinite;
}

@keyframes stat-glow {
  0%, 100% { opacity: 0; transform: scaleX(0); }
  50% { opacity: 1; transform: scaleX(1); }
}

.stat-label {
  font-size: 14px;
  color: rgba(201, 209, 255, 0.48);
}

.section-header {
  margin-bottom: 36px;
}

.section-title {
  margin: 0 0 8px;
  font-size: 23px;
  font-weight: 700;
  color: #e8eaed;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-description {
  margin: 0;
  font-size: 14px;
  color: rgba(201, 209, 255, 0.42);
}

.search-bar {
  max-width: 400px;
  margin-bottom: 24px;
}

.search-bar :deep(.el-input__wrapper) {
  background: rgba(14, 22, 42, 0.5);
  border: 1px solid rgba(102, 126, 234, 0.12);
  border-radius: 12px;
  box-shadow: none;
  transition: all 0.3s ease;
}

.search-bar :deep(.el-input__wrapper:hover),
.search-bar :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(102, 126, 234, 0.35);
  box-shadow: 0 0 20px rgba(102, 126, 234, 0.1);
}

.search-bar :deep(.el-input__inner) {
  color: #e8eaed;
}

.search-bar :deep(.el-input__prefix .el-icon) {
  color: rgba(180, 190, 220, 0.5);
}

.modules-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 22px;
  margin-bottom: 32px;
}

.module-card {
  position: relative;
  background: rgba(14, 22, 42, 0.65);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 32px;
  cursor: pointer;
  transition: all 0.42s cubic-bezier(0.22, 1, 0.36, 1);
  animation: card-rise 0.55s ease-out forwards;
  opacity: 0;
  overflow: hidden;
  border: 1px solid rgba(102, 126, 234, 0.08);
}

@keyframes card-rise {
  from { opacity: 0; transform: translateY(28px) scale(0.97); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.module-card:hover {
  transform: translateY(-10px) scale(1.01);
  box-shadow:
    0 24px 48px rgba(102, 126, 234, 0.12),
    0 8px 24px rgba(0, 0, 0, 0.3);
  border-color: rgba(102, 126, 234, 0.2);
  background: rgba(18, 28, 52, 0.78);
}

.card-shimmer-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  overflow: hidden;
  border-radius: 20px 20px 0 0;
}

.card-shimmer-bar {
  position: absolute;
  top: 0;
  left: -120%;
  width: 50%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(102,126,234,0.6), rgba(168,85,247,0.5), transparent);
  transition: none;
}

.card-shimmer-bar.active {
  animation: card-shimmer-run 0.9s ease forwards;
}

@keyframes card-shimmer-run {
  to { left: 170%; }
}

.card-content {
  position: relative;
  z-index: 2;
}

.card-icon-wrapper {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.12) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  transition: all 0.38s ease;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(102, 126, 234, 0.06);
}

.module-card:hover .card-icon-wrapper {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 8px 28px rgba(102, 126, 234, 0.28);
  transform: scale(1.08);
  border-color: transparent;
}

.module-card:hover .card-icon-wrapper .card-icon {
  color: white;
}

.icon-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 200%;
  height: 200%;
  transform: translate(-50%, -50%);
  background: radial-gradient(circle, rgba(255,255,255,0.18) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.38s;
  pointer-events: none;
}

.module-card:hover .icon-glow {
  opacity: 1;
  animation: icon-pulse 1.8s ease-in-out infinite;
}

@keyframes icon-pulse {
  0%, 100% { transform: translate(-50%, -50%) scale(0.85); opacity: 0.5; }
  50% { transform: translate(-50%, -50%) scale(1.1); opacity: 1; }
}

.card-icon {
  font-size: 30px;
  color: #a5b4fc;
  transition: color 0.38s ease;
  position: relative;
  z-index: 2;
}

.card-title {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight: 600;
  color: #e8eaed;
}

.card-description {
  margin: 0;
  font-size: 14px;
  color: rgba(201, 209, 255, 0.44);
}

.card-arrow {
  position: absolute;
  top: 32px;
  right: 32px;
  font-size: 20px;
  color: rgba(102, 126, 234, 0.25);
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.35s cubic-bezier(0.22, 1, 0.36, 1);
}

.module-card:hover .card-arrow {
  opacity: 1;
  transform: translateX(0);
  color: #a5b4fc;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px 0 48px;
}

.pagination-wrap :deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: rgba(201, 209, 255, 0.5);
  --el-pagination-button-bg-color: rgba(14, 22, 42, 0.5);
  --el-pagination-button-color: #c9d1ff;
  --el-pagination-hover-color: #a5b4fc;
  --el-pagination-active-bg-color: rgba(102, 126, 234, 0.25);
  --el-pagination-active-text-color: #e8eaed;
  --el-pagination-border-radius: 10px;
  --el-pagination-button-disabled-bg-color: rgba(14, 22, 42, 0.25);
  --el-pagination-button-disabled-color: rgba(120, 130, 150, 0.4);
}

.pagination-wrap :deep(.el-pager li) {
  background: rgba(14, 22, 42, 0.5);
  border: 1px solid rgba(102, 126, 234, 0.08);
  border-radius: 10px;
  min-width: 36px;
  height: 36px;
  line-height: 34px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.pagination-wrap :deep(.el-pager li.is-active) {
  background: rgba(102, 126, 234, 0.22);
  border-color: rgba(102, 126, 234, 0.3);
  color: #e8eaed;
  font-weight: 700;
}

.pagination-wrap :deep(.el-pager li:hover:not(.is-active)) {
  background: rgba(102, 126, 234, 0.12);
  border-color: rgba(102, 126, 234, 0.18);
  color: #c9d1ff;
}

.pagination-wrap :deep(.btn-prev),
.pagination-wrap :deep(.btn-next) {
  background: rgba(14, 22, 42, 0.5);
  border: 1px solid rgba(102, 126, 234, 0.08);
  border-radius: 10px;
  min-width: 36px;
  height: 36px;
  color: #c9d1ff;
  transition: all 0.3s ease;
}

.pagination-wrap :deep(.btn-prev:hover),
.pagination-wrap :deep(.btn-next:hover) {
  background: rgba(102, 126, 234, 0.12);
  border-color: rgba(102, 126, 234, 0.18);
  color: #a5b4fc;
}

.empty-state {
  text-align: center;
  padding: 72px 40px;
  background: rgba(14, 22, 42, 0.55);
  backdrop-filter: blur(16px);
  border-radius: 20px;
  border: 1px dashed rgba(102, 126, 234, 0.14);
  margin-bottom: 56px;
}

.empty-icon {
  width: 76px;
  height: 76px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.06));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 38px;
  color: rgba(201, 209, 255, 0.3);
}

.empty-state h3 {
  margin: 0 0 8px;
  font-size: 18px;
  color: #c9d1ff;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
  color: rgba(201, 209, 255, 0.36);
}

.skeleton-search {
  pointer-events: none;
}

.skeleton-search :deep(.el-skeleton__item) {
  height: 44px !important;
  border-radius: 12px !important;
  background: linear-gradient(
    90deg,
    rgba(102, 126, 234, 0.06) 25%,
    rgba(102, 126, 234, 0.12) 50%,
    rgba(102, 126, 234, 0.06) 75%
  );
  background-size: 200% 100%;
  animation: skeleton-shimmer 1.8s ease-in-out infinite;
}

.skeleton-card {
  pointer-events: none;
  background: rgba(14, 22, 42, 0.5);
}

.skeleton-card :deep(.el-skeleton__item) {
  background: linear-gradient(
    90deg,
    rgba(102, 126, 234, 0.06) 25%,
    rgba(102, 126, 234, 0.12) 50%,
    rgba(102, 126, 234, 0.06) 75%
  );
  background-size: 200% 100%;
  animation: skeleton-shimmer 1.8s ease-in-out infinite;
  border-radius: 8px !important;
}

.skeleton-card :deep(.el-skeleton__paragraph li:last-child) {
  width: 60% !important;
}

@keyframes skeleton-shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 22px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 24px;
  background: rgba(14, 22, 42, 0.6);
  backdrop-filter: blur(18px);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.22, 1, 0.36, 1);
  border: 1px solid rgba(102, 126, 234, 0.07);
  position: relative;
  overflow: hidden;
}

.action-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 60%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(102, 126, 234, 0.06),
    transparent
  );
  transition: left 0.6s ease;
  pointer-events: none;
}

.action-card:hover .action-shine {
  left: 150%;
}

.action-card:hover {
  transform: translateY(-3px) translateX(6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
  border-color: rgba(102, 126, 234, 0.16);
  background: rgba(18, 28, 52, 0.72);
}

.action-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
  position: relative;
  z-index: 2;
  transition: box-shadow 0.35s;
}

.action-card:hover .action-icon {
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.3);
}

.action-card.admin .action-icon {
  background: linear-gradient(135deg, #f56c6c 0%, #e64a19 100%);
}

.action-card.admin:hover .action-icon {
  box-shadow: 0 6px 24px rgba(245, 108, 108, 0.3);
}

.action-card.logout .action-icon {
  background: linear-gradient(135deg, rgba(120,130,150,0.8) 0%, rgba(80,90,110,0.8) 100%);
}

.action-content {
  flex: 1;
  position: relative;
  z-index: 2;
}

.action-content h4 {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
  color: #e8eaed;
}

.action-content p {
  margin: 0;
  font-size: 14px;
  color: rgba(201, 209, 255, 0.38);
}

.action-arrow {
  font-size: 20px;
  color: rgba(102, 126, 234, 0.2);
  transition: all 0.35s ease;
  position: relative;
  z-index: 2;
}

.action-card:hover .action-arrow {
  transform: translateX(5px);
  color: #a5b4fc;
}

@media (max-width: 768px) {
  .hero {
    padding: 32px 24px;
    border-radius: 16px;
  }

  .name {
    font-size: 26px;
  }

  .modules-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .quick-actions {
    grid-template-columns: 1fr;
  }

  .hero-orb {
    display: none;
  }

  .pagination-wrap {
    padding: 12px 0 36px;
  }

  .section-header {
    text-align: center;
  }

  .search-bar {
    max-width: 100%;
  }

  .stat-item {
    padding: 8px 12px;
  }

  .stat-value {
    font-size: 18px;
  }

  .stat-label {
    font-size: 11px;
  }
}
</style>
