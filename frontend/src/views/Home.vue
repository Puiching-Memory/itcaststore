<template>
  <div class="home">
    <el-container>
      <el-header>
        <Header />
      </el-header>
      <el-main>
        <!-- 轮播图区域 -->
        <div class="banner-section">
          <div class="banner-container">
            <el-carousel 
              ref="carouselRef"
              height="360px" 
              :interval="5000" 
              indicator-position="none"
              :arrow="'hover'"
              class="hero-carousel"
              @change="handleCarouselChange"
            >
              <el-carousel-item v-for="item in banners" :key="item.id">
                <div class="banner-wrapper">
                  <div class="banner-image-container">
                    <img :src="item.image" :alt="item.title" class="banner-image" />
                    <div class="banner-gradient-overlay"></div>
                  </div>
                  <div class="banner-content">
                    <div class="banner-badge">
                      <Icon icon="mdi:star" class="badge-icon" />
                      <span>精选推荐</span>
                    </div>
                    <h1 class="banner-title">{{ item.title }}</h1>
                    <p class="banner-subtitle">发现优质图书，开启阅读之旅</p>
                    <button class="banner-cta" @click="$router.push('/products')">
                      <span>立即探索</span>
                      <Icon icon="mdi:arrow-right" class="cta-icon" />
                    </button>
                  </div>
                </div>
              </el-carousel-item>
            </el-carousel>
            <div class="carousel-indicators">
              <div 
                v-for="(item, index) in banners" 
                :key="item.id"
                class="indicator"
                :class="{ active: index === currentBannerIndex }"
                @click="goToBanner(index)"
              ></div>
            </div>
          </div>
        </div>

        <!-- 热卖商品区域 -->
        <div class="section hot-products">
          <div class="section-header">
            <div class="section-title-wrapper">
              <div class="fire-badge">
                <Icon icon="mdi:fire" class="section-icon" />
              </div>
              <div>
                <h2 class="section-title">本周热卖</h2>
                <p class="section-subtitle">精选畅销好书，不容错过</p>
              </div>
            </div>
            <button class="more-btn" @click="$router.push('/products')">
              <span>查看全部</span>
              <Icon icon="mdi:arrow-right" class="more-icon" />
            </button>
          </div>
          
          <div class="hot-products-grid">
            <div 
              v-for="(product, index) in hotProducts" 
              :key="product.id"
              class="hot-product-card"
              :class="{ featured: index === 0 }"
              @click="goToProduct(product.id)"
            >
              <div class="hot-product-badge" v-if="index < 3">
                <Icon icon="mdi:trending-up" class="trending-icon" />
                <span>TOP {{ index + 1 }}</span>
              </div>
              <div class="hot-product-image-wrapper">
                <img 
                  :src="product.imgurl || '/products/product-1.png'" 
                  :alt="product.name"
                  class="hot-product-image"
                  @error="handleImageError($event)"
                />
                <div class="hot-product-overlay">
                  <button class="hot-view-btn" @click.stop="goToProduct(product.id)">
                    <Icon icon="mdi:eye" />
                    <span>查看详情</span>
                  </button>
                </div>
              </div>
              <div class="hot-product-content">
                <div class="hot-product-header">
                  <h3 class="hot-product-name">{{ product.name }}</h3>
                  <div class="hot-product-tags">
                    <span class="tag">热销</span>
                    <span class="tag" v-if="index === 0">推荐</span>
                  </div>
                </div>
                <div class="hot-product-footer">
                  <div class="hot-product-price-wrapper">
                    <span class="hot-product-price">¥{{ product.price }}</span>
                    <span class="hot-product-label">优惠价</span>
                  </div>
                  <button class="hot-cart-btn" @click.stop="addToCart(product)">
                    <Icon icon="mdi:cart-plus" class="hot-cart-icon" />
                    <span>立即购买</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 公告区域 - 固定在右侧 -->
        <div class="notices-sidebar">
          <div class="notices-sidebar-header">
            <div class="horn-badge">
              <Icon icon="mdi:bullhorn" class="section-icon" />
            </div>
            <div>
              <h2 class="notices-sidebar-title">系统公告</h2>
              <p class="notices-sidebar-subtitle">最新动态</p>
            </div>
          </div>
          <div class="notices-sidebar-content">
            <div 
              v-for="(notice, index) in notices" 
              :key="notice.id"
              class="notice-card-sidebar"
              :class="{ highlight: index === 0 }"
            >
              <div class="notice-card-header">
                <div class="notice-icon-wrapper">
                  <Icon 
                    :icon="index === 0 ? 'mdi:alert-circle' : 'mdi:information-outline'" 
                    class="notice-icon-modern"
                  />
                </div>
                <div class="notice-meta">
                  <span class="notice-time">{{ notice.time }}</span>
                  <span class="notice-badge" v-if="index === 0">最新</span>
                </div>
              </div>
              <h3 class="notice-title-sidebar">{{ notice.title }}</h3>
              <div class="notice-content-sidebar" v-html="notice.details"></div>
              <div class="notice-footer">
                <button class="notice-read-more" @click="handleNoticeClick(notice)">
                  <span>查看详情</span>
                  <Icon icon="mdi:arrow-right" class="arrow-icon" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </el-main>
    </el-container>

    <!-- 右下角智能推荐气泡 -->
    <div class="agent-bubble-container">
      <transition name="fade">
        <div v-if="agentBubbleOpen" class="agent-bubble-card">
          <div class="agent-bubble-header">
            <div class="agent-header-left">
              <Icon icon="mdi:robot" class="agent-icon" />
              <h3>智能图书推荐</h3>
            </div>
            <el-button
              text
              @click="agentBubbleOpen = false"
              class="close-btn"
            >
              <Icon icon="mdi:close" />
            </el-button>
          </div>
          <div class="agent-bubble-body">
            <div class="agent-messages">
              <div v-if="agentAnswer" class="agent-message agent-answer">
                <div class="message-avatar">
                  <Icon icon="mdi:robot" />
                </div>
                <div class="message-content">
                  <p style="white-space: pre-wrap">{{ agentAnswer }}</p>
                </div>
              </div>
            </div>
            <div class="agent-input-area">
              <el-input
                v-model="agentQuestion"
                type="textarea"
                :rows="3"
                placeholder="请输入你的需求，例如：我想找一本适合初学者的Java Web开发书"
                @keyup.ctrl.enter="handleAgentRecommend"
              />
              <el-button
                type="primary"
                :loading="agentLoading"
                @click="handleAgentRecommend"
                class="send-btn"
              >
                <Icon icon="mdi:send" style="margin-right: 6px" />
                获取推荐
              </el-button>
            </div>
          </div>
        </div>
      </transition>

      <div
        class="agent-bubble-button"
        @click="toggleAgentBubble"
        :class="{ active: agentBubbleOpen }"
      >
        <Icon 
          :icon="agentBubbleOpen ? 'mdi:close' : 'mdi:robot'" 
          :width="24" 
          :height="24"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'
