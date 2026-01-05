<template>
  <div class="order-manage-page">
    <Header />
    <el-container>
      <el-main>
        <!-- 权限拒绝页面 -->
        <div v-if="permissionDenied" class="permission-denied">
          <div class="denied-content">
            <Icon icon="mdi:shield-alert" class="denied-icon" />
            <h3>访问被拒绝</h3>
            <p>抱歉，您没有权限访问订单管理页面。</p>
            <p class="hint">只有管理员账户才能查看和管理所有订单。</p>
            <el-button type="primary" @click="router.push('/')">
              返回首页
            </el-button>
          </div>
        </div>

        <!-- 订单管理页面 -->
        <template v-else>
          <div class="page-header">
            <h2>
              <Icon icon="mdi:receipt" class="page-icon" />
              订单管理
            </h2>
            <div class="header-actions">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索订单号或收货人..."
                class="search-input"
                clearable
              >
                <template #prefix>
                  <Icon icon="mdi:magnify" />
                </template>
              </el-input>
            </div>
          </div>
          <el-table :data="paginatedOrders" v-loading="loading" class="order-table">
          <el-table-column prop="id" label="订单号" min-width="180">
            <template #default="{ row }">
              <span class="order-id">{{ row.id }}</span>
            </template>
          </el-table-column>
          <el-table-column label="用户信息" min-width="120">
            <template #default="{ row }">
              <div class="user-info">
                <span class="user-id">用户ID: {{ row.userId }}</span>
              </div>
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
          <el-table-column label="操作" min-width="100" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="viewDetail(row)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadOrders"
            @current-change="loadOrders"
          />
        </div>
        </template>
      </el-main>
    </el-container>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="600px"
      class="order-detail-dialog"
    >
      <div v-if="selectedOrder" class="order-detail">
        <div class="detail-section">
          <h4>基本信息</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="订单号">{{ selectedOrder.id }}</el-descriptions-item>
            <el-descriptions-item label="用户ID">{{ selectedOrder.userId }}</el-descriptions-item>
            <el-descriptions-item label="订单金额">¥{{ selectedOrder.money?.toFixed(2) }}</el-descriptions-item>
            <el-descriptions-item label="支付状态">
              <el-tag :type="selectedOrder.paystate === 1 ? 'success' : 'warning'" size="small">
                {{ selectedOrder.paystate === 1 ? '已支付' : '未支付' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="下单时间" :span="2">
              {{ formatTime(selectedOrder.ordertime) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h4>收货信息</h4>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="收货人">{{ selectedOrder.receiverName }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ selectedOrder.receiverPhone }}</el-descriptions-item>
            <el-descriptions-item label="收货地址">{{ selectedOrder.receiverAddress }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h4>订单商品</h4>
          <el-table :data="selectedOrder.orderItems || []" size="small" border>
            <el-table-column prop="product.id" label="商品ID" width="100" />
            <el-table-column prop="product.name" label="商品名称" />
            <el-table-column prop="buynum" label="数量" width="80" align="center" />
            <el-table-column label="单价" width="100">
              <template #default="{ row }">
                ¥{{ row.product?.price?.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column label="小计" width="100">
              <template #default="{ row }">
                ¥{{ (row.product?.price * row.buynum)?.toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'
import Header from '@/components/Header.vue'
import { useUserStore } from '@/stores/user'
import api from '@/utils/api'

interface Order {
  id: string
  userId: number
  money: number
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  paystate: number
  ordertime: string
  orderItems?: OrderItem[]
}

interface OrderItem {
  id: { orderId: string; productId: string }
  product: {
    id: string
    name: string
    price: number
  }
  buynum: number
}

const router = useRouter()
const userStore = useUserStore()
const orders = ref<Order[]>([])
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailDialogVisible = ref(false)
const selectedOrder = ref<Order | null>(null)
const permissionDenied = ref(false)

// 使用普通 ref 存储筛选后的数据，避免计算属性被写入的问题
// const filteredOrdersData = ref<Order[]>([])

const isAdmin = computed(() => {
  return userStore.user?.role === '超级用户' || userStore.user?.role === '管理员'
})

// 筛选逻辑：从所有订单中筛选匹配的订单
const filterOrders = (ordersList: Order[]) => {
  if (!searchKeyword.value) return [...ordersList] // 使用展开运算符创建新数组，避免直接返回原数组引用
  
  const keyword = searchKeyword.value.toLowerCase()
  return ordersList.filter(order => 
    order.id?.toLowerCase().includes(keyword) ||
    order.receiverName?.toLowerCase().includes(keyword) ||
    order.receiverPhone?.includes(keyword)
  )
}

// 计算分页后的数据
const paginatedOrders = computed(() => {
  const filtered = filterOrders(orders.value)
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

const loadOrders = async () => {
  // 检查管理员权限
  if (!isAdmin.value) {
    permissionDenied.value = true
    return
  }
  
  loading.value = true
  try {
    const response = await api.get('/orders/admin/all', {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value
      }
    })
    orders.value = response.data.data.content
    total.value = response.data.data.totalElements
  } catch (error: any) {
    console.error('加载订单列表失败:', error)
    if (error.response?.status === 403 || error.response?.data?.message?.includes('权限')) {
      permissionDenied.value = true
    }
    orders.value = []
  } finally {
    loading.value = false
  }
}

const viewDetail = (order: Order) => {
  selectedOrder.value = order
  detailDialogVisible.value = true
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

onMounted(async () => {
  // 确保用户信息已加载
  if (userStore.isAuthenticated && !userStore.user) {
    try {
      await userStore.fetchUserInfo()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
  
  // 延迟加载订单，确保用户信息已就绪
  setTimeout(() => {
    loadOrders()
  }, 100)
})
</script>

<style scoped>
.order-manage-page {
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-icon {
  font-size: 24px;
  margin-right: 8px;
  vertical-align: middle;
  color: #409eff;
}

.page-header h2 {
  margin: 0;
  display: flex;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.search-input {
  width: 280px;
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 15px;
}

/* 权限拒绝页面样式 */
.permission-denied {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.denied-content {
  text-align: center;
  padding: 48px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.denied-icon {
  font-size: 64px;
  color: #f56c6c;
  margin-bottom: 24px;
}

.denied-content h3 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 24px;
}

.denied-content p {
  margin: 0 0 8px 0;
  color: #606266;
  font-size: 15px;
}

.denied-content .hint {
  color: #909399;
  font-size: 13px;
  margin-bottom: 24px;
}
</style>

