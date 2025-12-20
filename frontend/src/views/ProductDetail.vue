<template>
  <div class="product-detail">
    <Header />
    <div class="page-container">
      <div v-if="product" class="detail-content">
        <!-- 商品图片 -->
        <div class="image-section">
          <div class="image-card">
            <img 
              :src="product.imgurl || '/products/product-1.png'" 
              :alt="product.name"
              class="detail-image"
              @error="handleImageError($event)"
            />
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="info-section">
          <div class="info-card">
            <h1 class="product-title">{{ product.name }}</h1>
            
            <div class="price-section">
              <span class="price-label">价格</span>
              <span class="price">¥{{ product.price }}</span>
            </div>

            <div class="meta-section">
              <div class="meta-item">
                <Icon icon="mdi:tag" class="meta-icon" />
                <span class="meta-label">分类</span>
                <span class="meta-value">{{ product.category }}</span>
              </div>
              <div class="meta-item">
                <Icon icon="mdi:package-variant" class="meta-icon" />
                <span class="meta-label">库存</span>
                <span class="meta-value">{{ product.pnum }} 本</span>
              </div>
            </div>

            <div class="description-section">
              <span class="description-label">商品描述</span>
              <p class="description">{{ product.description }}</p>
            </div>

            <button class="add-cart-btn" @click="addToCart">
              <Icon icon="mdi:cart-plus" class="btn-icon" />
              <span>加入购物车</span>
            </button>
          </div>
        </div>
      </div>
      
      <div v-else class="loading-state">
        <Icon icon="mdi:loading" class="loading-icon" />
        <p class="loading-text">加载中...</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Icon } from '@iconify/vue'
import Header from '@/components/Header.vue'
import api from '@/utils/api'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const cartStore = useCartStore()
const product = ref<any>(null)

const loadProduct = async () => {
  try {
    const response = await api.get(`/products/${route.params.id}`)
    product.value = response.data.data
  } catch (error) {
    console.error('加载商品详情失败:', error)
    ElMessage.error('加载商品详情失败，请稍后再试')
  }
}

const addToCart = () => {
  if (product.value) {
    cartStore.addItem({
      id: product.value.id.toString(),
      name: product.value.name,
      price: product.value.price,
      imgurl: product.value.imgurl,
      category: product.value.category,
      description: product.value.description
    })
  }
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = '/products/product-1.png'
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail {
  min-height: 100vh;
  background: #F2F2F7;
  padding-bottom: 80px;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 48px 32px;
}

.detail-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
  align-items: start;
}

/* 图片区域 */
.image-section {
  position: sticky;
  top: 100px;
}

.image-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(50px);
  -webkit-backdrop-filter: blur(50px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 40px;
  padding: 24px;
  box-shadow: 0 24px 48px -12px rgba(0, 0, 0, 0.08),
              0 1px 0 rgba(255, 255, 255, 0.6) inset;
  overflow: hidden;
}

.detail-image {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  border-radius: 28px;
  display: block;
}

/* 信息区域 */
.info-section {
  display: flex;
  flex-direction: column;
}

.info-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(50px);
  -webkit-backdrop-filter: blur(50px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 40px;
  padding: 48px;
  box-shadow: 0 24px 48px -12px rgba(0, 0, 0, 0.08),
              0 1px 0 rgba(255, 255, 255, 0.6) inset;
}

.product-title {
  margin: 0 0 32px 0;
  font-size: 36px;
  font-weight: 800;
  color: #1C1C1E;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.price-label {
  font-size: 13px;
  font-weight: 700;
  color: #8E8E93;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.price {
  color: #1C1C1E;
  font-size: 48px;
  font-weight: 800;
  letter-spacing: -0.03em;
}

.meta-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 20px;
  box-shadow: inset 0 1px 4px rgba(0, 0, 0, 0.04);
}

.meta-icon {
  font-size: 20px;
  width: 20px;
  height: 20px;
  color: #1C1C1E;
}

.meta-label {
  font-size: 13px;
  font-weight: 700;
  color: #8E8E93;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  min-width: 60px;
}

.meta-value {
  font-size: 17px;
  font-weight: 600;
  color: #1C1C1E;
  margin-left: auto;
}

.description-section {
  margin-bottom: 40px;
}

.description-label {
  display: block;
  font-size: 13px;
  font-weight: 700;
  color: #8E8E93;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 16px;
}

.description {
  font-size: 17px;
  line-height: 1.8;
  color: #636366;
  margin: 0;
  padding: 24px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 24px;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.04);
}

.add-cart-btn {
  width: 100%;
  padding: 20px 32px;
  border-radius: 28px;
  background: #1C1C1E;
  color: white;
  border: none;
  font-weight: 700;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(28, 28, 30, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.add-cart-btn:hover {
  background: #2C2C2E;
  box-shadow: 0 5px 14px rgba(28, 28, 30, 0.28);
  transform: translateY(-0.5px);
}

.add-cart-btn:active {
  transform: scale(0.98);
}

.btn-icon {
  font-size: 20px;
  width: 20px;
  height: 20px;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120px 32px;
  text-align: center;
}

.loading-icon {
  font-size: 48px;
  width: 48px;
  height: 48px;
  color: #8E8E93;
  margin-bottom: 24px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 17px;
  font-weight: 500;
  color: #8E8E93;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .detail-content {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .image-section {
    position: static;
  }

  .image-card {
    max-width: 600px;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .page-container {
    padding: 32px 16px;
  }

  .info-card {
    padding: 32px 24px;
    border-radius: 32px;
  }

  .product-title {
    font-size: 28px;
    margin-bottom: 24px;
  }

  .price {
    font-size: 36px;
  }

  .image-card {
    padding: 16px;
    border-radius: 32px;
  }

  .detail-image {
    border-radius: 24px;
  }

  .add-cart-btn {
    padding: 16px 24px;
    font-size: 16px;
  }
}
</style>