import type { ComponentPublicInstance } from 'vue'
import Header from '@/components/Header.vue'
import api from '@/utils/api'
import { useCartStore } from '@/stores/cart'

interface Product {
  id: number
  name: string
  price: number
  imgurl: string
}

interface Notice {
  id: number
  title: string
  details: string
  time: string
}

interface Banner {
  id: number
  title: string
  image: string
}

const router = useRouter()
const cartStore = useCartStore()
const hotProducts = ref<Product[]>([])
const notices = ref<Notice[]>([])
const banners = ref<Banner[]>([
  { id: 1, title: '新书上架', image: '/banners/banner1.png' },
  { id: 2, title: '促销活动', image: '/banners/banner2.png' },
  { id: 3, title: '品牌宣传', image: '/banners/banner3.png' }
])
const agentQuestion = ref('')
const agentAnswer = ref('')
const agentLoading = ref(false)
const agentBubbleOpen = ref(false)
const currentBannerIndex = ref(0)
const carouselRef = ref<ComponentPublicInstance | null>(null)

const goToProduct = (id: number) => {
  router.push(`/product/${id}`)
}

const addToCart = (product: Product) => {
  cartStore.addItem({
    id: product.id.toString(),
    name: product.name,
    price: product.price,
    imgurl: product.imgurl
  })
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = '/products/product-1.png'
}

const handleNoticeClick = (notice: Notice) => {
  // TODO: 实现公告详情查看功能
  console.log('查看公告:', notice)
}

