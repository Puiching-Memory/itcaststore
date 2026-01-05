<template>
  <div :class="{ 'auth-page': isAuthPage }">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const isAuthPage = computed(() => {
  return route.name === 'login' || route.name === 'register'
})

// 应用启动时，如果有 token 但没有用户信息，自动获取用户信息
onMounted(async () => {
  if (userStore.isAuthenticated && !userStore.user) {
    try {
      await userStore.fetchUserInfo()
    } catch (error) {
      // 如果获取失败（如 token 过期），清除认证状态
      console.error('获取用户信息失败:', error)
      userStore.logout()
    }
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  /* iOS系统级灰调色板 */
  --ios-gray-50: #F9F9FB;
  --ios-gray-100: #F2F2F7;
  --ios-gray-200: #E5E5EA;
  --ios-gray-300: #D1D1D6;
  --ios-gray-400: #C7C7CC;
  --ios-gray-500: #AEAEB2;
  --ios-gray-600: #8E8E93;
  --ios-gray-700: #636366;
  --ios-gray-800: #48484A;
  --ios-gray-900: #1C1C1E;
  
  /* 石墨色系 */
  --graphite: #1C1C1E;
  --graphite-light: #2C2C2E;
  
  /* 毛玻璃背景 - 白色半透明 */
  --frosted-bg: rgba(255, 255, 255, 0.5);
  --frosted-bg-light: rgba(255, 255, 255, 0.6);
  --frosted-bg-medium: rgba(255, 255, 255, 0.7);
  --frosted-bg-heavy: rgba(255, 255, 255, 0.8);
  --frosted-bg-full: rgba(255, 255, 255, 0.9);
  
  /* 毛玻璃边框 */
  --frosted-border: rgba(255, 255, 255, 0.6);
  --frosted-border-light: rgba(255, 255, 255, 0.8);
  
  /* 毛玻璃模糊度 */
  --blur-sm: 20px;
  --blur-md: 40px;
  --blur-lg: 50px;
  --blur-xl: 60px;
  
  /* 圆角 */
  --radius-sm: 12px;
  --radius-md: 20px;
  --radius-lg: 24px;
  --radius-xl: 28px;
  --radius-2xl: 32px;
  --radius-3xl: 40px;
  --radius-4xl: 48px;
  --radius-full: 50%;
  
  /* 过渡动画 */
  --transition-fast: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-base: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-slow: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  
  /* 阴影 */
  --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.06);
  --shadow-md: 0 4px 16px rgba(0, 0, 0, 0.1);
  --shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.12);
  --shadow-xl: 0 24px 48px -12px rgba(0, 0, 0, 0.08);
  --shadow-2xl: 0 32px 64px -16px rgba(0, 0, 0, 0.15);
  
  /* 内嵌阴影（inset） */
  --shadow-inset: 0 1px 0 rgba(255, 255, 255, 0.6) inset;
  --shadow-inset-light: 0 1px 0 rgba(255, 255, 255, 0.8) inset;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'Segoe UI', Roboto, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background: var(--ios-gray-100);
  color: var(--ios-gray-900);
  letter-spacing: -0.01em;
}

#app {
  min-height: 100vh;
  position: relative;
  background: var(--ios-gray-100);
}

/* 为内容区域添加半透明背景（登录和注册页面除外） */
#app > div:not(.auth-page)::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: var(--ios-gray-100);
  z-index: -1;
  pointer-events: none;
}

/* 登录和注册页面保持原有渐变背景 */
.auth-page {
  position: relative;
}

.auth-page::before {
  display: none;
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: var(--ios-gray-100);
}

::-webkit-scrollbar-thumb {
  background: var(--ios-gray-300);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--ios-gray-400);
}

/* 强制覆盖下拉菜单背景 */
.dropdown-menu,
.el-dropdown-menu__item {
  background-color: #ffffff !important;
}
</style>
