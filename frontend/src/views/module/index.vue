<template>
  <div class="module-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>模块列表</span>
          <el-text type="info">代码模块在 modules 目录，修改后需重启前端</el-text>
        </div>
      </template>

      <el-table :data="modules" v-loading="loading" stripe>
        <el-table-column label="模块信息" min-width="200">
          <template #default="{ row }">
            <div class="module-info">
              <el-icon :size="24">
                <component :is="row.icon || 'Box'" />
              </el-icon>
              <div class="module-detail">
                <div class="module-name">{{ row.name || row.moduleKey }}</div>
                <div class="module-key">{{ row.moduleKey }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="version" label="版本" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="info">v{{ row.version || '1.0' }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />

        <el-table-column label="启用/停用" width="120" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleToggle(row)"
              :loading="row.loading"
            />
          </template>
        </el-table-column>
      </el-table>

      <el-alert
        type="info"
        :closable="false"
        class="tips"
      >
        <template #title>
          <strong>模块管理说明</strong>
        </template>
        <div class="tips-content">
          <p>• <strong>模块代码</strong>：位于 <code>modules/</code> 目录，修改后需重启前端服务</p>
          <p>• <strong>数据库记录</strong>：模块信息存储在 sys_module 表，可通过管理界面启用/停用</p>
          <p>• <strong>新增模块</strong>：将模块文件夹放入 <code>modules/</code> 目录，然后重启前端服务</p>
        </div>
      </el-alert>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useMenuStore } from '@/stores/menu'
import request from '@/utils/request'

interface Module {
  id: number
  moduleKey: string
  name: string
  version: string
  author: string
  description: string
  icon: string
  status: number
  loading?: boolean
}

const loading = ref(false)
const modules = ref<Module[]>([])
const menuStore = useMenuStore()

const loadModules = async () => {
  loading.value = true
  try {
    const res = await request.get('/v1/modules')
    modules.value = res.data || []
  } catch (error) {
    console.error('Failed to load modules:', error)
    ElMessage.error('加载模块列表失败')
  } finally {
    loading.value = false
  }
}

const handleToggle = async (module: Module) => {
  const isEnabling = module.status === 1
  const action = isEnabling ? '启用' : '停用'

  try {
    await ElMessageBox.confirm(
      `确定要${action}模块「${module.name || module.moduleKey}」吗？${isEnabling ? '' : '停用后所有用户将无法访问该模块。'}`,
      `${action}模块确认`,
      {
        confirmButtonText: `确认${action}`,
        cancelButtonText: '取消',
        type: isEnabling ? 'info' : 'warning'
      }
    )
  } catch {
    module.status = isEnabling ? 0 : 1
    return
  }

  module.loading = true
  try {
    await request.put(`/v1/modules/${module.id}/toggle`)
    ElMessage.success(`模块已${action}`)
    await menuStore.refreshMenus()
  } catch (error) {
    module.status = isEnabling ? 0 : 1
    ElMessage.error('操作失败')
  } finally {
    module.loading = false
  }
}

onMounted(() => {
  loadModules()
})
</script>

<style scoped>
.module-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.module-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.module-info .el-icon {
  font-size: 24px;
  color: #409eff;
}

.module-detail {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.module-name {
  font-weight: 600;
  color: #303133;
}

.module-key {
  font-size: 12px;
  color: #909399;
  font-family: monospace;
}

.tips {
  margin-top: 20px;
}

.tips-content p {
  margin: 8px 0;
  font-size: 14px;
  color: #606266;
}

.tips-content code {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
  color: #409eff;
  font-size: 13px;
}

@media (max-width: 768px) {
  .module-container {
    padding: 12px;
  }

  .card-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .tips-content p {
    font-size: 13px;
  }

  .el-table :deep(.cell) {
    padding: 8px 4px;
    font-size: 13px;
  }
}
</style>