const handleCarouselChange = (index: number) => {
  currentBannerIndex.value = index
}

const goToBanner = (index: number) => {
  if (carouselRef.value) {
    ;(carouselRef.value as any).setActiveItem(index)
    currentBannerIndex.value = index
  }
}

const toggleAgentBubble = () => {
  agentBubbleOpen.value = !agentBubbleOpen.value
  if (!agentBubbleOpen.value) {
    agentQuestion.value = ''
    agentAnswer.value = ''
  }
}

const handleAgentRecommend = async () => {
  if (!agentQuestion.value.trim()) {
    return
  }
  agentLoading.value = true
  agentAnswer.value = ''
  try {
    const res = await api.post('/agent/recommend', {
      question: agentQuestion.value
    })
    agentAnswer.value = res.data.data || '暂时没有合适的推荐结果，请稍后再试。'
  } catch (error) {
    console.error('获取智能推荐失败:', error)
    agentAnswer.value = '获取推荐失败，请检查网络或稍后再试。'
  } finally {
    agentLoading.value = false
  }
}

onMounted(async () => {
  try {
    const [productsRes, noticesRes] = await Promise.all([
      api.get('/products/hot'),
      api.get('/notices')
    ])
    hotProducts.value = productsRes.data.data
    notices.value = noticesRes.data.data
  } catch (error) {
    console.error('加载数据失败:', error)
  }
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  padding-bottom: 80px;
  background: #F2F2F7;
}

:deep(.el-main) {
  padding: 24px 0;
  padding-right: 400px; /* 为右侧固定公告留出空间 */
  background: transparent;
}

/* 轮播图区域 */
.banner-section {
  margin: 0 auto 50px;
  max-width: 1400px;
  padding: 0 32px;
}

.banner-container {
  position: relative;
  border-radius: var(--radius-4xl);
  overflow: hidden;
  box-shadow: var(--shadow-2xl),
              0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(var(--blur-xl));
  -webkit-backdrop-filter: blur(var(--blur-xl));
}

.hero-carousel {
  border-radius: var(--radius-4xl);
  overflow: hidden;
}

.hero-carousel :deep(.el-carousel__container) {
  height: 360px;
}

.hero-carousel :deep(.el-carousel__arrow) {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(var(--blur-sm));
  -webkit-backdrop-filter: blur(var(--blur-sm));
  border: 1px solid rgba(255, 255, 255, 0.8);
  width: 48px;
  height: 48px;
  border-radius: var(--radius-full);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  transition: var(--transition-base);
  color: var(--graphite);
}

.hero-carousel :deep(.el-carousel__arrow) i {
  color: var(--graphite);
  font-size: 20px;
  font-weight: 600;
}

.hero-carousel :deep(.el-carousel__arrow):hover {
  background: white;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.3);
  color: var(--graphite);
  border-color: rgba(255, 255, 255, 1);
}

.hero-carousel :deep(.el-carousel__arrow):hover i {
  color: var(--graphite);
}

.hero-carousel :deep(.el-carousel__arrow--left) {
  left: 24px;
}

.hero-carousel :deep(.el-carousel__arrow--right) {
  right: 24px;
}

.banner-wrapper {
  width: 100%;
  height: 360px;
  position: relative;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.banner-image-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  transition: transform 8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.banner-wrapper:hover .banner-image {
  transform: scale(1.08);
}

.banner-gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(28, 28, 30, 0.75) 0%,
    rgba(28, 28, 30, 0.5) 50%,
    rgba(28, 28, 30, 0.3) 100%
  );
  z-index: 2;
}

.banner-content {
  position: relative;
  z-index: 3;
  padding: 0 60px;
  max-width: 680px;
  color: white;
}

.banner-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(var(--blur-sm));
  -webkit-backdrop-filter: blur(var(--blur-sm));
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: var(--radius-lg);
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 16px;
  box-shadow: var(--shadow-md);
}

.badge-icon {
  font-size: 16px;
  width: 16px;
  height: 16px;
}

.banner-title {
  margin: 0 0 12px 0;
  font-size: 42px;
  font-weight: 900;
  line-height: 1.1;
  letter-spacing: -0.03em;
  text-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.banner-subtitle {
  margin: 0 0 24px 0;
  font-size: 18px;
  font-weight: 500;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.95);
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.banner-cta {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 14px 28px;
  background: white;
  color: var(--graphite);
  border: none;
  border-radius: var(--radius-2xl);
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: var(--transition-base);
  box-shadow: var(--shadow-lg);
}

.banner-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
  background: rgba(255, 255, 255, 0.98);
}

