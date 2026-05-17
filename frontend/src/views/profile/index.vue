<template>
  <div class="profile">
    <div class="profile-header">
      <div class="avatar-section">
        <div class="avatar-wrapper">
          <el-avatar :size="100" class="avatar">
            <el-icon><UserFilled /></el-icon>
          </el-avatar>
          <div class="avatar-decoration"></div>
        </div>
        <h2 class="user-name">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</h2>
        <el-tag :type="isAdmin ? 'danger' : 'success'" size="large" class="role-tag">
          {{ isAdmin ? '超级管理员' : '普通用户' }}
        </el-tag>
      </div>
    </div>

    <div class="profile-content">
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </div>
        </template>

        <el-form
          ref="formRef"
          :model="formData"
          label-position="top"
          :disabled="!isEditing"
          class="profile-form"
        >
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="用户名">
                <el-input v-model="formData.username" disabled>
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="真实姓名">
                <el-input v-model="formData.realName">
                  <template #prefix>
                    <el-icon><Postcard /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="手机号">
                <el-input v-model="formData.phone">
                  <template #prefix>
                    <el-icon><Phone /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱">
                <el-input v-model="formData.email">
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <div class="form-actions">
            <el-button v-if="!isEditing" type="primary" @click="isEditing = true">
              <el-icon><Edit /></el-icon>
              编辑信息
            </el-button>
            <template v-else>
              <el-button type="primary" @click="handleSave">
                <el-icon><Check /></el-icon>
                保存
              </el-button>
              <el-button @click="handleCancel">
                <el-icon><Close /></el-icon>
                取消
              </el-button>
            </template>
          </div>
        </el-form>
      </el-card>

      <el-card class="roles-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><UserFilled /></el-icon>
            <span>角色权限</span>
          </div>
        </template>

        <div class="roles-section">
          <h4>我的角色</h4>
          <div class="role-tags">
            <el-tag
              v-for="role in userStore.userInfo?.roles"
              :key="role"
              :type="getRoleType(role)"
              size="large"
              class="role-tag-item"
            >
              {{ getRoleName(role) }}
            </el-tag>
          </div>
        </div>

        <el-divider />

        <div class="permissions-section">
          <h4>拥有权限</h4>
          <template v-if="isAdmin">
            <div class="permission-tags">
              <el-tag
                v-for="permission in userStore.userInfo?.permissions"
                :key="permission"
                size="small"
                class="permission-tag"
              >
                {{ getPermissionLabel(permission) }}
              </el-tag>
            </div>
          </template>
          <template v-else>
            <div class="permission-summary">
              <el-icon><CircleCheck /></el-icon>
              <span>您拥有 {{ userStore.userInfo?.permissions?.length ?? 0 }} 项功能访问权限</span>
            </div>
            <el-collapse v-if="userStore.userInfo?.permissions?.length" class="permission-collapse">
              <el-collapse-item title="查看详细信息">
                <div class="permission-tags">
                  <el-tag
                    v-for="permission in userStore.userInfo?.permissions"
                    :key="permission"
                    size="small"
                    class="permission-tag"
                  >
                    {{ getPermissionLabel(permission) }}
                  </el-tag>
                </div>
              </el-collapse-item>
            </el-collapse>
          </template>
        </div>
      </el-card>

      <el-card class="security-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><Lock /></el-icon>
            <span>安全设置</span>
          </div>
        </template>

        <div class="security-item">
          <div class="security-info">
            <h4>登录密码</h4>
            <p>定期更换密码可以提高账户安全性</p>
          </div>
          <el-button type="primary" plain @click="showPasswordDialog = true">
            修改密码
          </el-button>
        </div>
      </el-card>
    </div>

    <el-dialog
      v-model="showPasswordDialog"
      title="修改密码"
      width="420px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="pwdFormRef"
        :model="pwdForm"
        :rules="pwdRules"
        label-position="top"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="pwdForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入当前密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="pwdForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码（至少6位）"
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="pwdForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElLoading } from 'element-plus'
import { changePassword, type PasswordChangeForm } from '@/api/auth'
import request from '@/utils/request'

const userStore = useUserStore()
const formRef = ref()
const isEditing = ref(false)
const showPasswordDialog = ref(false)
const pwdFormRef = ref()

const formData = reactive({
  username: '',
  realName: '',
  phone: '',
  email: ''
})

const pwdForm = reactive<PasswordChangeForm & { confirmPassword: string }>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: Function) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const isAdmin = computed(() => {
  return userStore.userInfo?.roles?.includes('SUPER_ADMIN')
})

onMounted(() => {
  Object.assign(formData, {
    username: userStore.userInfo?.username || '',
    realName: userStore.userInfo?.realName || '',
    phone: userStore.userInfo?.phone || '',
    email: userStore.userInfo?.email || ''
  })
})

const handleSave = async () => {
  const loading = ElLoading.service({ lock: true, text: '保存中...' })
  try {
    await request.put(`/v1/users/${userStore.userInfo?.id}`, {
      realName: formData.realName,
      phone: formData.phone,
      email: formData.email
    })

    if (userStore.userInfo) {
      userStore.userInfo.realName = formData.realName
      userStore.userInfo.phone = formData.phone
      userStore.userInfo.email = formData.email
    }

    ElMessage.success('个人信息更新成功')
    isEditing.value = false
  } catch (error) {
    ElMessage.error('保存失败，请重试')
  } finally {
    loading.close()
  }
}

