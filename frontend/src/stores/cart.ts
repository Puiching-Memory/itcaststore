import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from './user'

export interface CartItem {
  id: string
  name: string
  price: number
  imgurl: string
  quantity: number
  category?: string
  description?: string
}

const CART_STORAGE_KEY = 'itcaststore_cart'

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])

  // 从 localStorage 加载购物车数据
  const loadCart = () => {
    try {
      const stored = localStorage.getItem(CART_STORAGE_KEY)
      if (stored) {
        items.value = JSON.parse(stored)
      }
    } catch (error) {
      console.error('加载购物车失败:', error)
      items.value = []
    }
  }

  // 保存购物车到 localStorage
  const saveCart = () => {
    try {
      localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(items.value))
    } catch (error) {
      console.error('保存购物车失败:', error)
    }
  }

  // 初始化时加载购物车
  loadCart()

  // 计算总数量
  const totalQuantity = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  // 计算总价
  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  // 添加商品到购物车
  const addItem = (product: {
    id: string
    name: string
    price: number
    imgurl?: string
    category?: string
    description?: string
  }, quantity: number = 1) => {
    // 检查用户是否已登录
    const userStore = useUserStore()
    if (!userStore.isAuthenticated) {
      ElMessage.warning({
        message: '请先登录后再添加商品到购物车',
        duration: 2000,
        offset: typeof window !== 'undefined' ? window.innerHeight - 150 : 80,
        showClose: false
      })
      return false
    }

    const existingItem = items.value.find(item => item.id === product.id)

    if (existingItem) {
      // 如果商品已存在，增加数量
      existingItem.quantity += quantity
      ElMessage.success({
        message: `已将《${product.name}》添加到购物车（当前数量：${existingItem.quantity}）`,
        duration: 1200,
        offset: typeof window !== 'undefined' ? window.innerHeight - 150 : 80,
        showClose: false
      })
    } else {
      // 如果商品不存在，添加新项
      const newItem: CartItem = {
        id: product.id,
        name: product.name,
        price: product.price,
        imgurl: product.imgurl || '/products/product-1.png',
        quantity,
        category: product.category,
        description: product.description
      }
      items.value.push(newItem)
      ElMessage.success({
        message: `已将《${product.name}》添加到购物车`,
        duration: 1200,
        offset: typeof window !== 'undefined' ? window.innerHeight - 150 : 80,
        showClose: false
      })
    }

    saveCart()
    return true
  }

  // 从购物车删除商品
  const removeItem = (productId: string) => {
    const index = items.value.findIndex(item => item.id === productId)
    if (index !== -1) {
      const item = items.value[index]
      items.value.splice(index, 1)
      saveCart()
      ElMessage.success({
        message: `已从购物车删除《${item.name}》`,
        duration: 1200,
        offset: typeof window !== 'undefined' ? window.innerHeight - 150 : 80,
        showClose: false
      })
    }
  }

  // 更新商品数量
  const updateQuantity = (productId: string, quantity: number) => {
    if (quantity <= 0) {
      removeItem(productId)
      return
    }

    const item = items.value.find(item => item.id === productId)
    if (item) {
      item.quantity = quantity
      saveCart()
    }
  }

  // 清空购物车
  const clearCart = () => {
    items.value = []
    saveCart()
    ElMessage.success({
      message: '购物车已清空',
      duration: 1200,
      offset: typeof window !== 'undefined' ? window.innerHeight - 150 : 80,
      showClose: false
    })
  }

  // 检查商品是否在购物车中
  const isInCart = (productId: string) => {
    return items.value.some(item => item.id === productId)
  }

  // 获取商品在购物车中的数量
  const getItemQuantity = (productId: string) => {
    const item = items.value.find(item => item.id === productId)
    return item ? item.quantity : 0
  }

  return {
    items,
    totalQuantity,
    totalPrice,
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    isInCart,
    getItemQuantity,
    loadCart,
    saveCart
  }
})