.banner-cta:active {
  transform: translateY(0);
}

.cta-icon {
  font-size: 20px;
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.banner-cta:hover .cta-icon {
  transform: translateX(4px);
}

.carousel-indicators {
  position: absolute;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
  z-index: 4;
}

.indicator {
  width: 8px;
  height: 8px;
  border-radius: var(--radius-full);
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  transition: var(--transition-base);
}

.indicator:hover {
  background: var(--frosted-bg-light);
  transform: scale(1.2);
}

.indicator.active {
  width: 32px;
  border-radius: 4px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

/* 通用区域样式 */
.section {
  margin-top: 50px;
  max-width: 1400px;
  margin-left: auto;
  margin-right: auto;
  padding: 0 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.section-title-wrapper {
  display: flex;
  align-items: center;
  gap: 20px;
}

.fire-badge {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3);
}

.section-icon {
  font-size: 28px;
  width: 28px;
  height: 28px;
  color: white;
}

.section-title {
  margin: 0 0 4px 0;
  font-size: 28px;
  font-weight: 900;
  color: var(--graphite);
  letter-spacing: -0.03em;
  line-height: 1.2;
}

.section-subtitle {
  margin: 0;
  font-size: 15px;
  font-weight: 500;
  color: var(--ios-gray-600);
  letter-spacing: -0.01em;
}

.more-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: var(--radius-lg);
  background: var(--frosted-bg-heavy);
  backdrop-filter: blur(var(--blur-md));
  -webkit-backdrop-filter: blur(var(--blur-md));
  border: 1px solid var(--frosted-border);
  box-shadow: var(--shadow-sm);
  font-weight: 600;
  font-size: 15px;
  color: var(--graphite);
  cursor: pointer;
  transition: var(--transition-fast);
}

.more-btn:hover {
  background: white;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
  transform: translateY(-0.5px);
}

.more-btn:active {
  transform: scale(0.96);
}

.more-icon {
  font-size: 18px;
  width: 18px;
  height: 18px;
}

/* 热卖商品区域 - 网格布局设计 */
.hot-products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-top: 0;
  align-items: start;
}

.hot-product-card {
  background: var(--frosted-bg-medium);
  backdrop-filter: blur(var(--blur-lg));
  -webkit-backdrop-filter: blur(var(--blur-lg));
  border: 1px solid var(--frosted-border);
  border-radius: var(--radius-2xl);
  overflow: hidden;
  box-shadow: var(--shadow-xl),
              var(--shadow-inset);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
}

.hot-product-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: var(--radius-2xl);
  padding: 2px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.3), rgba(255, 142, 83, 0.3), rgba(74, 144, 226, 0.3));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1;
  pointer-events: none;
}

.hot-product-card.featured {
  box-shadow: var(--shadow-2xl),
              0 0 0 2px rgba(255, 107, 107, 0.2),
              var(--shadow-inset-light);
}

.hot-product-card.featured::before {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.5), rgba(255, 142, 83, 0.5));
}

.hot-product-card:hover {
  transform: translateY(-8px) scale(1.03);
  box-shadow: 0 20px 60px -12px rgba(0, 0, 0, 0.25),
              0 0 0 1px rgba(255, 255, 255, 0.5),
              inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border-color: rgba(255, 255, 255, 0.3);
}

.hot-product-card:hover::before {
  opacity: 1;
}

.hot-product-card.featured:hover {
  transform: translateY(-10px) scale(1.04);
  box-shadow: 0 24px 72px -12px rgba(255, 107, 107, 0.3),
              0 0 0 2px rgba(255, 107, 107, 0.4),
              inset 0 1px 0 rgba(255, 255, 255, 0.7);
  border-color: rgba(255, 107, 107, 0.4);
}

.hot-product-badge {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border-radius: var(--radius-md);
  font-size: 13px;
  font-weight: 700;
  color: white;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4),
              0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.hot-product-card:hover .hot-product-badge {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(255, 107, 107, 0.5),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
}

