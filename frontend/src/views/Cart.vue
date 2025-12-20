<template>
  <div class="cart-page">
    <Header />
    <el-container>
      <el-main>
        <div class="cart-container">
          <div class="cart-header">
            <h2>
              <Icon icon="mdi:cart" class="page-icon" />
              购物车
              <span v-if="cartStore.totalQuantity > 0" class="cart-count">
                ({{ cartStore.totalQuantity }})
              </span>
            </h2>
            <el-button
              v-if="cartStore.items.length > 0"
              type="danger"
              :icon="Delete"
              @click="handleClearCart"
            >
              清空购物车
            </el-button>
          </div>

          <el-empty
            v-if="cartStore.items.length === 0"
            description="购物车是空的"
            :image-size="120"
          >
            <template #image>
              <Icon icon="mdi:cart-off" :width="120" :height="120" style="color: #c0c4cc;" />
            </template>
            <el-button type="primary" @click="$router.push('/products')">
              去购物
            </el-button>
          </el-empty>

          <div v-else class="cart-content">
            <el-card class="cart-card">
              <el-table :data="cartStore.items" style="width: 100%">
                <el-table-column label="商品" width="300">
                  <template #default="{ row }">
                    <div class="product-cell">
                      <img
                        :src="row.imgurl || '/products/product-1.png'"
                        :alt="row.name"
                        class="product-image"
                        @error="handleImageError($event)"
                      />
                      <div class="product-info">
                        <div class="product-name" @click="goToProduct(row.id)">
                          {{ row.name }}
                        </div>
                        <div v-if="row.category" class="product-category">
                          {{ row.category }}
                        </div>
                      </div>
                    </div>
                  </template>
                </el-table-column>

                <el-table-column label="单价" width="120" align="center">
                  <template #default="{ row }">
                    <span class="price-text">¥{{ row.price }}</span>
                  </template>
                </el-table-column>

                <el-table-column label="数量" width="180" align="center">
                  <template #default="{ row }">
                    <div class="quantity-control">
                      <el-button
                        :icon="Minus"
                        circle
                        size="small"
                        @click="decreaseQuantity(row.id)"
                        :disabled="row.quantity <= 1"
                      />
                      <span class="quantity-value">{{ row.quantity }}</span>
                      <el-button
                        :icon="Plus"
                        circle
                        size="small"
                        @click="increaseQuantity(row.id)"
                      />
                    </div>
                  </template>
                </el-table-column>

                <el-table-column label="小计" width="120" align="center">
                  <template #default="{ row }">
                    <span class="subtotal-text">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
                  </template>
                </el-table-column>

                <el-table-column label="操作" width="100" align="center">
                  <template #default="{ row }">
                    <el-button
                      type="danger"
                      :icon="Delete"
                      size="small"
                      circle
                      @click="handleRemoveItem(row.id)"
                    />
                  </template>
                </el-table-column>
              </el-table>

              <div class="cart-footer">
                <div class="cart-summary">
                  <div class="summary-item">
                    <span class="summary-label">商品总数：</span>
                    <span class="summary-value">{{ cartStore.totalQuantity }} 件</span>
                  </div>
                  <div class="summary-item">
                    <span class="summary-label">合计：</span>
                    <span class="summary-total">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
                  </div>
                </div>
                <div class="cart-actions">
                  <el-button
                    size="large"
                    @click="$router.push('/products')"
                  >
                    继续购物
                  </el-button>
                  <el-button
                    type="primary"
                    size="large"
                    @click="handleCheckout"
                  >
                    去结算
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Plus, Minus } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = '/products/product-1.png'
}

const goToProduct = (productId: string) => {
  router.push(`/product/${productId}`)
}

const increaseQuantity = (productId: string) => {
  const item = cartStore.items.find(item => item.id === productId)
  if (item) {
    cartStore.updateQuantity(productId, item.quantity + 1)
  }
}

const decreaseQuantity = (productId: string) => {
  const item = cartStore.items.find(item => item.id === productId)
  if (item && item.quantity > 1) {
    cartStore.updateQuantity(productId, item.quantity - 1)
  }
}

