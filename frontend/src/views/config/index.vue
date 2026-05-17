<template>
  <div class="config-container">
    <el-card>
      <template #header>
        <span>系统配置</span>
      </template>

      <el-form :model="configForm" label-width="150px">
        <el-form-item label="平台名称">
          <el-input v-model="configForm.platformName" placeholder="请输入平台名称" />
        </el-form-item>

        <el-form-item label="平台Logo URL">
          <el-input v-model="configForm.logo" placeholder="请输入Logo URL" />
        </el-form-item>

        <el-form-item label="主题颜色">
          <el-color-picker v-model="configForm.themeColor" />
        </el-form-item>

        <el-form-item label="登录超时时间">
          <el-input-number
            v-model="configForm.loginTimeout"
            :min="30"
            :max="3600"
            placeholder="分钟"
          />
          <span style="margin-left: 10px">分钟</span>
        </el-form-item>

        <el-form-item label="密码最小长度">
          <el-input-number
            v-model="configForm.passwordMinLength"
            :min="6"
            :max="20"
          />
        </el-form-item>

        <el-form-item label="登录失败锁定次数">
          <el-input-number
            v-model="configForm.maxLoginFailures"
            :min="3"
            :max="10"
          />
        </el-form-item>

        <el-form-item label="登录锁定时间">
          <el-input-number
            v-model="configForm.lockoutDuration"
            :min="5"
            :max="60"
          />
          <span style="margin-left: 10px">分钟</span>
        </el-form-item>

        <el-form-item label="日志保留天数">
          <el-input-number
            v-model="configForm.logRetentionDays"
            :min="7"
            :max="365"
          />
          <span style="margin-left: 10px">天</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave">保存配置</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)

const configForm = reactive({
  platformName: '内网万用平台',
  logo: '',
  themeColor: '#409eff',
  loginTimeout: 120,
  passwordMinLength: 6,
  maxLoginFailures: 5,
  lockoutDuration: 15,
  logRetentionDays: 90
})

const loadConfig = async () => {
  loading.value = true
  try {
    const res = await request.get('/v1/configs')
    if (res.data) {
      const data = res.data as Record<string, any>
      configForm.platformName = data.platformName || '内网万用平台'
      configForm.logo = data.logo || ''
      configForm.themeColor = data.themeColor || '#409eff'
      configForm.loginTimeout = Number(data.loginTimeout) || 120
      configForm.passwordMinLength = Number(data.passwordMinLength) || 6
      configForm.maxLoginFailures = Number(data.maxLoginFailures) || 5
      configForm.lockoutDuration = Number(data.lockoutDuration) || 15
      configForm.logRetentionDays = Number(data.logRetentionDays) || 90
    }
  } catch (error) {
    console.error('Failed to load config:', error)
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  try {
    await request.put('/v1/configs', configForm)
    ElMessage.success('配置保存成功')
  } catch (error) {
    ElMessage.error('配置保存失败')
  }
}

const handleReset = () => {
  loadConfig()
}

onMounted(() => {
  loadConfig()
})
</script>

<style scoped>
.config-container {
  padding: 20px;
  max-width: 800px;
}

@media (max-width: 768px) {
  .config-container {
    padding: 12px;
    max-width: 100%;
  }

  .el-form-item__label {
    font-size: 13px;
  }
}
</style>