.trending-icon {
  font-size: 16px;
  width: 16px;
  height: 16px;
}

.hot-product-image-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 4 / 3;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
}

.hot-product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  filter: brightness(1) saturate(1);
}

.hot-product-card:hover .hot-product-image {
  transform: scale(1.12) translateZ(0);
  filter: brightness(1.1) saturate(1.15);
}

.hot-product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(0, 0, 0, 0.3) 0%,
    rgba(0, 0, 0, 0.5) 50%,
    rgba(0, 0, 0, 0.3) 100%
  );
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.hot-product-card:hover .hot-product-overlay {
  opacity: 1;
  background: linear-gradient(
    135deg,
    rgba(0, 0, 0, 0.4) 0%,
    rgba(0, 0, 0, 0.6) 50%,
    rgba(0, 0, 0, 0.4) 100%
  );
}

.hot-view-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: var(--radius-lg);
  font-size: 15px;
  font-weight: 600;
  color: var(--graphite);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2),
              0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  transform: translateY(10px);
  opacity: 0;
}

.hot-product-card:hover .hot-view-btn {
  transform: translateY(0);
  opacity: 1;
}

.hot-view-btn:hover {
  transform: translateY(-2px) scale(1.05);
  background: white;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3),
              0 0 0 1px rgba(255, 255, 255, 0.2) inset;
}

.hot-view-btn :deep(svg) {
  width: 18px;
  height: 18px;
}

.hot-product-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-product-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.hot-product-name {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  color: var(--graphite);
  line-height: 1.3;
  letter-spacing: -0.02em;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.hot-product-card.featured .hot-product-name {
  font-size: 20px;
}

.hot-product-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 12px;
  background: rgba(28, 28, 30, 0.08);
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 600;
  color: var(--graphite);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.hot-product-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
  padding-top: 6px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.hot-product-price-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.hot-product-price {
  font-size: 26px;
  font-weight: 900;
  color: var(--graphite);
  letter-spacing: -0.03em;
  line-height: 1;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.hot-product-card.featured .hot-product-price {
  font-size: 28px;
}

.hot-product-card:hover .hot-product-price {
  color: #FF6B6B;
  transform: scale(1.05);
}

.hot-product-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--ios-gray-600);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.hot-cart-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, var(--graphite) 0%, var(--graphite-light) 100%);
  color: white;
  border: none;
  border-radius: var(--radius-lg);
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 16px rgba(28, 28, 30, 0.25),
              0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  white-space: nowrap;
  position: relative;
  overflow: hidden;
}

.hot-cart-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.hot-cart-btn:hover {
  background: linear-gradient(135deg, var(--graphite-light) 0%, var(--graphite) 100%);
  box-shadow: 0 8px 24px rgba(28, 28, 30, 0.35),
              0 0 0 1px rgba(255, 255, 255, 0.15) inset;
  transform: translateY(-2px) scale(1.02);
}

.hot-cart-btn:hover::before {
  left: 100%;
}

.hot-cart-btn:active {
  transform: translateY(0) scale(0.98);
}

.hot-cart-icon {
  font-size: 18px;
  width: 18px;
  height: 18px;
}


/* 公告区域 - 固定在右侧 */
.notices-sidebar {
  position: fixed;
  top: 80px;
  right: 20px;
  width: 360px;
  max-height: calc(100vh - 100px);
  background: var(--frosted-bg-medium);
  backdrop-filter: blur(var(--blur-lg));
  -webkit-backdrop-filter: blur(var(--blur-lg));
  border: 1px solid var(--frosted-border);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-2xl);
  z-index: 100;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.notices-sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.notices-sidebar-title {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 900;
  color: var(--graphite);
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.notices-sidebar-subtitle {
  margin: 0;
  font-size: 13px;
  font-weight: 500;
  color: var(--ios-gray-600);
  letter-spacing: -0.01em;
}

.notices-sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notices-sidebar-content::-webkit-scrollbar {
  width: 6px;
}

.notices-sidebar-content::-webkit-scrollbar-track {
  background: transparent;
}

.notices-sidebar-content::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}

.notices-sidebar-content::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.2);
}

.horn-badge {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-lg);
  flex-shrink: 0;
}