const handleCancel = () => {
  Object.assign(formData, {
    username: userStore.userInfo?.username || '',
    realName: userStore.userInfo?.realName || '',
    phone: userStore.userInfo?.phone || '',
    email: userStore.userInfo?.email || ''
  })
  isEditing.value = false
}

const handleChangePassword = async () => {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return

  const loading = ElLoading.service({ lock: true, text: '修改中...' })
  try {
    await changePassword(pwdForm)
    ElMessage.success('密码修改成功，请重新登录')
    showPasswordDialog.value = false
    setTimeout(() => {
      userStore.userLogout()
    }, 1500)
  } catch (error) {
    ElMessage.error('密码修改失败，请检查原密码是否正确')
  } finally {
    loading.close()
  }
}

const getRoleName = (role: string) => {
  const roleMap: { [key: string]: string } = {
    'SUPER_ADMIN': '超级管理员',
    'NORMAL_USER': '普通用户'
  }
  return roleMap[role] || role
}

const getRoleType = (role: string) => {
  return role === 'SUPER_ADMIN' ? 'danger' : 'success'
}

const permissionLabels: { [key: string]: string } = {
  'user-menu': '用户管理菜单',
  'role-menu': '角色管理菜单',
  'module-menu': '模块管理菜单',
  'config-menu': '系统配置菜单',
  'log-menu': '日志管理菜单',
  'user:view': '查看用户',
  'user:create': '创建用户',
  'user:edit': '编辑用户',
  'user:delete': '删除用户',
  'user:assign-roles': '分配用户角色',
  'role:view': '查看角色',
  'role:create': '创建角色',
  'role:edit': '编辑角色',
  'role:delete': '删除角色',
  'role:assign-permissions': '分配角色权限',
  'module:view': '查看模块',
  'module:install': '安装模块',
  'module:uninstall': '卸载模块',
  'module:enable': '启用模块',
  'module:disable': '停用模块',
  'config:view': '查看配置',
  'config:edit': '编辑配置',
  'log:view': '查看日志',
  'log:export': '导出日志',
  'log:delete': '删除日志',
  'admin-access': '后台访问权限',
  'example-module:view': '查看示例模块',
  'example-module:list': '查看示例列表',
  'example-module:create': '创建示例',
  'example-module:edit': '编辑示例',
  'example-module:delete': '删除示例'
}

const getPermissionLabel = (code: string): string => {
  return permissionLabels[code] || code
}
</script>

<style scoped>
.profile {
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.profile-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  padding: 48px;
  margin-bottom: 32px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.3);
}

.avatar-section {
  position: relative;
  display: inline-block;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.avatar {
  background: white;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease;
}

.avatar-wrapper:hover .avatar {
  transform: scale(1.05);
}

.avatar-decoration {
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.5;
  }
}

.user-name {
  margin: 0 0 12px;
  font-size: 28px;
  font-weight: 700;
  color: white;
}

.role-tag {
  font-size: 14px;
}

.profile-content {
  display: grid;
  gap: 24px;
}

.info-card,
.roles-card {
  border-radius: 20px;
  border: none;
  transition: all 0.3s ease;
}

.info-card:hover,
.roles-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.profile-form {
  padding: 20px 0;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.roles-section,
.permissions-section {
  margin-bottom: 20px;
}

.roles-section h4,
.permissions-section h4 {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.role-tags {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.role-tag-item {
  font-size: 14px;
  padding: 8px 16px;
}

.permission-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.permission-tag {
  font-size: 12px;
}

.permission-summary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.08), rgba(82, 196, 26, 0.04));
  border-radius: 12px;
  color: #67c23a;
  font-size: 14px;
}

.permission-summary .el-icon {
  font-size: 20px;
}

.permission-collapse {
  margin-top: 12px;
  border: none;
}

.permission-collapse :deep(.el-collapse-item__header) {
  font-size: 13px;
  color: #909399;
  border-bottom: none;
}

.permission-collapse :deep(.el-collapse-item__wrap) {
  border-bottom: none;
}

.permission-collapse :deep(.el-collapse-item__content) {
  padding-top: 12px;
}

.security-card {
  border-radius: 20px;
  border: none;
  transition: all 0.3s ease;
}

.security-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
}

.security-info h4 {
  margin: 0 0 6px 0;
  font-size: 15px;
  color: #303133;
}

.security-info p {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

@media (max-width: 768px) {
  .profile-header {
    padding: 24px 16px;
  }

  .user-name {
    font-size: 20px;
  }

  .info-card,
  .roles-card,
  .security-card {
    border-radius: 16px;
  }

  .el-col-12 {
    max-width: 100% !important;
    flex: 0 0 100% !important;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions .el-button {
    width: 100%;
  }

  .security-item {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .el-dialog {
    width: 92% !important;
    margin: 0 auto !important;
  }
}
</style>
