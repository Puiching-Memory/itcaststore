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
              <div class="edit-form-wrapper">
                <el-form
                  ref="formRef"
                  :model="formData"
                  :rules="rules"
                  label-position="top"
                  class="modern-form"
                >
                  <div class="form-row">
                    <el-form-item label="用户名" class="form-item-modern">
                      <div class="input-wrapper">
                        <Icon icon="mdi:account" class="input-icon" />
                        <el-input
                          v-model="formData.username"
                          disabled
                          placeholder="用户名不可修改"
                          class="modern-input"
                        />
                      </div>
                    </el-form-item>
                  </div>

                  <div class="form-row form-row-split">
                    <el-form-item label="邮箱" prop="email" class="form-item-modern">
                      <div class="input-wrapper">
                        <Icon icon="mdi:email" class="input-icon" />
                        <el-input
                          v-model="formData.email"
                          type="email"
                          placeholder="请输入邮箱地址"
                          clearable
                          class="modern-input"
                        />
                      </div>
                    </el-form-item>

                    <el-form-item label="电话" prop="telephone" class="form-item-modern">
                      <div class="input-wrapper">
                        <Icon icon="mdi:phone" class="input-icon" />
                        <el-input
                          v-model="formData.telephone"
                          type="tel"
                          placeholder="请输入电话号码"
                          clearable
                          class="modern-input"
                        />
                      </div>
                    </el-form-item>
                  </div>

                  <div class="form-row">
                    <el-form-item label="性别" prop="gender" class="form-item-modern">
                      <div class="radio-group-wrapper">
                        <el-radio-group v-model="formData.gender" class="modern-radio-group">
                          <el-radio label="男" class="modern-radio" :class="{ 'is-selected': formData.gender === '男' }">
                            <Icon icon="mdi:gender-male" class="radio-icon" />
                            <span>男</span>
                          </el-radio>
                          <el-radio label="女" class="modern-radio" :class="{ 'is-selected': formData.gender === '女' }">
                            <Icon icon="mdi:gender-female" class="radio-icon" />
                            <span>女</span>
                          </el-radio>
                        </el-radio-group>
                      </div>
                    </el-form-item>
                  </div>

                  <div class="form-row">
                    <el-form-item label="个人简介" prop="introduce" class="form-item-modern">
                      <div class="textarea-wrapper">
                        <Icon icon="mdi:text-box-outline" class="textarea-icon" />
                        <el-input
                          v-model="formData.introduce"
                          type="textarea"
                          :rows="4"
                          placeholder="请输入个人简介（最多100字）"
                          maxlength="100"
                          show-word-limit
                          clearable
                          class="modern-textarea"
                        />
                      </div>
                    </el-form-item>
                  </div>

                  <div class="form-row">
                    <el-form-item label="角色" class="form-item-modern">
                      <div class="input-wrapper">
                        <Icon icon="mdi:account-star" class="input-icon" />
                        <el-input
                          v-model="formData.role"
                          disabled
                          placeholder="角色不可修改"
                          class="modern-input"
                        />
                      </div>
                    </el-form-item>
                  </div>
                </el-form>
              </div>
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
import { Icon } from '@iconify/vue'
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
  padding: 0;
}

.edit-form-wrapper {
  padding: 8px 0;
}