.notice-card-sidebar {
  background: var(--frosted-bg-heavy);
  backdrop-filter: blur(var(--blur-md));
  -webkit-backdrop-filter: blur(var(--blur-md));
  border: 1px solid var(--frosted-border);
  border-radius: var(--radius-xl);
  padding: 16px;
  box-shadow: var(--shadow-sm);
  transition: var(--transition-base);
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: relative;
  overflow: hidden;
}

.notice-card-sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #4A90E2 0%, #357ABD 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.notice-card-sidebar.highlight::before {
  opacity: 1;
}

.notice-card-sidebar.highlight {
  border-color: rgba(74, 144, 226, 0.3);
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.15),
              0 0 0 1px rgba(74, 144, 226, 0.1);
}

.notice-card-sidebar:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  border-color: var(--frosted-border-light);
}

.notice-card-sidebar.highlight:hover {
  box-shadow: 0 8px 20px rgba(74, 144, 226, 0.2),
              0 0 0 1px rgba(74, 144, 226, 0.2);
}

.notice-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.notice-icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.15) 0%, rgba(53, 122, 189, 0.1) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notice-card-modern.highlight .notice-icon-wrapper {
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.25) 0%, rgba(53, 122, 189, 0.2) 100%);
}

.notice-icon-modern {
  font-size: 20px;
  width: 20px;
  height: 20px;
  color: #4A90E2;
}

.notice-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  flex: 1;
}

.notice-time {
  font-size: 13px;
  font-weight: 600;
  color: var(--ios-gray-600);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.notice-badge {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border-radius: var(--radius-sm);
  font-size: 11px;
  font-weight: 700;
  color: white;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  box-shadow: var(--shadow-sm);
}

.notice-title-sidebar {
  margin: 0;
  font-size: 16px;
  font-weight: 800;
  color: var(--graphite);
  letter-spacing: -0.01em;
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-card-sidebar.highlight .notice-title-sidebar {
  font-size: 17px;
}

.notice-content-sidebar {
  color: var(--ios-gray-700);
  line-height: 1.5;
  font-size: 13px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-content-modern :deep(p) {
  margin: 0;
}

.notice-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 8px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.notice-read-more {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: transparent;
  border: 1px solid rgba(28, 28, 30, 0.1);
  border-radius: var(--radius-md);
  font-size: 13px;
  font-weight: 600;
  color: var(--graphite);
  cursor: pointer;
  transition: var(--transition-base);
}

.notice-read-more:hover {
  background: rgba(28, 28, 30, 0.05);
  border-color: rgba(28, 28, 30, 0.2);
  transform: translateX(4px);
}

.notice-read-more:active {
  transform: translateX(2px);
}

.arrow-icon {
  font-size: 16px;
  width: 16px;
  height: 16px;
  transition: transform 0.3s ease;
}

.notice-read-more:hover .arrow-icon {
  transform: translateX(2px);
}

/* 智能推荐气泡 */
.agent-bubble-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
}

.agent-bubble-button {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-full);
  background: var(--graphite);
  box-shadow: 0 8px 24px rgba(28, 28, 30, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--transition-base);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.agent-bubble-button:hover {
  transform: scale(1.04);
  box-shadow: 0 10px 28px rgba(28, 28, 30, 0.3);
}

.agent-bubble-button:active {
  transform: scale(0.95);
}

.agent-bubble-button.active {
  background: var(--ios-gray-700);
}

.agent-bubble-card {
  position: absolute;
  bottom: 80px;
  right: 0;
  width: 420px;
  max-height: 680px;
  background: var(--frosted-bg-light);
  backdrop-filter: blur(var(--blur-lg));
  -webkit-backdrop-filter: blur(var(--blur-lg));
  border: 1px solid var(--frosted-border);
  border-radius: var(--radius-3xl);
  box-shadow: var(--shadow-2xl),
              var(--shadow-inset);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.agent-bubble-header {
  padding: 24px;
  background: var(--graphite);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-sm);
}

.agent-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.agent-icon {
  font-size: 24px;
}

.agent-bubble-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  color: white;
  padding: 0;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.agent-bubble-body {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-height: 570px;
}

.agent-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  min-height: 120px;
  max-height: 350px;
  background: var(--ios-gray-100);
}

.agent-message {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  background: var(--graphite);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(28, 28, 30, 0.2);
}

.message-content {
  flex: 1;
  background: var(--frosted-bg-heavy);
  backdrop-filter: blur(var(--blur-md));
  -webkit-backdrop-filter: blur(var(--blur-md));
  border: 1px solid var(--frosted-border);
  border-radius: var(--radius-md);
  padding: 16px 20px;
  box-shadow: var(--shadow-sm),
              var(--shadow-inset);
}

.message-content p {
  margin: 0;
  line-height: 1.6;
  color: #303133;
}

.agent-input-area {
  padding: 24px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(40px);
  -webkit-backdrop-filter: blur(40px);
}

.agent-input-area :deep(.el-textarea__inner) {
  border-radius: var(--radius-md);
  resize: none;
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: var(--frosted-bg-heavy);
  backdrop-filter: blur(var(--blur-sm));
  -webkit-backdrop-filter: blur(var(--blur-sm));
  padding: 16px;
  font-size: 15px;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.04);
}

