<template>
  <div class="bookmark-container">
    <div class="header-section">
      <h2>网址收藏合集</h2>
      <el-button type="primary" size="large" @click="handleAdd" v-if="hasCreatePermission">
        <el-icon><Plus /></el-icon>
        添加网址
      </el-button>
    </div>

    <div class="search-filter-section">
      <div class="search-bar">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索网址标题、描述或URL..."
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <div class="filter-options">
        <el-checkbox v-model="queryParams.mineOnly" @change="handleSearch">只看我的</el-checkbox>
        <el-select v-model="queryParams.sortBy" @change="handleSearch" style="width: 120px;">
          <el-option label="最新添加" value="createdAt" />
          <el-option label="最热门" value="clickCount" />
        </el-select>
        <el-select v-model="queryParams.sortOrder" @change="handleSearch" style="width: 100px;">
          <el-option label="降序" value="desc" />
          <el-option label="升序" value="asc" />
        </el-select>
      </div>
    </div>

    <div class="bookmark-grid" v-loading="loading">
      <template v-if="!loading && bookmarkList.length === 0">
        <div class="empty-state">
          <el-empty description="暂无网址收藏">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加第一个网址
            </el-button>
          </el-empty>
        </div>
      </template>

      <el-card
        v-for="bookmark in bookmarkList"
        :key="bookmark.id"
        class="bookmark-card"
        :class="{ 'private-card': bookmark.isPrivate === 1 }"
        shadow="hover"
      >
        <div class="card-header">
          <div class="favicon-title">
            <img
              :src="getFavicon(bookmark.url)"
              alt=""
              class="favicon"
              @error="handleImageError"
            />
            <h3 class="title">{{ bookmark.title }}</h3>
          </div>
          <el-tooltip :content="bookmark.isPrivate === 1 ? '私密' : '公开'" placement="top">
            <el-switch
              v-model="bookmark.isPrivate"
              :active-value="1"
              :inactive-value="0"
              @change="(val) => handleTogglePrivacy(bookmark.id, val)"
              size="small"
            />
          </el-tooltip>
        </div>

        <div class="url">{{ bookmark.url }}</div>

        <div class="description">{{ bookmark.description || '暂无描述' }}</div>

        <div class="meta-info">
          <span class="creator">
            <el-icon><User /></el-icon>
            {{ bookmark.createdBy }}
          </span>
          <span class="clicks">
            <el-icon><View /></el-icon>
            {{ bookmark.clickCount }}次
          </span>
          <span class="time">{{ formatTime(bookmark.createdAt) }}</span>
        </div>

        <div class="action-buttons">
          <el-button size="small" @click.stop="handleCopyUrl(bookmark.url)">
            <el-icon><CopyDocument /></el-icon>
            复制
          </el-button>
          <el-button size="small" type="primary" @click.stop="handleOpenUrl(bookmark.url, bookmark.id)">
            <el-icon><Link /></el-icon>
            打开
          </el-button>
          <el-button
            size="small"
            type="warning"
            @click.stop="handleEdit(bookmark)"
            v-if="canEdit(bookmark)"
          >
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
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
                删除
              </el-button>
            </template>
          </el-popconfirm>
        </div>
      </el-card>
    </div>

    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑网址' : '添加网址'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="URL地址" prop="url">
          <el-input v-model="formData.url" placeholder="请输入网址URL" />
        </el-form-item>
        <el-form-item label="网页标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入网页标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="简短描述为什么收藏这个网址（可选）"
          />
        </el-form-item>
        <el-form-item label="网站图标">
          <el-input v-model="formData.icon" placeholder="网站图标URL（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
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
  View,
  CopyDocument,
  Link,
  Edit,
  Delete
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
  mineOnly: false,
  isPrivate: undefined as number | undefined,
  sortBy: 'createdAt',
  sortOrder: 'desc'
})

const formData = reactive({
  id: undefined as number | undefined,
  title: '',
  url: '',
  description: '',
  icon: ''
})

