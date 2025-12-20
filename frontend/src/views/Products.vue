<template>
  <div class="products-page">
    <Header />
    <div class="page-container">
      <!-- 搜索和筛选区域 -->
      <div class="search-section">
        <div class="search-card">
          <div class="search-wrapper">
            <Icon icon="mdi:magnify" class="search-icon" />
            <input
              v-model="keyword"
              type="text"
              placeholder="搜索图书..."
              class="search-input"
              @keyup.enter="searchProducts"
            />
            <button class="search-btn" @click="searchProducts">
              <Icon icon="mdi:magnify" class="btn-icon" />
              <span>搜索</span>
            </button>
          </div>
        </div>
        
        <!-- 分类筛选 -->
        <div class="filter-section">
          <div class="filter-card">
            <div class="filter-header">
              <Icon icon="mdi:filter" class="filter-icon" />
              <span class="filter-title">图书分类</span>
            </div>
            <div class="category-list">
              <button
                class="category-btn"
                :class="{ active: selectedCategory === '' }"
                @click="selectCategory('')"
              >
                全部
              </button>
              <button
                v-for="category in categories"
                :key="category"
                class="category-btn"
                :class="{ active: selectedCategory === category }"
                @click="selectCategory(category)"
              >
                {{ category }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品网格 -->
      <div class="products-section">
        <div v-if="products.length === 0" class="empty-state">
          <Icon icon="mdi:book-off" class="empty-icon" />
          <p class="empty-text">暂无图书</p>
        </div>
        <div v-else class="products-grid">
          <div
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @click="goToProduct(product.id)"
          >
            <div class="product-image-wrapper">
              <img 
                :src="product.imgurl || '/products/product-1.png'" 
                :alt="product.name"
                class="product-image"
                @error="handleImageError($event)"
              />
              <div class="product-overlay">
                <button class="view-btn" @click.stop="goToProduct(product.id)">
                  <Icon icon="mdi:eye" />
                </button>
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name" :title="product.name">{{ product.name }}</h3>
              <div class="product-footer">
                <span class="product-price">¥{{ product.price }}</span>
                <button class="cart-btn" @click.stop="addToCart(product)">
                  <Icon icon="mdi:cart-plus" class="cart-icon" />
                  <span>加入购物车</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页器 -->
      <div v-if="total > pageSize" class="pagination-section">
        <div class="pagination-card">
          <button
            class="pagination-btn"
            :disabled="currentPage === 1"
            @click="goToPage(currentPage - 1)"
          >
            <Icon icon="mdi:chevron-left" />
          </button>
          <div class="pagination-info">
            <span class="page-text">第 {{ currentPage }} 页</span>
            <span class="total-text">共 {{ totalPages }} 页</span>
          </div>
          <button
            class="pagination-btn"
            :disabled="currentPage === totalPages"
            @click="goToPage(currentPage + 1)"
          >
            <Icon icon="mdi:chevron-right" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'
import Header from '@/components/Header.vue'
import api from '@/utils/api'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@/stores/cart'

interface Product {
  id: number
  name: string
  price: number
  imgurl: string
  category?: string
}

const router = useRouter()
const cartStore = useCartStore()
const products = ref<Product[]>([])
const keyword = ref('')
const selectedCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 图书分类列表（根据数据库中的实际分类）
const categories = ref<string[]>([
  '编程类',
  '设计类',
  '技术类',
  '前端开发',
  '后端开发',
  '移动开发',
  '算法',
  '数据库',
  '网络安全',
  '云计算',
  '机器学习',
  'DevOps',
  '测试',
  '项目管理',
  '架构设计',
  '前端框架',
  '全栈开发',
  '性能优化',
  '设计模式',
  '系统设计'
])

const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

const loadProducts = async () => {
  try {
    const params: any = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    if (keyword.value) {
      params.keyword = keyword.value
    }
    if (selectedCategory.value) {
      params.category = selectedCategory.value
    }
    const response = await api.get('/products', { params })
    products.value = response.data.data.content
    total.value = response.data.data.totalElements
  } catch (error) {
    console.error('加载商品失败:', error)
    ElMessage.error('加载商品失败，请稍后再试')
  }
}

const searchProducts = () => {
  currentPage.value = 1
  loadProducts()
}

const selectCategory = (category: string) => {
  selectedCategory.value = category
  currentPage.value = 1
  loadProducts()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadProducts()
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

const goToProduct = (id: number) => {
  router.push(`/product/${id}`)
}

const addToCart = (product: Product) => {
  cartStore.addItem({
    id: product.id.toString(),
    name: product.name,
    price: product.price,
    imgurl: product.imgurl,
    category: product.category
  })
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = '/products/product-1.png'
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.products-page {
  min-height: 100vh;
  background: #F2F2F7;
  padding-bottom: 80px;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 48px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.search-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(50px);
  -webkit-backdrop-filter: blur(50px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 28px;
  padding: 24px;
  box-shadow: 0 24px 48px -12px rgba(0, 0, 0, 0.08),
              0 1px 0 rgba(255, 255, 255, 0.6) inset;
}

.search-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-icon {
  font-size: 24px;
  width: 24px;
  height: 24px;
  color: #8E8E93;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  padding: 16px 20px;
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

.search-input:focus {
  border-color: #1C1C1E;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.06),
              0 0 0 3px rgba(28, 28, 30, 0.1);
  background: rgba(255, 255, 255, 0.95);
}

.search-input::placeholder {
  color: #8E8E93;
}

.search-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 16px 24px;
  border-radius: 20px;
  background: #1C1C1E;
  color: white;
  border: none;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(28, 28, 30, 0.2);
  white-space: nowrap;
}

.search-btn:hover {
  background: #2C2C2E;
  box-shadow: 0 5px 14px rgba(28, 28, 30, 0.22);
  transform: translateY(-0.5px);
}

.search-btn:active {
  transform: scale(0.96);
}

.btn-icon {
  font-size: 18px;
  width: 18px;
  height: 18px;
}

/* 分类筛选区域 */
.filter-section {
  margin-top: 0;
}

.filter-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(50px);
  -webkit-backdrop-filter: blur(50px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 28px;
  padding: 24px;
  box-shadow: 0 24px 48px -12px rgba(0, 0, 0, 0.08),
              0 1px 0 rgba(255, 255, 255, 0.6) inset;
}

.filter-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.filter-icon {
  font-size: 20px;
  width: 20px;
  height: 20px;
  color: var(--graphite, #1C1C1E);
}

.filter-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--graphite, #1C1C1E);
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.category-btn {
  padding: 10px 20px;
  border-radius: 20px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  font-size: 15px;
  font-weight: 500;
  color: var(--graphite, #1C1C1E);
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: inset 0 1px 4px rgba(0, 0, 0, 0.04);
}

.category-btn:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.category-btn.active {
  background: var(--graphite, #1C1C1E);
  color: white;
  border-color: var(--graphite, #1C1C1E);
  box-shadow: 0 4px 12px rgba(28, 28, 30, 0.3);
}

.category-btn.active:hover {
  background: var(--graphite-light, #2C2C2E);
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(28, 28, 30, 0.35);
}

/* 商品区域 */
.products-section {
  margin-bottom: 48px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 48px;
}

.product-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(50px);
  -webkit-backdrop-filter: blur(50px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 24px 48px -12px rgba(0, 0, 0, 0.08),
              0 1px 0 rgba(255, 255, 255, 0.6) inset;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 28px 56px -12px rgba(0, 0, 0, 0.1),
              0 1px 0 rgba(255, 255, 255, 0.7) inset;
  border-color: rgba(255, 255, 255, 0.7);
}

.product-card:active {
  transform: scale(0.99) translateY(-2px);
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1;
  overflow: hidden;
  background: #F2F2F7;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.product-card:hover .product-image {
  transform: scale(1.03);
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.view-btn {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  color: #1C1C1E;
}

.view-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.15);
}

.view-btn:active {
  transform: scale(0.95);
}

.view-btn :deep(svg) {
  width: 24px;
  height: 24px;
}

.product-info {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  margin: 0 0 16px 0;
  font-size: 17px;
  font-weight: 600;
  color: #1C1C1E;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 51px;
  letter-spacing: -0.01em;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  gap: 12px;
}

.product-price {
  color: #1C1C1E;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.cart-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 20px;
  background: #1C1C1E;
  color: white;
  border: none;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(28, 28, 30, 0.2);
  white-space: nowrap;
}

.cart-btn:hover {
  background: #2C2C2E;
  box-shadow: 0 5px 14px rgba(28, 28, 30, 0.22);
  transform: translateY(-0.5px);
}

.cart-btn:active {
  transform: scale(0.96);
}

.cart-icon {
  font-size: 16px;
  width: 16px;
  height: 16px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120px 32px;
  text-align: center;
}

.empty-icon {
  font-size: 80px;
  width: 80px;
  height: 80px;
  color: #C7C7CC;
  margin-bottom: 24px;
}

.empty-text {
  font-size: 17px;
  font-weight: 500;
  color: #8E8E93;
  margin: 0;
}

/* 分页器 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 48px;
}

.pagination-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(50px);
  -webkit-backdrop-filter: blur(50px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 28px;
  padding: 12px 24px;
  box-shadow: 0 8px 24px -8px rgba(0, 0, 0, 0.08),
              0 1px 0 rgba(255, 255, 255, 0.6) inset;
}

.pagination-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.1);
  color: #1C1C1E;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.pagination-btn:hover:not(:disabled) {
  background: white;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
  transform: translateY(-0.5px);
}

.pagination-btn:active:not(:disabled) {
  transform: scale(0.95);
}

.pagination-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.pagination-btn :deep(svg) {
  width: 24px;
  height: 24px;
}

.pagination-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  font-weight: 500;
  color: #1C1C1E;
}

.page-text {
  font-weight: 600;
}

.total-text {
  color: #8E8E93;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 32px;
  }
}

@media (max-width: 768px) {
  .page-container {
    padding: 24px 16px;
  }

  .search-card {
    padding: 20px;
    border-radius: 24px;
  }

  .search-wrapper {
    flex-direction: column;
    gap: 12px;
  }

  .search-btn {
    width: 100%;
    justify-content: center;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 24px;
  }

  .product-info {
    padding: 16px;
  }

  .category-list {
    gap: 8px;
  }

  .category-btn {
    padding: 8px 16px;
    font-size: 14px;
  }

  .filter-card {
    padding: 20px;
  }

  .product-name {
    font-size: 15px;
    min-height: 45px;
  }

  .product-price {
    font-size: 24px;
  }

  .cart-btn {
    padding: 8px 16px;
    font-size: 13px;
  }

  .cart-btn span {
    display: none;
  }

  .pagination-card {
    padding: 10px 16px;
  }

  .pagination-info {
    flex-direction: column;
    gap: 4px;
    font-size: 13px;
  }
}
</style>
