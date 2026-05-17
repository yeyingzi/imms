<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="6" v-for="item in statCards" :key="item.key">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-card__body">
            <div class="stat-card__icon" :style="{ background: item.color }">
              <el-icon :size="28"><component :is="item.icon" /></el-icon>
            </div>
            <div class="stat-card__content">
              <div class="stat-card__value">{{ stats[item.key as keyof typeof stats] }}</div>
              <div class="stat-card__label">{{ item.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><InfoFilled /></el-icon>
              <span>系统信息</span>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="平台名称">{{ systemInfo.platformName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="系统版本">{{ systemInfo.version || '-' }}</el-descriptions-item>
            <el-descriptions-item label="运行环境">{{ systemInfo.env || '-' }}</el-descriptions-item>
            <el-descriptions-item label="Java版本">{{ systemInfo.javaVersion || '-' }}</el-descriptions-item>
            <el-descriptions-item label="操作系统">{{ systemInfo.osName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="运行时长">{{ systemInfo.uptime || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Operation /></el-icon>
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <div
              v-for="action in quickActions"
              :key="action.path"
              class="quick-action"
              @click="navigateTo(action.path)"
            >
              <el-icon :size="18"><component :is="action.icon" /></el-icon>
              <span>{{ action.label }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()

const stats = reactive({
  userCount: 0,
  moduleCount: 0,
  operationLogCount: 0,
  loginLogCount: 0,
  onlineUserCount: 0
})

const statCards = [
  { key: 'userCount', label: '用户总数', icon: 'User', color: '#409eff' },
  { key: 'moduleCount', label: '模块数量', icon: 'Grid', color: '#67c23a' },
  { key: 'operationLogCount', label: '操作日志', icon: 'Document', color: '#e6a23c' },
  { key: 'onlineUserCount', label: '在线用户', icon: 'Clock', color: '#f56c6c' }
]

const quickActions = [
  { path: '/admin/user', label: '用户管理', icon: 'User' },
  { path: '/admin/module', label: '模块管理', icon: 'Grid' },
  { path: '/admin/config', label: '系统配置', icon: 'Setting' },
  { path: '/admin/log', label: '日志查看', icon: 'Document' }
]

const systemInfo = reactive({
  platformName: '',
  version: '',
  env: '',
  javaVersion: '',
  osName: '',
  uptime: ''
})

const loadStats = async () => {
  try {
    const res = await request.get<any, { data: any }>('/v1/dashboard/stats')
    if (res.data) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    console.error('Failed to load stats:', error)
  }
}

const loadSystemInfo = async () => {
  try {
    const res = await request.get<any, { data: any }>('/v1/dashboard/system-info')
    if (res.data) {
      Object.assign(systemInfo, res.data)
    }
  } catch (error) {
    console.error('Failed to load system info:', error)
  }
}

const navigateTo = (path: string) => {
  router.push(path)
}

onMounted(() => {
  loadStats()
  loadSystemInfo()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  margin-bottom: 0;
}

.stat-card :deep(.el-card__body) {
  padding: 24px;
}

.stat-card__body {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-card__icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.stat-card__content {
  min-width: 0;
}

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  color: #1d2129;
  line-height: 1.2;
}

.stat-card__label {
  font-size: 13px;
  color: #86909c;
  margin-top: 4px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: #1d2129;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.quick-action {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: #f7f8fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  color: #4e5969;
}

.quick-action:hover {
  background: #e8f3ff;
  color: #409eff;
}
</style>
