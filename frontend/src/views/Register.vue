<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-card-content">
        <div class="card-header">
          <Icon icon="mdi:account-plus" class="header-icon" />
          <h2>用户注册</h2>
        </div>
        <form @submit.prevent="handleRegister" class="register-form">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">用户名</label>
              <div class="input-wrapper">
                <Icon icon="mdi:account" class="input-icon" />
                <input
                  v-model="form.username"
                  type="text"
                  placeholder="3-20个字符"
                  class="form-input"
                  required
                  minlength="3"
                  maxlength="20"
                />
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">邮箱</label>
              <div class="input-wrapper">
                <Icon icon="mdi:email" class="input-icon" />
                <input
                  v-model="form.email"
                  type="email"
                  placeholder="请输入邮箱"
                  class="form-input"
                  required
                />
              </div>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">密码</label>
              <div class="input-wrapper">
                <Icon icon="mdi:lock" class="input-icon" />
                <input
                  v-model="form.password"
                  type="password"
                  placeholder="6-20个字符"
                  class="form-input"
                  required
                  minlength="6"
                  maxlength="20"
                />
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">确认密码</label>
              <div class="input-wrapper">
                <Icon icon="mdi:lock-check" class="input-icon" />
                <input
                  v-model="form.confirmPassword"
                  type="password"
                  placeholder="再次输入密码"
                  class="form-input"
                  required
                />
              </div>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">性别</label>
              <div class="radio-group">
                <label class="radio-label">
                  <input
                    v-model="form.gender"
                    type="radio"
                    value="男"
                    class="radio-input"
                  />
                  <Icon icon="mdi:gender-male" class="radio-icon" />
                  <span>男</span>
                </label>
                <label class="radio-label">
                  <input
                    v-model="form.gender"
                    type="radio"
                    value="女"
                    class="radio-input"
                  />
                  <Icon icon="mdi:gender-female" class="radio-icon" />
                  <span>女</span>
                </label>
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">电话</label>
              <div class="input-wrapper">
                <Icon icon="mdi:phone" class="input-icon" />
                <input
                  v-model="form.telephone"
                  type="tel"
                  placeholder="请输入电话"
                  class="form-input"
                />
              </div>
            </div>
          </div>
          <button type="submit" class="submit-btn" :disabled="loading">
            <Icon icon="mdi:account-plus" class="btn-icon" />
            <span>{{ loading ? '注册中...' : '注册' }}</span>
          </button>
          <div class="form-footer">
            <button type="button" class="link-btn" @click="$router.push('/login')">
              <Icon icon="mdi:login" class="link-icon" />
              <span>已有账号？立即登录</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  gender: '',
  telephone: ''
})

const handleRegister = async () => {
  if (!form.username || !form.password || !form.confirmPassword || !form.email) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  if (form.password !== form.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  if (form.username.length < 3 || form.username.length > 20) {
    ElMessage.error('用户名长度必须在3-20个字符之间')
    return
  }
  
  if (form.password.length < 6 || form.password.length > 20) {
    ElMessage.error('密码长度必须在6-20个字符之间')
    return
  }
  
  loading.value = true
  try {
    await userStore.register(form)
    ElMessage.success('注册成功')
    router.push('/')
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('/backgrounds/background-texture.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  padding: 20px;
  position: relative;
}

.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(2px);
  -webkit-backdrop-filter: blur(2px);
  z-index: 0;
}

.register-container > * {
  position: relative;
  z-index: 1;
}

.register-card {
  width: 100%;
  max-width: 800px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(50px);
  -webkit-backdrop-filter: blur(50px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 32px;
  box-shadow: 0 24px 48px -12px rgba(0, 0, 0, 0.15),
              0 1px 0 rgba(255, 255, 255, 0.6) inset;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.register-card-content {
  padding: 32px 40px;
  overflow: visible;
}

/* 自定义滚动条样式，使其与圆角对齐 */
.register-card-content::-webkit-scrollbar {
  width: 8px;
}

.register-card-content::-webkit-scrollbar-track {
  background: transparent;
  margin: 0;
}

.register-card-content::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: padding-box;
}

.register-card-content::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
  background-clip: padding-box;
}

/* Firefox 滚动条样式 */
.register-card-content {
  scrollbar-width: thin;
  scrollbar-color: rgba(0, 0, 0, 0.2) transparent;
}

.card-header {
  text-align: center;
  margin-bottom: 24px;
}

.card-header h2 {
  margin: 8px 0 0 0;
  font-size: 24px;
  font-weight: 800;
  color: #1C1C1E;
  letter-spacing: -0.02em;
}

.header-icon {
  font-size: 36px;
  width: 36px;
  height: 36px;
  color: #1C1C1E;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 12px;
  font-weight: 700;
  color: #636366;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  font-size: 18px;
  width: 18px;
  height: 18px;
  color: #8E8E93;
  z-index: 1;
}

.form-input {
  width: 100%;
  padding: 12px 16px 12px 48px;
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  font-size: 15px;
  color: #1C1C1E;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;
}

.form-input:focus {
  border-color: #1C1C1E;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.06),
              0 0 0 3px rgba(28, 28, 30, 0.1);
  background: rgba(255, 255, 255, 0.95);
}

.form-input::placeholder {
  color: #8E8E93;
}

.radio-group {
  display: flex;
  gap: 12px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  font-size: 14px;
  color: #1C1C1E;
  box-shadow: inset 0 1px 4px rgba(0, 0, 0, 0.04);
  flex: 1;
}

.radio-label:hover {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(0, 0, 0, 0.15);
}

.radio-input {
  display: none;
}

.radio-input:checked + .radio-icon + span,
.radio-input:checked ~ span {
  color: #1C1C1E;
}

.radio-label:has(.radio-input:checked) {
  background: rgba(28, 28, 30, 0.1);
  border-color: #1C1C1E;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.06);
}

.radio-icon {
  font-size: 16px;
  width: 16px;
  height: 16px;
}

.submit-btn {
  width: 100%;
  padding: 14px 24px;
  border-radius: 20px;
  background: #1C1C1E;
  color: white;
  border: none;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(28, 28, 30, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 4px;
}

.submit-btn:hover:not(:disabled) {
  background: #2C2C2E;
  box-shadow: 0 6px 16px rgba(28, 28, 30, 0.35);
  transform: translateY(-1px);
}

.submit-btn:active:not(:disabled) {
  transform: scale(0.98);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 18px;
  width: 18px;
  height: 18px;
}

.form-footer {
  text-align: center;
  margin-top: 4px;
}

.link-btn {
  background: none;
  border: none;
  color: #1C1C1E;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 16px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.link-btn:hover {
  background: rgba(0, 0, 0, 0.04);
}

.link-btn:active {
  transform: scale(0.96);
}

.link-icon {
  font-size: 16px;
  width: 16px;
  height: 16px;
}

@media (max-width: 768px) {
  .register-card {
    border-radius: 24px;
    max-width: 100%;
  }

  .register-card-content {
    padding: 24px 20px;
  }
  
  .card-header h2 {
    font-size: 22px;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .register-form {
    gap: 14px;
  }
}
</style>
