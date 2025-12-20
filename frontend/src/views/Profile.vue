<template>
  <div class="profile-page">
    <Header />
    <el-container>
      <el-main>
        <div class="profile-container">
          <div class="profile-header">
            <h2>个人中心</h2>
            <el-button
              v-if="!isEditing"
              type="primary"
              @click="startEdit"
              :icon="Edit"
            >
              编辑信息
            </el-button>
            <div v-else class="edit-actions">
              <el-button @click="cancelEdit" :disabled="loading">
                取消
              </el-button>
              <el-button
                type="primary"
                @click="handleSave"
                :loading="loading"
                :icon="Check"
              >
                保存
              </el-button>
            </div>
          </div>

          <el-card v-if="userStore.user" class="profile-card">
            <div v-if="!isEditing" class="view-mode">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="用户名">
                  <span class="field-value">{{ userStore.user.username }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="邮箱">
                  <span class="field-value">{{ userStore.user.email || '未设置' }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="性别">
                  <span class="field-value">{{ userStore.user.gender || '未设置' }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="电话">
                  <span class="field-value">{{ userStore.user.telephone || '未设置' }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="角色">
                  <el-tag type="info">{{ userStore.user.role }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item
                  v-if="userStore.user.introduce"
                  label="个人简介"
                  :span="2"
                >
                  <span class="field-value">{{ userStore.user.introduce }}</span>
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <div v-else class="edit-mode">
              <el-form
                ref="formRef"
                :model="formData"
                :rules="rules"
                label-width="100px"
                label-position="left"
              >
                <el-form-item label="用户名">
                  <el-input
                    v-model="formData.username"
                    disabled
                    placeholder="用户名不可修改"
                  />
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                  <el-input
                    v-model="formData.email"
                    type="email"
                    placeholder="请输入邮箱"
                    clearable
                  />
                </el-form-item>

                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="formData.gender">
                    <el-radio label="男">男</el-radio>
                    <el-radio label="女">女</el-radio>
                  </el-radio-group>
                </el-form-item>

                <el-form-item label="电话" prop="telephone">
                  <el-input
                    v-model="formData.telephone"
                    type="tel"
                    placeholder="请输入电话号码"
                    clearable
                  />
                </el-form-item>

                <el-form-item label="个人简介" prop="introduce">
                  <el-input
                    v-model="formData.introduce"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入个人简介（最多100字）"
                    maxlength="100"
                    show-word-limit
                    clearable
                  />
                </el-form-item>

                <el-form-item label="角色">
                  <el-input
                    v-model="formData.role"
                    disabled
                    placeholder="角色不可修改"
                  />
                </el-form-item>
              </el-form>
            </div>
          </el-card>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Edit, Check } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isEditing = ref(false)
const loading = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive({
  username: '',
  email: '',
  gender: '',
  telephone: '',
  introduce: '',
  role: ''
})

const rules: FormRules = {
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  telephone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: ['blur', 'change']
    }
  ],
  introduce: [
    {
      max: 100,
      message: '个人简介不能超过100个字符',
      trigger: ['blur', 'change']
    }
  ]
}

const initFormData = () => {
  if (userStore.user) {
    formData.username = userStore.user.username || ''
    formData.email = userStore.user.email || ''
    formData.gender = userStore.user.gender || ''
    formData.telephone = userStore.user.telephone || ''
    formData.introduce = userStore.user.introduce || ''
    formData.role = userStore.user.role || ''
  }
}

const startEdit = () => {
  initFormData()
  isEditing.value = true
}

const cancelEdit = () => {
  isEditing.value = false
  formRef.value?.resetFields()
}

const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    const updateData: any = {}
    if (formData.email !== userStore.user?.email) {
      updateData.email = formData.email
    }
    if (formData.gender !== userStore.user?.gender) {
      updateData.gender = formData.gender
    }
    if (formData.telephone !== userStore.user?.telephone) {
      updateData.telephone = formData.telephone
    }
    if (formData.introduce !== userStore.user?.introduce) {
      updateData.introduce = formData.introduce
    }

    if (Object.keys(updateData).length === 0) {
      ElMessage.info('没有需要保存的更改')
      isEditing.value = false
      return
    }

    await userStore.updateUserInfo(updateData)
    ElMessage.success('信息更新成功')
    isEditing.value = false
  } catch (error: any) {
    console.error('保存失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else if (typeof error === 'string') {
      ElMessage.error(error)
    } else {
      ElMessage.error('保存失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (!userStore.user) {
    try {
      await userStore.fetchUserInfo()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--ios-gray-100, #F2F2F7);
}

.profile-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 32px;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.profile-header h2 {
  margin: 0;
  font-size: 32px;
  font-weight: 800;
  color: var(--graphite, #1C1C1E);
  letter-spacing: -0.02em;
}

.edit-actions {
  display: flex;
  gap: 12px;
}

.profile-card {
  background: var(--frosted-bg-medium, rgba(255, 255, 255, 0.7));
  backdrop-filter: blur(var(--blur-lg, 50px));
  -webkit-backdrop-filter: blur(var(--blur-lg, 50px));
  border: 1px solid var(--frosted-border, rgba(255, 255, 255, 0.6));
  border-radius: var(--radius-2xl, 32px);
  box-shadow: var(--shadow-xl, 0 24px 48px -12px rgba(0, 0, 0, 0.08));
}

.view-mode {
  padding: 8px 0;
}

.field-value {
  color: var(--graphite, #1C1C1E);
  font-weight: 500;
}

.edit-mode {
  padding: 8px 0;
}

.edit-mode :deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--graphite, #1C1C1E);
}

.edit-mode :deep(.el-input__inner),
.edit-mode :deep(.el-textarea__inner) {
  border-radius: var(--radius-md, 20px);
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(var(--blur-sm, 20px));
  -webkit-backdrop-filter: blur(var(--blur-sm, 20px));
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.edit-mode :deep(.el-input__inner):focus,
.edit-mode :deep(.el-textarea__inner):focus {
  border-color: var(--graphite, #1C1C1E);
  box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.1);
  background: rgba(255, 255, 255, 0.95);
}

.edit-mode :deep(.el-radio-group) {
  display: flex;
  gap: 24px;
}

.edit-mode :deep(.el-radio) {
  margin-right: 0;
}

.edit-mode :deep(.el-radio__input.is-checked .el-radio__inner) {
  background-color: var(--graphite, #1C1C1E);
  border-color: var(--graphite, #1C1C1E);
}

.edit-mode :deep(.el-radio__label) {
  color: var(--graphite, #1C1C1E);
  font-weight: 500;
}

:deep(.el-main) {
  padding: 32px 0;
  background: transparent;
}

@media (max-width: 768px) {
  .profile-container {
    padding: 16px;
  }

  .profile-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .profile-header h2 {
    font-size: 24px;
  }

  .edit-mode :deep(.el-form-item__label) {
    width: 80px !important;
  }
}
</style>