const formRules = {
  url: [
    { required: true, message: '请输入URL地址', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入网页标题', trigger: 'blur' }
  ]
}

const hasCreatePermission = computed(() => {
  return userStore.permissions?.includes('bookmark-module:create')
})

const canEdit = (bookmark: Bookmark) => {
  const isAdmin = userStore.roles?.includes('SUPER_ADMIN')
  const isOwner = bookmark.createdBy === userStore.userInfo?.username
  return isAdmin || (isOwner && userStore.permissions?.includes('bookmark-module:edit'))
}

const canDelete = (bookmark: Bookmark) => {
  const isAdmin = userStore.roles?.includes('SUPER_ADMIN')
  const isOwner = bookmark.createdBy === userStore.userInfo?.username
  return isAdmin || (isOwner && userStore.permissions?.includes('bookmark-module:delete'))
}

onMounted(() => {
  if (userStore.userInfo?.username) {
    queryParams.currentUser = userStore.userInfo.username
  }
  loadBookmarkList()
})

const loadBookmarkList = async () => {
  loading.value = true
  try {
    const res: any = await bookmarkApi.getBookmarkList(queryParams)
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
  formData.icon = bookmark.icon || ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (isEdit.value && formData.id) {
        const res: any = await bookmarkApi.updateBookmark(formData.id, {
          title: formData.title,
          url: formData.url,
          description: formData.description,
          icon: formData.icon
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
          url: formData.url,
          description: formData.description,
          icon: formData.icon,
          createdBy: userStore.userInfo?.username || 'anonymous',
          isPrivate: 0,
          clickCount: 0
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
    await bookmarkApi.deleteBookmark(id)
    ElMessage.success('删除成功')
    loadBookmarkList()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

const handleTogglePrivacy = async (id: number, value: number) => {
  try {
    await bookmarkApi.togglePrivacy(id)
    ElMessage.success(value === 1 ? '已设为私密' : '已设为公开')
    loadBookmarkList()
  } catch (error) {
    console.error('切换隐私状态失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleCopyUrl = async (url: string) => {
  try {
    await navigator.clipboard.writeText(url)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    console.error('复制失败:', error)
    ElMessage.error('复制失败，请手动复制')
  }
}

const handleOpenUrl = async (url: string, id: number) => {
  window.open(url, '_blank')
  try {
    await bookmarkApi.incrementClickCount(id)
  } catch (error) {
    console.error('记录点击次数失败:', error)
  }
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

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  if (days < 365) return `${Math.floor(days / 30)}个月前`
  return `${Math.floor(days / 365)}年前`
}

const resetForm = () => {
  formData.id = undefined
  formData.title = ''
  formData.url = ''
  formData.description = ''
  formData.icon = ''
}
</script>

<style scoped>
.bookmark-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-section h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.search-filter-section {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.search-bar .el-input {
  flex: 1;
}

.filter-options {
  display: flex;
  gap: 16px;
  align-items: center;
}

.bookmark-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
  min-height: 400px;
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.bookmark-card {
  transition: all 0.3s ease;
  border-radius: 8px;
}

.bookmark-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.private-card {
  opacity: 0.85;
  border-left: 4px solid #E6A23C;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.favicon-title {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  overflow: hidden;
}

.favicon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}

.title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.url {
  color: #409EFF;
  font-size: 13px;
  margin-bottom: 10px;
  word-break: break-all;
  line-height: 1.4;
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
  align-items: center;
  padding: 12px 0;
  border-top: 1px solid #EBEEF5;
  margin-bottom: 12px;
  color: #909399;
  font-size: 13px;
}

.meta-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  flex: 1;
  min-width: 70px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .bookmark-grid {
    grid-template-columns: 1fr;
  }

  .header-section {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .filter-options {
    flex-wrap: wrap;
  }

  .search-bar {
    flex-direction: column;
  }
}
</style>