.agent-input-area :deep(.el-textarea__inner):focus {
  border-color: var(--graphite);
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.06);
}

.send-btn {
  width: 100%;
  margin-top: 16px;
  height: 48px;
  border-radius: var(--radius-lg);
  background: var(--graphite);
  color: white;
  font-weight: 600;
  font-size: 15px;
  border: none;
  cursor: pointer;
  transition: var(--transition-fast);
  box-shadow: 0 4px 12px rgba(28, 28, 30, 0.2);
}

.send-btn:hover {
  background: var(--graphite-light);
  box-shadow: 0 5px 14px rgba(28, 28, 30, 0.22);
  transform: translateY(-0.5px);
}

.send-btn:active {
  transform: scale(0.98);
}

/* 动画 */
.fade-enter-active,
.fade-leave-active {
  transition: var(--transition-base);
  transform-origin: bottom right;
}

.fade-enter-from {
  opacity: 0;
  transform: scale(0.8) translateY(20px);
}

.fade-leave-to {
  opacity: 0;
  transform: scale(0.8) translateY(20px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-section {
    padding: 0 16px;
    margin-bottom: 40px;
  }

  .banner-container {
    border-radius: 32px;
  }

  .hero-carousel,
  .hero-carousel :deep(.el-carousel__container),
  .banner-wrapper {
    height: 280px;
  }

  .banner-content {
    padding: 0 32px;
    max-width: 100%;
  }

  .banner-badge {
    font-size: 12px;
    padding: 6px 16px;
    margin-bottom: 16px;
  }

  .banner-title {
    font-size: 32px;
    margin-bottom: 12px;
  }

  .banner-subtitle {
    font-size: 16px;
    margin-bottom: 24px;
  }

  .banner-cta {
    padding: 14px 28px;
    font-size: 15px;
  }

  .carousel-indicators {
    bottom: 20px;
  }

  .hero-carousel :deep(.el-carousel__arrow) {
    width: 40px;
    height: 40px;
    background: rgba(255, 255, 255, 0.95);
    color: var(--graphite);
  }

  .hero-carousel :deep(.el-carousel__arrow) i {
    color: var(--graphite);
    font-size: 18px;
  }

  .hero-carousel :deep(.el-carousel__arrow--left) {
    left: 16px;
  }

  .hero-carousel :deep(.el-carousel__arrow--right) {
    right: 16px;
  }

  .section {
    padding: 0 16px;
    margin-top: 40px;
  }

  .section-title {
    font-size: 24px;
  }

  :deep(.el-main) {
    padding-right: 0;
  }

  .notices-sidebar {
    position: relative;
    top: auto;
    right: auto;
    width: 100%;
    max-height: none;
    margin-top: 40px;
  }

  .notices-sidebar-content {
    max-height: 400px;
  }

  .agent-bubble-card {
    width: calc(100vw - 40px);
    right: -10px;
  }

  .hot-products-grid {
    grid-template-columns: 1fr;
    gap: 24px;
    margin-top: 32px;
  }

  .hot-product-name {
    font-size: 18px;
  }

  .hot-product-card.featured .hot-product-name {
    font-size: 20px;
  }

  .hot-product-price {
    font-size: 28px;
  }

  .hot-product-card.featured .hot-product-price {
    font-size: 32px;
  }

  .scroll-nav-btn {
    display: none;
  }
}
</style>