.modern-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row-split {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-item-modern {
  margin-bottom: 0;
}

.form-item-modern :deep(.el-form-item__label) {
  font-size: 13px;
  font-weight: 700;
  color: #636366;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 12px;
  padding: 0;
}

.input-wrapper,
.textarea-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon,
.textarea-icon {
  position: absolute;
  left: 20px;
  font-size: 20px;
  width: 20px;
  height: 20px;
  color: #8E8E93;
  z-index: 2;
  pointer-events: none;
}

.textarea-icon {
  top: 16px;
  align-self: flex-start;
}

/* 移除 Element Plus 外层包装器的默认样式 */
.modern-input :deep(.el-input__wrapper),
.modern-textarea :deep(.el-textarea__wrapper) {
  border-radius: 20px;
  border: none;
  box-shadow: none;
  background: transparent;
  padding: 0;
}

.modern-input :deep(.el-input__inner),
.modern-textarea :deep(.el-textarea__inner) {
  width: 100%;
  padding: 16px 20px 16px 56px;
  border-radius: 20px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  font-size: 17px;
  color: #1C1C1E;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;
}

.modern-textarea :deep(.el-textarea__inner) {
  padding-left: 56px;
  padding-top: 16px;
  min-height: 120px;
  resize: vertical;
}

.modern-input :deep(.el-input__wrapper.is-focus),
.modern-textarea :deep(.el-textarea__wrapper.is-focus) {
  box-shadow: none;
}

.modern-input :deep(.el-input__inner):focus,
.modern-textarea :deep(.el-textarea__inner):focus {
  border-color: #1C1C1E;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.06),
              0 0 0 3px rgba(28, 28, 30, 0.1);
  background: rgba(255, 255, 255, 0.95);
}

.modern-input :deep(.el-input__inner)::placeholder,
.modern-textarea :deep(.el-textarea__inner)::placeholder {
  color: #8E8E93;
}

.modern-input :deep(.el-input.is-disabled .el-input__inner),
.modern-input :deep(.el-input__inner:disabled) {
  background: rgba(0, 0, 0, 0.04);
  color: #8E8E93;
  cursor: not-allowed;
}

.radio-group-wrapper {
  display: flex;
  align-items: center;
}

.modern-radio-group {
  display: flex;
  gap: 16px;
  width: 100%;
}

.modern-radio {
  flex: 1;
  margin-right: 0;
  padding: 16px 24px;
  border-radius: 20px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.04);
}

.modern-radio:hover {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(0, 0, 0, 0.15);
}

.modern-radio :deep(.el-radio__input) {
  display: none;
}

.modern-radio :deep(.el-radio__label) {
  padding: 0;
  color: #1C1C1E;
  font-weight: 600;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.modern-radio :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #1C1C1E;
}

.modern-radio :deep(.el-radio__input.is-checked) ~ .el-radio__label {
  color: #1C1C1E;
}

.modern-radio.is-selected {
  background: rgba(28, 28, 30, 0.1);
  border-color: #1C1C1E;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.06),
              0 0 0 2px rgba(28, 28, 30, 0.1);
}

.radio-icon {
  font-size: 20px;
  width: 20px;
  height: 20px;
}

/* 清除按钮样式优化 */
.modern-input :deep(.el-input__suffix),
.modern-textarea :deep(.el-textarea__suffix) {
  right: 16px;
}

.modern-input :deep(.el-input__clear),
.modern-textarea :deep(.el-textarea__clear) {
  color: #8E8E93;
  font-size: 18px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-input :deep(.el-input__clear):hover,
.modern-textarea :deep(.el-textarea__clear):hover {
  color: #1C1C1E;
  transform: scale(1.1);
}

/* 字数统计样式 */
.modern-textarea :deep(.el-input__count) {
  background: transparent;
  color: #8E8E93;
  font-size: 13px;
  bottom: 12px;
  right: 16px;
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

  .form-row-split {
    grid-template-columns: 1fr;
  }

  .form-item-modern :deep(.el-form-item__label) {
    font-size: 12px;
  }

  .modern-input :deep(.el-input__inner),
  .modern-textarea :deep(.el-textarea__inner) {
    padding: 12px 16px 12px 48px;
    font-size: 15px;
  }

  .input-icon,
  .textarea-icon {
    left: 16px;
    font-size: 18px;
    width: 18px;
    height: 18px;
  }

  .modern-radio {
    padding: 12px 16px;
  }

  .modern-radio :deep(.el-radio__label) {
    font-size: 14px;
  }
}
</style>
