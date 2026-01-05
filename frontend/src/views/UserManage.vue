<template>
  <div class="user-manage-page">
    <Header />
    <el-container>
      <el-main>
        <div v-if="permissionDenied" class="permission-denied">
          <div class="denied-card">
            <Icon icon="mdi:shield-lock" class="denied-icon" />
            <h3>访问被拒绝</h3>
            <p>只有管理员可以访问用户管理页面。</p>
            <el-button type="primary" @click="router.push('/')">返回首页</el-button>
          </div>
        </div>

        <template v-else>
          <div class="page-header">
            <div>
              <div class="title-row">
                <Icon icon="mdi:account-cog" class="page-icon" />
                <div>
                  <h2>用户管理</h2>
                  <p class="subtitle">集中查看、筛选并快速调整用户角色与状态。</p>
                </div>
              </div>
            </div>
            <div class="header-actions">
              <el-input
                v-model="searchKeyword"
                placeholder="按用户名或邮箱搜索"
                clearable
                class="search-input"
                @keyup.enter="loadUsers"
              >
                <template #prefix>
                  <Icon icon="mdi:magnify" />
                </template>
              </el-input>
              <el-button :icon="Search" type="primary" @click="loadUsers">搜索</el-button>
              <el-button :icon="Refresh" @click="handleReset">重置</el-button>
            </div>
          </div>

          <el-card class="frosted-card" shadow="hover">
            <el-table :data="users" v-loading="loading" class="user-table">
              <el-table-column label="ID" prop="id" width="90">
                <template #default="{ row }">
                  <el-tag type="info" size="small">#{{ row.id }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="用户名" min-width="160">
                <template #default="{ row }">
                  <div class="user-cell">
                    <span class="name">{{ row.username }}</span>
                    <div class="secondary">{{ row.email || '未填写邮箱' }}</div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="联系方式" min-width="140">
                <template #default="{ row }">
                  <div class="secondary">{{ row.telephone || '未填写电话' }}</div>
                </template>
              </el-table-column>
              <el-table-column label="角色" min-width="180">
                <template #default="{ row }">
                  <el-select
                    :model-value="row.role"
                    size="small"
                    class="role-select"
                    :loading="updatingRoleId === row.id"
                    :disabled="updatingRoleId === row.id"
                    @change="(value) => handleRoleChange(row, value)"
                  >
                    <el-option label="普通用户" value="普通用户" />
                    <el-option label="管理员" value="管理员" />
                    <el-option label="超级用户" value="超级用户" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="状态" min-width="160" align="center">
                <template #default="{ row }">
                  <div class="state-cell">
                    <el-tag :type="row.state === 1 ? 'success' : 'warning'" size="small">
                      {{ row.state === 1 ? '已激活' : '未激活' }}
                    </el-tag>
                    <el-switch
                      :model-value="row.state"
                      :active-value="1"
                      :inactive-value="0"
                      :loading="updatingStateId === row.id"
                      @change="(value) => handleStateChange(row, value)"
                    />
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="注册时间" prop="registTime" min-width="200">
                <template #default="{ row }">
                  <span class="secondary">{{ formatTime(row.registTime) }}</span>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next"
                :total="total"
                @size-change="loadUsers"
                @current-change="loadUsers"
              />
            </div>
          </el-card>
        </template>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'
import { ElMessage } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import api from '@/utils/api'
import { useUserStore } from '@/stores/user'

interface ManagedUser {
  id: number
  username: string
  email?: string
  telephone?: string
  gender?: string
  role: string
  state: number
  registTime?: string
}

const router = useRouter()
const userStore = useUserStore()

const users = ref<ManagedUser[]>([])
const loading = ref(false)
const permissionDenied = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const updatingRoleId = ref<number | null>(null)
const updatingStateId = ref<number | null>(null)

const isAdmin = computed(() => {
  return userStore.user?.role === '超级用户' || userStore.user?.role === '管理员'
})

const loadUsers = async () => {
  if (!isAdmin.value) {
    permissionDenied.value = true
    return
  }

  loading.value = true
  try {
    const response = await api.get('/users/admin', {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value,
        keyword: searchKeyword.value || undefined
      }
    })
    users.value = response.data.data.content || []
    total.value = response.data.data.totalElements || 0
  } catch (error: any) {
    console.error('加载用户列表失败:', error)
    if (error.response?.status === 403) {
      permissionDenied.value = true
    }
    users.value = []
  } finally {
    loading.value = false
  }
}

const handleRoleChange = async (user: ManagedUser, value: string) => {
  const previousRole = user.role
  user.role = value
  updatingRoleId.value = user.id
  try {
    await api.put(`/users/admin/${user.id}/role`, { role: value })
    ElMessage.success('角色已更新')
  } catch (error: any) {
    console.error('更新角色失败:', error)
    user.role = previousRole
    ElMessage.error(error.response?.data?.message || '更新角色失败')
  } finally {
    updatingRoleId.value = null
  }
}

const handleStateChange = async (user: ManagedUser, value: number) => {
  const previousState = user.state
  user.state = value
  updatingStateId.value = user.id
  try {
    await api.put(`/users/admin/${user.id}/state`, { state: value })
    ElMessage.success('状态已更新')
  } catch (error: any) {
    console.error('更新状态失败:', error)
    user.state = previousState
    ElMessage.error(error.response?.data?.message || '更新状态失败')
  } finally {
    updatingStateId.value = null
  }
}

const handleReset = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  loadUsers()
}

const formatTime = (time?: string) => {
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
  if (userStore.isAuthenticated && !userStore.user) {
    try {
      await userStore.fetchUserInfo()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
  loadUsers()
})
</script>

<style scoped>
.user-manage-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7ff 0%, #fef5f5 100%);
}

.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 20px;
  gap: 16px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-icon {
  font-size: 32px;
  color: #1f2937;
}

.page-header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 800;
  color: #111827;
}

.subtitle {
  margin: 4px 0 0;
  color: #6b7280;
  font-size: 14px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  width: 260px;
}

.frosted-card {
  border-radius: 24px;
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.08);
}

.user-table :deep(.el-table__header th) {
  background: rgba(17, 24, 39, 0.04);
  font-weight: 700;
}

.user-table :deep(.el-table__cell) {
  padding: 12px 10px;
}

.user-cell .name {
  font-weight: 700;
  color: #111827;
}

.secondary {
  color: #6b7280;
  font-size: 13px;
}

.role-select {
  width: 140px;
}

.state-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.permission-denied {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.denied-card {
  text-align: center;
  padding: 32px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
}

.denied-icon {
  font-size: 48px;
  color: #f97316;
  margin-bottom: 12px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .search-input {
    flex: 1;
    min-width: 200px;
  }
}
</style>
