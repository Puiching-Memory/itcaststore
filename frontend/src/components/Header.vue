<template>
  <div class="header">
    <div class="header-content">
      <div class="logo" @click="$router.push('/')">
        <img src="/logo.png" alt="ItCast Store" class="logo-image" />
      </div>
      <nav class="menu">
        <div 
          v-for="item in menuItems" 
          :key="item.index"
          class="menu-item"
          :class="{ active: activeIndex === item.index }"
          @click="handleSelect(item.index)"
        >
          <Icon :icon="item.icon" class="menu-icon" />
          <span>{{ item.label }}</span>
        </div>
      </nav>
      <div class="user-actions">
        <template v-if="userStore.isAuthenticated">
          <div class="user-dropdown">
            <div class="user-name" @click="showDropdown = !showDropdown">
              <Icon icon="mdi:account-circle" class="user-icon" />
              <span>{{ userStore.user?.username }}</span>
              <Icon icon="mdi:chevron-down" class="chevron-icon" :class="{ open: showDropdown }" />
            </div>
            <transition name="dropdown">
              <div v-if="showDropdown" class="dropdown-menu">
                <div class="dropdown-item" @click="$router.push('/profile'); showDropdown = false">
                  <Icon icon="mdi:account" class="dropdown-icon" />
                  <span>个人中心</span>
                </div>
                <div 
                  v-if="userStore.user?.role === '超级用户' || userStore.user?.role === '管理员'"
                  class="dropdown-item" 
                  @click="$router.push('/notices/manage'); showDropdown = false"
                >
                  <Icon icon="mdi:bullhorn" class="dropdown-icon" />
                  <span>公告管理</span>
                </div>
                <div 
                  v-if="userStore.user?.role === '超级用户' || userStore.user?.role === '管理员'"
                  class="dropdown-item" 
                  @click="$router.push('/products/manage'); showDropdown = false"
                >
                  <Icon icon="mdi:store" class="dropdown-icon" />
                  <span>商品管理</span>
                </div>
                <div 
                  v-if="userStore.user?.role === '超级用户' || userStore.user?.role === '管理员'"
                  class="dropdown-item" 
                  @click="$router.push('/orders/manage'); showDropdown = false"
                >
                  <Icon icon="mdi:receipt" class="dropdown-icon" />
                  <span>订单管理</span>
                </div>
                <div class="dropdown-item" @click="handleLogout">
                  <Icon icon="mdi:logout" class="dropdown-icon" />
                  <span>退出登录</span>
                </div>
              </div>
            </transition>
          </div>
        </template>
        <template v-else>
          <button class="btn-secondary" @click="$router.push('/login')">
            <Icon icon="mdi:login" class="btn-icon" />
            登录
          </button>
          <button class="btn-primary" @click="$router.push('/register')">
            <Icon icon="mdi:account-plus" class="btn-icon" />
            注册
          </button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const showDropdown = ref(false)

const menuItems = [
  { index: 'home', label: '首页', icon: 'mdi:home' },
  { index: 'products', label: '图书', icon: 'mdi:book-open-variant' },
  ...(userStore.isAuthenticated ? [
    { index: 'cart', label: '购物车', icon: 'mdi:cart' },
    { index: 'orders', label: '我的订单', icon: 'mdi:file-document-multiple' }
  ] : [])
]

const activeIndex = computed(() => {
  const name = route.name as string
  return name || 'home'
})

const handleSelect = (key: string) => {
  router.push({ name: key })
}

const handleLogout = () => {
  userStore.logout()
  router.push('/')
  showDropdown.value = false
}

// 点击外部关闭下拉菜单
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (!target.closest('.user-dropdown')) {
    showDropdown.value = false
  }
}

onMounted(async () => {
  document.addEventListener('click', handleClickOutside)
  
  // 如果已认证但没有用户信息，自动获取用户信息
  if (userStore.isAuthenticated && !userStore.user) {
    try {
      await userStore.fetchUserInfo()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.header {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(50px);
  -webkit-backdrop-filter: blur(50px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 1000; /* 提高 z-index 确保下拉菜单不会被其他固定元素遮挡 */
  box-shadow: 0 1px 0 rgba(255, 255, 255, 0.6) inset,
              0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 72px;
}

.logo {
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo:active {
  transform: scale(0.98);
}

.logo-image {
  height: 48px;
  width: auto;
  max-width: 180px;
  object-fit: contain;
}

.menu {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 48px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  font-size: 15px;
  color: #636366;
  position: relative;
}

.menu-item:hover {
  background: rgba(0, 0, 0, 0.03);
  color: #1C1C1E;
}

.menu-item.active {
  color: #1C1C1E;
  font-weight: 600;
  background: rgba(0, 0, 0, 0.06);
}

.menu-item:active {
  transform: scale(0.96);
}

.menu-icon {
  font-size: 18px;
  width: 18px;
  height: 18px;
}

.user-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.user-dropdown {
  position: relative;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 24px;
  cursor: pointer;
  background: rgba(0, 0, 0, 0.04);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  font-size: 15px;
  color: #1C1C1E;
}

.user-name:hover {
  background: rgba(0, 0, 0, 0.06);
}

.user-name:active {
  transform: scale(0.96);
}

.user-icon {
  font-size: 20px;
  width: 20px;
  height: 20px;
}

.chevron-icon {
  font-size: 16px;
  width: 16px;
  height: 16px;
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.chevron-icon.open {
  transform: rotate(180deg);
}

.user-dropdown .dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 180px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 24px;
  padding: 8px;
  box-shadow: 0 16px 32px -8px rgba(0, 0, 0, 0.1),
              0 1px 0 rgba(255, 255, 255, 0.8) inset;
  z-index: 1000;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  font-size: 15px;
  color: #1C1C1E;
}

.dropdown-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.dropdown-item:active {
  transform: scale(0.98);
}

.dropdown-icon {
  font-size: 18px;
  width: 18px;
  height: 18px;
}

/* 按钮样式 */
.btn-primary,
.btn-secondary {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 24px;
  border-radius: 24px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  outline: none;
}

.btn-primary {
  background: #1C1C1E;
  color: white;
  box-shadow: 0 4px 12px rgba(28, 28, 30, 0.2);
}

.btn-primary:hover {
  background: #2C2C2E;
  box-shadow: 0 5px 14px rgba(28, 28, 30, 0.22);
  transform: translateY(-0.5px);
}

.btn-primary:active {
  transform: scale(0.96) translateY(0);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.8);
  color: #1C1C1E;
  border: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.btn-secondary:hover {
  background: white;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
  transform: translateY(-0.5px);
}

.btn-secondary:active {
  transform: scale(0.96) translateY(0);
}

.btn-icon {
  font-size: 16px;
  width: 16px;
  height: 16px;
}

/* 下拉动画 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: top right;
}

.dropdown-enter-from {
  opacity: 0;
  transform: scale(0.95) translateY(-8px);
}

.dropdown-leave-to {
  opacity: 0;
  transform: scale(0.95) translateY(-8px);
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 20px;
    height: 64px;
  }
  
  .menu {
    margin: 0 16px;
    gap: 4px;
  }
  
  .menu-item {
    padding: 8px 12px;
    font-size: 14px;
  }
  
  .menu-item span {
    display: none;
  }
  
  .btn-primary,
  .btn-secondary {
    padding: 10px 16px;
    font-size: 14px;
  }
  
  .btn-primary span,
  .btn-secondary span {
    display: none;
  }
}
</style>
