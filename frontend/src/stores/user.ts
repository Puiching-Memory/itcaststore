import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/utils/api'

export interface User {
  id: number
  username: string
  email: string
  gender: string
  telephone: string
  role: string
  introduce?: string
}

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  
  // 使用普通 ref 存储 token，避免计算属性被写入的问题
  const token = ref<string | null>(localStorage.getItem('token'))
  
  // isAuthenticated 作为计算属性，只依赖 token
  const isAuthenticated = computed(() => !!token.value)

  const login = async (username: string, password: string) => {
    const response = await api.post('/auth/login', { username, password })
    token.value = response.data.data.token
    user.value = response.data.data.data
    localStorage.setItem('token', token.value || '')
    return response.data
  }

  const register = async (userData: any) => {
    const response = await api.post('/auth/register', userData)
    token.value = response.data.data.token
    user.value = response.data.data.user
    localStorage.setItem('token', token.value || '')
    return response.data
  }

  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
  }

  const fetchUserInfo = async () => {
    try {
      const response = await api.get('/users/me')
      user.value = response.data.data
      return response.data
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  const updateUserInfo = async (userData: Partial<User>) => {
    try {
      const response = await api.put('/users/me', userData)
      user.value = response.data.data
      return response.data
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw error
    }
  }

  return {
    user,
    token,
    isAuthenticated,
    login,
    register,
    logout,
    fetchUserInfo,
    updateUserInfo
  }
})
