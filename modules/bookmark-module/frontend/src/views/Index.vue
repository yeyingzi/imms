<template>
  <div class="bookmark-container">
    <div class="header-section">
      <div class="header-content">
        <div class="header-text">
          <h2>🌐 网址收藏</h2>
          <p class="subtitle">整理您的网络资源，随时随地访问</p>
        </div>
        <el-button type="primary" size="large" @click="handleAdd" v-if="hasCreatePermission" class="add-btn">
          <el-icon><Plus /></el-icon>
          添加网址
        </el-button>
      </div>
    </div>

    <div class="search-filter-section">
      <div class="search-bar">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索网址..."
          clearable
          size="large"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleSearch" class="search-btn">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
        
        <el-checkbox v-model="queryParams.mineOnly" @change="handleSearch" class="mine-checkbox">
          <el-icon><User /></el-icon>
          <span>只看我的</span>
        </el-checkbox>
      </div>
    </div>

    <div class="bookmark-content" v-loading="loading">
      <template v-if="!loading && bookmarkList.length === 0">
        <div class="empty-state">
          <div class="empty-illustration">
            <svg viewBox="0 0 120 120" class="empty-svg">
              <defs>
                <linearGradient id="emptyGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#667eea"/>
                  <stop offset="100%" stop-color="#764ba2"/>
                </linearGradient>
              </defs>
              <circle cx="60" cy="60" r="50" fill="url(#emptyGrad)" opacity="0.1"/>
              <path d="M40 50 L60 30 L80 50 M60 30 L60 90" stroke="#667eea" stroke-width="3" fill="none" stroke-linecap="round"/>
              <circle cx="60" cy="30" r="8" fill="#667eea"/>
            </svg>
          </div>
          <h3>暂无收藏</h3>
          <p>开始添加您的第一个网址收藏吧</p>
          <el-button type="primary" size="large" @click="handleAdd" v-if="hasCreatePermission">
            <el-icon><Plus /></el-icon>
            添加第一个网址
          </el-button>
        </div>
      </template>

      <div class="bookmark-grid" v-else>
        <transition-group name="card">
          <div
            v-for="(bookmark, index) in bookmarkList"
            :key="bookmark.id"
            class="bookmark-card"
            :class="{ 'private-card': bookmark.isPrivate === 1 }"
            :style="{ '--delay': index * 0.05 + 's' }"
          >
            <div class="card-inner">
              <div class="card-header">
                <div class="favicon-title">
                  <img
                    :src="getFavicon(bookmark.url)"
                    alt=""
                    class="favicon"
                    @error="handleImageError"
                  />
                  <h3 class="title" @click="handleOpenUrl(bookmark.url, bookmark.id)">
                    {{ bookmark.title }}
                  </h3>
                </div>
                <el-tooltip :content="bookmark.isPrivate === 1 ? '🔒 私密' : '🌍 公开'" placement="top" v-if="canTogglePrivacy(bookmark)">
                  <el-switch
                    v-model="bookmark.isPrivate"
                    :active-value="1"
                    :inactive-value="0"
                    @change="(val) => handleTogglePrivacy(bookmark.id, val)"
                    size="small"
                  />
                </el-tooltip>
              </div>

              <a :href="bookmark.url" target="_blank" class="url-link" @click.prevent="handleOpenUrl(bookmark.url, bookmark.id)">
                <el-icon><Link /></el-icon>
                {{ formatUrl(bookmark.url) }}
              </a>

              <div class="description" v-if="bookmark.description">
                {{ bookmark.description }}
              </div>

              <div class="meta-info">
                <span class="creator" v-if="bookmark.createdBy !== userStore.userInfo?.username">
                  <el-icon><User /></el-icon>
                  {{ bookmark.createdBy }}
                </span>
                <span class="private-badge" v-if="bookmark.isPrivate === 1">
                  <el-icon><Lock /></el-icon>
                  私密
                </span>
                <span class="time">
                  <el-icon><Clock /></el-icon>
                  {{ formatTime(bookmark.createdAt) }}
                </span>
              </div>

              <div class="action-buttons">
                <el-tooltip content="复制链接" placement="top">
                  <el-button size="small" @click.stop="handleCopyUrl(bookmark.url)">
                    <el-icon><CopyDocument /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="打开网址" placement="top">
                  <el-button size="small" type="primary" @click.stop="handleOpenUrl(bookmark.url, bookmark.id)">
                    <el-icon><TopRight /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="编辑" placement="top" v-if="canEdit(bookmark)">
                  <el-button size="small" type="warning" @click.stop="handleEdit(bookmark)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-popconfirm
                  title="确定删除该网址吗？"
                  confirm-button-text="确定"
                  cancel-button-text="取消"
                  @confirm="handleDelete(bookmark.id)"
                  v-if="canDelete(bookmark)"
                >
                  <template #reference>
                    <el-button size="small" type="danger" @click.stop>
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-popconfirm>
              </div>
            </div>
          </div>
        </transition-group>
      </div>

      <div class="pagination-wrapper" v-if="total > 0 && bookmarkList.length > 0">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
          class="custom-pagination"
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑网址' : '添加网址'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="URL" prop="url">
          <el-input v-model="formData.url" placeholder="请输入网址" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  User,
  CopyDocument,
  Link,
  Edit,
  Delete,
  Clock,
  TopRight,
  Lock
} from '@element-plus/icons-vue'
import { bookmarkApi, type Bookmark } from '../api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const bookmarkList = ref<Bookmark[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  pageSize: 12,
  keyword: '',
  currentUser: '',
  mineOnly: false
})

