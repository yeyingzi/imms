<template>
  <div class="log-container">
    <el-card>
      <template #header>
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="操作日志" name="operation" />
          <el-tab-pane label="登录日志" name="login" />
        </el-tabs>
      </template>

      <div class="search-bar" v-if="activeTab === 'operation'">
        <el-form :inline="true" :model="operationSearchForm">
          <el-form-item label="用户名">
            <el-input
              v-model="operationSearchForm.username"
              placeholder="请输入用户名"
              clearable
            />
          </el-form-item>
          <el-form-item label="操作模块">
            <el-input
              v-model="operationSearchForm.module"
              placeholder="请输入操作模块"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleOperationSearch">
              查询
            </el-button>
            <el-button @click="handleOperationReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="search-bar" v-if="activeTab === 'login'">
        <el-form :inline="true" :model="loginSearchForm">
          <el-form-item label="用户名">
            <el-input
              v-model="loginSearchForm.username"
              placeholder="请输入用户名"
              clearable
            />
          </el-form-item>
          <el-form-item label="登录状态">
            <el-select
              v-model="loginSearchForm.status"
              placeholder="请选择状态"
              clearable
            >
              <el-option label="成功" :value="1" />
              <el-option label="失败" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleLoginSearch">查询</el-button>
            <el-button @click="handleLoginReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table
        :data="tableData"
        stripe
        v-loading="loading"
        v-if="activeTab === 'operation'"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="module" label="操作模块" />
        <el-table-column prop="action" label="操作动作" />
        <el-table-column prop="description" label="操作描述" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="createdAt" label="操作时间" width="180" />
      </el-table>

      <el-table
        :data="tableData"
        stripe
        v-loading="loading"
        v-if="activeTab === 'login'"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="loginType" label="登录类型" width="100">
          <template #default="{ row }">
            {{ row.loginType === 1 ? '登录' : '登出' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMsg" label="错误信息" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="userAgent" label="浏览器" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="登录时间" width="180" />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  getOperationLogList,
  getLoginLogList,
  type OperationLog,
  type LoginLog
} from '@/api/log'

const loading = ref(false)
const tableData = ref<(OperationLog | LoginLog)[]>([])
const activeTab = ref<'operation' | 'login'>('operation')

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const operationSearchForm = reactive({
  username: '',
  module: ''
})

const loginSearchForm = reactive({
  username: '',
  status: null as number | null
})

const loadOperationData = async () => {
  loading.value = true
  try {
    const res: any = await getOperationLogList({
      username: operationSearchForm.username || undefined,
      module: operationSearchForm.module || undefined,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error('Failed to load data:', error)
  } finally {
    loading.value = false
  }
}

const loadLoginData = async () => {
  loading.value = true
  try {
    const res: any = await getLoginLogList({
      username: loginSearchForm.username || undefined,
      status: loginSearchForm.status ?? undefined,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error('Failed to load data:', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = (tab: string) => {
  pagination.pageNum = 1
  if (tab === 'operation') {
    loadOperationData()
  } else {
    loadLoginData()
  }
}

const handleOperationSearch = () => {
  pagination.pageNum = 1
  loadOperationData()
}

const handleOperationReset = () => {
  operationSearchForm.username = ''
  operationSearchForm.module = ''
  handleOperationSearch()
}

const handleLoginSearch = () => {
  pagination.pageNum = 1
  loadLoginData()
}

const handleLoginReset = () => {
  loginSearchForm.username = ''
  loginSearchForm.status = null
  handleLoginSearch()
}

const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  activeTab.value === 'operation' ? loadOperationData() : loadLoginData()
}

const handlePageChange = (val: number) => {
  pagination.pageNum = val
  activeTab.value === 'operation' ? loadOperationData() : loadLoginData()
}

onMounted(() => {
  loadOperationData()
})
</script>

<style scoped>
.log-container {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .log-container {
    padding: 12px;
  }

  .search-bar :deep(.el-input) {
    width: 100%;
  }

  .pagination {
    justify-content: center;
  }

  .el-table :deep(.cell) {
    padding: 8px 4px;
    font-size: 13px;
  }
}
</style>
