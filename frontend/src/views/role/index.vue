<template>
  <div class="role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="code" label="角色编码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handlePermission(row)">
              分配权限
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="permissionDialogVisible"
      title="分配权限"
      width="600px"
    >
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        :props="treeProps"
        node-key="id"
        :default-checked-keys="selectedPermissions"
        show-checkbox
        default-expand-all
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePermissionSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import {
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  getRolePermissions,
  assignRolePermissions,
  type Role
} from '@/api/role'
import { getPermissionList, type Permission } from '@/api/permission'

const loading = ref(false)
const tableData = ref<Role[]>([])
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const permissionTreeRef = ref()
const selectedPermissions = ref<number[]>([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null as number | null,
  name: '',
  code: '',
  description: ''
})

const dialogTitle = computed(() => (isEdit.value ? '编辑角色' : '新增角色'))

const formRules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const permissionTree = ref<Permission[]>([])
const currentRoleId = ref<number | null>(null)

const treeProps = {
  children: 'children',
  label: 'name'
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getRoleList({
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

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: Role) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row: Role) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handlePermission = async (row: Role) => {
  currentRoleId.value = row.id
  permissionDialogVisible.value = true
  try {
    const [roleRes, permRes]: any[] = await Promise.all([
      getRolePermissions(row.id),
      getPermissionList()
    ])
    selectedPermissions.value = roleRes.data
    permissionTree.value = permRes.data
  } catch (error) {
    ElMessage.error('加载权限数据失败')
  }
}

const handlePermissionSubmit = async () => {
  if (!currentRoleId.value) return
  const checkedKeys = permissionTreeRef.value.getCheckedKeys()
  try {
    await assignRolePermissions(currentRoleId.value, checkedKeys as number[])
    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
  } catch (error) {
    ElMessage.error('权限分配失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value && formData.id) {
          await updateRole(formData.id, {
            name: formData.name,
            code: formData.code,
            description: formData.description
          })
          ElMessage.success('更新成功')
        } else {
          await createRole({
            name: formData.name,
            code: formData.code,
            description: formData.description
          })
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      }
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(formData, { id: null, name: '', code: '', description: '' })
}

const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  loadData()
}

const handlePageChange = (val: number) => {
  pagination.pageNum = val
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.role-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .role-container {
    padding: 12px;
  }

  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .pagination {
    justify-content: center;
  }

  .el-table :deep(.cell) {
    padding: 8px 4px;
    font-size: 13px;
  }

  .el-dialog {
    width: 94% !important;
  }
}
</style>