const formData = reactive({
  id: undefined as number | undefined,
  title: '',
  url: '',
  description: ''
})

const formRules = {
  url: [
    { required: true, message: '请输入URL地址', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入网页标题', trigger: 'blur' }
  ]
}

const isOwner = (createdBy: string) => {
  return createdBy === userStore.userInfo?.username
}

const hasCreatePermission = computed(() => {
  return true
})

const canEdit = (bookmark: Bookmark) => {
  return isOwner(bookmark.createdBy || '')
}

const canDelete = (bookmark: Bookmark) => {
  return isOwner(bookmark.createdBy || '')
}

const canTogglePrivacy = (bookmark: Bookmark) => {
  return isOwner(bookmark.createdBy || '')
}

onMounted(() => {
  loadBookmarkList()
})

const loadBookmarkList = async () => {
  loading.value = true
  try {
    if (!queryParams.currentUser && userStore.userInfo?.username) {
      queryParams.currentUser = userStore.userInfo.username
    }

    const params: any = {
      page: queryParams.page,
      pageSize: queryParams.pageSize
    }

    if (queryParams.keyword) {
      params.keyword = queryParams.keyword
    }
    if (queryParams.currentUser) {
      params.currentUser = queryParams.currentUser
    }
    if (queryParams.mineOnly) {
      params.mineOnly = true
    }

    const res: any = await bookmarkApi.getBookmarkList(params)
    if (res.code === 200) {
      bookmarkList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载网址列表失败:', error)
    ElMessage.error('加载网址列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  loadBookmarkList()
}

const handlePageChange = (page: number) => {
  queryParams.page = page
  loadBookmarkList()
}

const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  queryParams.page = 1
  loadBookmarkList()
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (bookmark: Bookmark) => {
  isEdit.value = true
  formData.id = bookmark.id
  formData.title = bookmark.title
  formData.url = bookmark.url
  formData.description = bookmark.description || ''
  dialogVisible.value = true
}

const resetForm = () => {
  formData.id = undefined
  formData.title = ''
  formData.url = ''
  formData.description = ''
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    let finalUrl = formData.url.trim()
    if (!finalUrl.startsWith('http://') && !finalUrl.startsWith('https://')) {
      finalUrl = 'https://' + finalUrl
    }

    submitLoading.value = true
    try {
      const currentUser = userStore.userInfo?.username || 'anonymous'
      if (isEdit.value && formData.id) {
        const res: any = await bookmarkApi.updateBookmark(formData.id, currentUser, {
          title: formData.title,
          url: finalUrl,
          description: formData.description
        })
        if (res.code === 200) {
          ElMessage.success('更新成功')
        } else {
          ElMessage.error(res.message || '更新失败')
          return
        }
      } else {
        const res: any = await bookmarkApi.createBookmark({
          title: formData.title,
          url: finalUrl,
          description: formData.description,
          createdBy: currentUser,
          isPrivate: 0
        })
        if (res.code === 200) {
          ElMessage.success('创建成功')
        } else if (res.code === 409) {
          ElMessage.warning(res.message || '该网址已被收藏')
          return
        } else {
          ElMessage.error(res.message || '创建失败')
          return
        }
      }
      dialogVisible.value = false
      loadBookmarkList()
    } catch (error: any) {
      console.error('保存失败:', error)
      const errorMsg = error?.response?.data?.message || error?.message || (isEdit.value ? '更新失败' : '创建失败')
      ElMessage.error(errorMsg)
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = async (id: number) => {
  try {
    const currentUser = userStore.userInfo?.username || 'anonymous'
    await bookmarkApi.deleteBookmark(id, currentUser)
    ElMessage.success('🗑️ 删除成功')
    loadBookmarkList()
  } catch (error: any) {
    console.error('删除失败:', error)
    const errorMsg = error?.response?.data?.message || '删除失败'
    ElMessage.error(errorMsg)
  }
}

const handleTogglePrivacy = async (id: number, value: number) => {
  try {
    const currentUser = userStore.userInfo?.username || 'anonymous'
    await bookmarkApi.togglePrivacy(id, currentUser)
    ElMessage.success(value === 1 ? '🔒 已设为私密' : '🌍 已设为公开')
    loadBookmarkList()
  } catch (error: any) {
    console.error('切换隐私状态失败:', error)
    const errorMsg = error?.response?.data?.message || '操作失败'
    ElMessage.error(errorMsg)
  }
}

const handleCopyUrl = async (url: string) => {
  try {
    await navigator.clipboard.writeText(url)
    ElMessage.success('📋 已复制到剪贴板')
  } catch (error) {
    console.error('复制失败:', error)
    ElMessage.error('复制失败，请手动复制')
  }
}

const handleOpenUrl = async (url: string, id: number) => {
  let finalUrl = url
  if (!url.startsWith('http://') && !url.startsWith('https://')) {
    finalUrl = 'https://' + url
  }
  window.open(finalUrl, '_blank')
}

const getFavicon = (url: string) => {
  try {
    const domain = new URL(url).hostname
    return `https://www.google.com/s2/favicons?domain=${domain}&sz=32`
  } catch {
    return ''
  }
}

const handleImageError = (e: Event) => {
  const img = e.target as HTMLImageElement
  img.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzIiIGhlaWdodD0iMzIiIHZpZXdCb3g9IjAgMCAzMiAzMiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjMyIiBoZWlnaHQ9IjMyIiByeD0iNCIgZmlsbD0iI0Y1RjZGQSIvPgo8cGF0aCBkPSJNMTYgMTJMMTIgMjBIMjBMMTYgMTJaIiBmaWxsPSIjOUI5QUEwIi8+Cjwvc3ZnPgo='
}

const formatUrl = (url: string) => {
  try {
    const parsed = new URL(url)
    return parsed.hostname + (parsed.pathname !== '/' ? parsed.pathname : '')
  } catch {
    return url
  }
}

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days < 0) return '刚刚'
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  if (days < 365) return `${Math.floor(days / 30)}个月前`
  return `${Math.floor(days / 365)}年前`
}
</script>

<style scoped>
.bookmark-container {
  padding: 0;
  min-height: calc(100vh - 140px);
}

.header-section {
  margin-bottom: 32px;
  padding: 32px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-text h2 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 16px;
  opacity: 0.9;
}

.add-btn {
  padding: 12px 24px;
  border-radius: 10px;
}

.search-filter-section {
  margin-bottom: 32px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  font-size: 15px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 8px 12px;
}

.search-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
}

.mine-checkbox {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.mine-checkbox:hover {
  background: #e4e7ed;
}

.mine-checkbox :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #667eea;
}

@media (max-width: 768px) {
  .search-bar {
    padding: 12px;
    gap: 10px;
  }
  
  .search-input {
    width: 100%;
  }
  
  .search-bar :deep(.el-input__wrapper) {
    padding: 12px 16px;
  }
  
  .mine-checkbox {
    width: 100%;
    justify-content: center;
  }
}

.bookmark-content {
  min-height: 400px;
}

.bookmark-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
  padding: 0;
}

.bookmark-card {
  opacity: 0;
  animation: cardFadeIn 0.5s ease forwards;
  animation-delay: var(--delay);
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-inner {
  background: white;
  border-radius: 16px;
  padding: 24px;
  height: 100%;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #f0f0f0;
}

.card-inner:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

.private-card .card-inner {
  border-left: 4px solid #f56c6c;
  background: linear-gradient(to right, #fef0f0, white);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.favicon-title {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.favicon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  flex-shrink: 0;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  cursor: pointer;
  transition: color 0.3s ease;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.title:hover {
  color: #667eea;
}

.url-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 12px;
  text-decoration: none;
  transition: color 0.3s ease;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.url-link:hover {
  color: #667eea;
}

.description {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 44px;
}

.meta-info {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #909399;
  flex-wrap: wrap;
}

.meta-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.private-badge {
  color: #f56c6c;
  font-weight: 500;
}

.creator {
  color: #409eff;
}

.action-buttons {
  display: flex;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.action-buttons .el-button {
  flex: 1;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-buttons .el-button:hover {
  transform: scale(1.05);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-illustration {
  margin-bottom: 32px;
}

.empty-svg {
  width: 160px;
  height: 160px;
  opacity: 0.8;
}

.empty-state h3 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 12px;
}

.empty-state p {
  color: #909399;
  font-size: 16px;
  margin-bottom: 32px;
}

.pagination-wrapper {
  margin-top: 48px;
  display: flex;
  justify-content: center;
}

.custom-pagination {
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.card-enter-active,
.card-leave-active {
  transition: all 0.5s ease;
}

.card-enter-from,
.card-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

@media (max-width: 768px) {
  .header-section {
    padding: 24px 20px;
  }

  .header-text h2 {
    font-size: 24px;
  }

  .bookmark-grid {
    grid-template-columns: 1fr;
  }

  .bookmark-card {
    animation-delay: 0s;
  }
}
</style>
