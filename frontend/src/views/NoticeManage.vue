<template>
  <div class="notice-manage-page">
    <Header />
    <el-container>
      <el-main>
        <div class="manage-container">
          <div class="page-header">
            <h2>
              <Icon icon="mdi:bullhorn" class="page-icon" />
              公告管理
            </h2>
            <el-button
              type="primary"
              :icon="Plus"
              @click="handleAdd"
            >
              添加公告
            </el-button>
          </div>

          <el-card class="notice-list-card">
            <el-table :data="notices" style="width: 100%" v-loading="loading">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="标题" min-width="200" />
              <el-table-column prop="details" label="内容" min-width="300" show-overflow-tooltip />
              <el-table-column prop="time" label="发布时间" width="180" />
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <el-button
                    type="primary"
                    :icon="Edit"
                    size="small"
                    @click="handleEdit(row)"
                  >
                    编辑
                  </el-button>
                  <el-button
                    type="danger"
                    :icon="Delete"
                    size="small"
                    @click="handleDelete(row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-main>
    </el-container>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="formData.title"
            placeholder="请输入公告标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="内容" prop="details">
          <el-input
            v-model="formData.details"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submitting"
          @click="handleSubmit"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { Icon } from '@iconify/vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import Header from '@/components/Header.vue'
import api from '@/utils/api'

interface Notice {
  id: number
  title: string
  details: string
  time: string
}

const notices = ref<Notice[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const currentNoticeId = ref<number | null>(null)

const formData = ref({
  title: '',
  details: ''
})

const rules: FormRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { max: 100, message: '标题长度不能超过100个字符', trigger: 'blur' }
  ],
  details: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { max: 2000, message: '内容长度不能超过2000个字符', trigger: 'blur' }
  ]
}

const dialogTitle = computed(() => {
  return isEdit.value ? '编辑公告' : '添加公告'
})

const loadNotices = async () => {
  loading.value = true
  try {
    const response = await api.get('/notices')
    notices.value = response.data.data
  } catch (error) {
    console.error('加载公告失败:', error)
    ElMessage.error('加载公告失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  currentNoticeId.value = null
  formData.value = {
    title: '',
    details: ''
  }
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

const handleEdit = (notice: Notice) => {
  isEdit.value = true
  currentNoticeId.value = notice.id
  formData.value = {
    title: notice.title,
    details: notice.details
  }
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (isEdit.value && currentNoticeId.value !== null) {
      // 更新公告
      await api.put(`/notices/${currentNoticeId.value}`, {
        title: formData.value.title,
        details: formData.value.details
      })
      ElMessage.success('公告更新成功')
    } else {
      // 添加公告
      await api.post('/notices', {
        title: formData.value.title,
        details: formData.value.details
      })
      ElMessage.success('公告添加成功')
    }

    dialogVisible.value = false
    await loadNotices()
  } catch (error: any) {
    console.error('保存失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else if (typeof error === 'string') {
      ElMessage.error(error)
    } else {
      ElMessage.error('保存失败，请稍后重试')
    }
  } finally {
    submitting.value = false
  }
}

const handleDelete = (notice: Notice) => {
  ElMessageBox.confirm(
    `确定要删除公告《${notice.title}》吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await api.delete(`/notices/${notice.id}`)
      ElMessage.success('公告删除成功')
      await loadNotices()
    } catch (error: any) {
      console.error('删除失败:', error)
      if (error.response?.data?.message) {
        ElMessage.error(error.response.data.message)
      } else {
        ElMessage.error('删除失败，请稍后重试')
      }
    }
  }).catch(() => {
    // 用户取消
  })
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
.notice-manage-page {
  min-height: 100vh;
  background: var(--ios-gray-100, #F2F2F7);
}

.manage-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
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

.notice-list-card {
  background: var(--frosted-bg-medium, rgba(255, 255, 255, 0.7));
  backdrop-filter: blur(var(--blur-lg, 50px));
  -webkit-backdrop-filter: blur(var(--blur-lg, 50px));
  border: 1px solid var(--frosted-border, rgba(255, 255, 255, 0.6));
  border-radius: var(--radius-2xl, 32px);
  box-shadow: var(--shadow-xl, 0 24px 48px -12px rgba(0, 0, 0, 0.08));
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

:deep(.el-dialog) {
  border-radius: var(--radius-2xl, 32px);
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--graphite, #1C1C1E);
}

@media (max-width: 768px) {
  .manage-container {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .page-header h2 {
    font-size: 24px;
  }
}
</style>

