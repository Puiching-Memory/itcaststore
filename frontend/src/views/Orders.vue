<template>
  <div class="orders-page">
    <Header />
    <el-container>
      <el-main>
        <h2>
          <Icon icon="mdi:file-document-multiple" class="page-icon" />
          我的订单
        </h2>

        <!-- 管理员提示 -->
        <div v-if="isAdmin" class="admin-tip">
          <el-alert
            type="info"
            :closable="false"
            show-icon
          >
            <template #title>
              您是管理员，如果您需要查看所有用户的订单，请使用
              <span class="admin-link" @click="goToOrderManage">订单管理</span>
              页面。
            </template>
          </el-alert>
        </div>

        <el-table :data="orders" v-loading="loading" class="order-table">
          <el-table-column prop="id" label="订单号" min-width="180">
            <template #default="{ row }">
              <span class="order-id">{{ row.id }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="money" label="金额" min-width="100">
            <template #default="{ row }">
              <span class="money">¥{{ row.money?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="receiverName" label="收货人" min-width="100" />
          <el-table-column prop="receiverPhone" label="联系电话" min-width="120" />
          <el-table-column prop="receiverAddress" label="收货地址" min-width="200" show-overflow-tooltip />
          <el-table-column prop="paystate" label="支付状态" min-width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.paystate === 1 ? 'success' : 'warning'" size="small">
                {{ row.paystate === 1 ? '已支付' : '未支付' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ordertime" label="下单时间" min-width="160">
            <template #default="{ row }">
              <span class="order-time">{{ formatTime(row.ordertime) }}</span>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空状态 -->
        <el-empty v-if="!loading && orders.length === 0" description="您还没有订单">
          <el-button type="primary" @click="$router.push('/products')">
            去购物
          </el-button>
        </el-empty>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'
import Header from '@/components/Header.vue'
import { useUserStore } from '@/stores/user'
import api from '@/utils/api'

const router = useRouter()

interface Order {
  id: string
  userId: number
  money: number
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  paystate: number
  ordertime: string
}

const userStore = useUserStore()
const orders = ref<Order[]>([])
const loading = ref(false)

const isAdmin = computed(() => {
  return userStore.user?.role === '超级用户' || userStore.user?.role === '管理员'
})

const loadOrders = async () => {
  loading.value = true
  try {
    const response = await api.get('/orders/list')
    orders.value = response.data.data
  } catch (error) {
    console.error('加载订单失败:', error)
    orders.value = []
  } finally {
    loading.value = false
  }
}

const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const goToOrderManage = () => {
  router.push('/orders/manage')
}

onMounted(async () => {
  // 确保用户信息已加载
  if (userStore.isAuthenticated && !userStore.user) {
    try {
      await userStore.fetchUserInfo()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
  loadOrders()
})
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
}

.page-icon {
  font-size: 24px;
  margin-right: 8px;
  vertical-align: middle;
  color: #409eff;
}

.admin-tip {
  margin-bottom: 20px;
}

.admin-link {
  color: #409eff;
  cursor: pointer;
  text-decoration: underline;
  transition: color 0.2s;
}

.admin-link:hover {
  color: #66b1ff;
}

.order-table {
  width: 100%;
}

.order-id {
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 13px;
}

.money {
  font-weight: 600;
  color: #f56c6c;
}

.order-time {
  font-size: 13px;
  color: #909399;
}
</style>