const handleRemoveItem = (productId: string) => {
  const item = cartStore.items.find(item => item.id === productId)
  if (item) {
    ElMessageBox.confirm(
      `确定要从购物车删除《${item.name}》吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      cartStore.removeItem(productId)
    }).catch(() => {
      // 用户取消
    })
  }
}

const handleClearCart = () => {
  ElMessageBox.confirm(
    '确定要清空购物车吗？',
    '确认清空',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    cartStore.clearCart()
  }).catch(() => {
    // 用户取消
  })
}

const handleCheckout = () => {
  if (cartStore.items.length === 0) {
    ElMessage.warning('购物车是空的')
    return
  }
  // TODO: 跳转到结算页面或创建订单
  ElMessage.info('结算功能开发中...')
}
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background: var(--ios-gray-100, #F2F2F7);
}

.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.cart-header h2 {
  margin: 0;
  font-size: 32px;
  font-weight: 800;
  color: var(--graphite, #1C1C1E);
  letter-spacing: -0.02em;
  display: flex;
  align-items: center;
}

.page-icon {
  font-size: 32px;
  margin-right: 12px;
  color: var(--graphite, #1C1C1E);
}

.cart-count {
  font-size: 20px;
  font-weight: 600;
  color: var(--ios-gray-600, #8E8E93);
  margin-left: 8px;
}

.cart-content {
  margin-top: 24px;
}

.cart-card {
  background: var(--frosted-bg-medium, rgba(255, 255, 255, 0.7));
  backdrop-filter: blur(var(--blur-lg, 50px));
  -webkit-backdrop-filter: blur(var(--blur-lg, 50px));
  border: 1px solid var(--frosted-border, rgba(255, 255, 255, 0.6));
  border-radius: var(--radius-2xl, 32px);
  box-shadow: var(--shadow-xl, 0 24px 48px -12px rgba(0, 0, 0, 0.08));
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 16px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: var(--radius-md, 20px);
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--graphite, #1C1C1E);
  cursor: pointer;
  transition: color 0.2s;
}

.product-name:hover {
  color: var(--ios-gray-700, #636366);
}

.product-category {
  font-size: 13px;
  color: var(--ios-gray-600, #8E8E93);
}

.price-text {
  font-size: 16px;
  font-weight: 600;
  color: var(--graphite, #1C1C1E);
}

.quantity-control {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.quantity-value {
  min-width: 40px;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  color: var(--graphite, #1C1C1E);
}

.subtotal-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--graphite, #1C1C1E);
}

.cart-footer {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 24px;
}

.cart-summary {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.summary-label {
  font-size: 16px;
  color: var(--ios-gray-600, #8E8E93);
}

.summary-value {
  font-size: 16px;
  font-weight: 600;
  color: var(--graphite, #1C1C1E);
}

.summary-total {
  font-size: 24px;
  font-weight: 800;
  color: var(--graphite, #1C1C1E);
}

.cart-actions {
  display: flex;
  gap: 12px;
}

:deep(.el-main) {
  padding: 32px 0;
  background: transparent;
}

:deep(.el-table) {
  background: transparent;
}

:deep(.el-table th) {
  background: rgba(255, 255, 255, 0.5);
  color: var(--graphite, #1C1C1E);
  font-weight: 600;
}

:deep(.el-table td) {
  background: transparent;
}

:deep(.el-table tr:hover > td) {
  background: rgba(255, 255, 255, 0.3);
}

@media (max-width: 768px) {
  .cart-container {
    padding: 16px;
  }

  .cart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .cart-header h2 {
    font-size: 24px;
  }

  .product-cell {
    flex-direction: column;
    align-items: flex-start;
  }

  .product-image {
    width: 60px;
    height: 60px;
  }

  .cart-footer {
    flex-direction: column;
    align-items: stretch;
  }

  .cart-actions {
    width: 100%;
    flex-direction: column;
  }

  .cart-actions .el-button {
    width: 100%;
  }
}
</style>
