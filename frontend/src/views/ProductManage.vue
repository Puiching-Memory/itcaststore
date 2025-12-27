<template>
  <div class="product-manage-page">
    <Header />
    <el-container>
      <el-main>
        <div class="manage-container">
          <div class="page-header">
            <h2>
              <Icon icon="mdi:store" class="page-icon" />
              商品管理
            </h2>
            <el-button
              type="primary"
              :icon="Plus"
              @click="handleAdd"
            >
              添加商品
            </el-button>
          </div>
          
          <el-card class="product-list-card">
            <el-empty v-if="!loading && products.length === 0" description="暂无商品数据" />
            <el-table v-else :data="products" style="width: 100%" v-loading="loading">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="name" label="商品名称" min-width="200" />
              <el-table-column prop="price" label="价格" width="120">
                <template #default="{ row }">
                  ¥{{ row.price.toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column prop="category" label="分类" width="150" />
              <el-table-column prop="pnum" label="库存" width="100" />
              <el-table-column label="图片预览" width="120">
                <template #default="{ row }">
                  <img 
                    v-if="row.imgurl" 
                    :src="row.imgurl" 
                    :alt="row.name"
                    style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px;"
                    @error="handleImageError"
                  />
                  <span v-else class="no-image">无图片</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <el-button
                    type="primary"
                    size="small"
                    :icon="Edit"
                    @click="handleEdit(row)"
                  >
                    编辑
                  </el-button>
                  <el-button
                    type="danger"
                    size="small"
                    :icon="Delete"
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
        label-width="100px"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入商品名称"
            maxlength="40"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="formData.price"
            :min="0.01"
            :precision="2"
            :step="0.01"
            placeholder="请输入价格"
          />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select
            v-model="formData.category"
            placeholder="请选择分类"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="category in categories"
              :key="category"
              :label="category"
              :value="category"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="库存数量" prop="pnum">
          <el-input-number
            v-model="formData.pnum"
            :min="0"
            placeholder="请输入库存数量"
          />
        </el-form-item>
        <el-form-item label="图片URL" prop="imgurl">
          <el-input
            v-model="formData.imgurl"
            placeholder="请输入图片URL（可选）"
            maxlength="255"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入商品描述（可选）"
            maxlength="1000"
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

interface Product {
  id: string
  name: string
  price: number
  category?: string
  pnum?: number  // 库存数量，与后端字段名一致
  imgurl?: string
  description?: string
}

const products = ref<Product[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const currentProductId = ref<string | null>(null)

const formData = ref({
  name: '',
  price: 0,
  category: '',
  pnum: 0,  // 使用pnum与后端字段名一致
  imgurl: '',
  description: ''
})

// 分类列表（20个分类）
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

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { max: 40, message: '商品名称长度不能超过40个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { 
      validator: (_rule: any, value: number, callback: Function) => {
        if (value === null || value === undefined || value === 0) {
          callback(new Error('请输入价格'))
        } else if (value < 0.01) {
          callback(new Error('价格必须大于0.01'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  pnum: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存不能为负数', trigger: 'blur' }
  ],
  category: [
    { max: 40, message: '分类长度不能超过40个字符', trigger: 'blur' }
  ],
  imgurl: [
    { max: 255, message: '图片URL长度不能超过255个字符', trigger: 'blur' }
  ],
  description: [
    { max: 1000, message: '商品描述长度不能超过1000个字符', trigger: 'blur' }
  ]
}

const dialogTitle = computed(() => {
  return isEdit.value ? '编辑商品' : '添加商品'
})

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await api.get('/products', {
      params: {
        page: 0,
        size: 1000  // 获取所有商品，管理页面不需要分页
      }
    })
    // 处理分页响应：可能是 { content: [...] } 或直接是数组
    const data = response.data.data
    products.value = Array.isArray(data) ? data : (data?.content || [])
  } catch (error) {
    console.error('加载商品失败:', error)
    ElMessage.error('加载商品失败，请稍后再试')
    products.value = []
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  currentProductId.value = null
  formData.value = {
    name: '',
    price: 0,
    category: '',
    pnum: 0,
    imgurl: '',
    description: ''
  }
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

const handleEdit = (product: Product) => {
  isEdit.value = true
  currentProductId.value = product.id
  formData.value = {
    name: product.name,
    price: product.price,
    category: product.category || '',
    pnum: product.pnum || 0,
    imgurl: product.imgurl || '',
    description: product.description || ''
  }
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

const handleDelete = (product: Product) => {
  ElMessageBox.confirm(
    `确定要删除商品《${product.name}》吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await api.delete(`/products/${product.id}`)
      ElMessage.success('商品删除成功')
      await loadProducts()
    } catch (error: any) {
      console.error('删除失败:', error)
      if (error.response?.data?.message) {
        ElMessage.error(error.response.data.message)
      } else {
        ElMessage.error('删除失败，请稍后重试')
      }
    }
  }).catch(() => {
    // 用户取消删除，不需要处理
  })
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = '/products/product-1.png'
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    // 准备提交数据，确保字段名与后端匹配
    const submitData = {
      name: formData.value.name,
      price: formData.value.price,
      category: formData.value.category || null,
      pnum: formData.value.pnum,
      imgurl: formData.value.imgurl || null,
      description: formData.value.description || null
    }

    if (isEdit.value && currentProductId.value !== null) {
      // 编辑商品
      await api.put(`/products/${currentProductId.value}`, submitData)
      ElMessage.success('商品更新成功')
    } else {
      // 添加商品
      await api.post('/products', submitData)
      ElMessage.success('商品添加成功')
    }

    dialogVisible.value = false
    await loadProducts()
  } catch (error: any) {
    console.error('保存失败:', error)
    // 处理验证错误
    if (error.response?.status === 400 && error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else if (error.response?.data?.message) {
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

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-manage-page {
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

.product-list-card {
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .manage-container {
    padding: 24px;
  }
  
  .page-header h2 {
    font-size: 28px;
  }
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

  .page-header .el-button {
    width: 100%;
  }

  :deep(.el-dialog) {
    width: 95% !important;
    margin: 0 auto;
  }

  :deep(.el-form-item__label) {
    font-size: 14px;
  }

  :deep(.el-table) {
    font-size: 12px;
  }

  :deep(.el-table .cell) {
    line-height: 1.3;
  }

  :deep(.el-button) {
    padding: 8px 12px;
    font-size: 12px;
  }

  :deep(.el-input-number) {
    width: 100%;
  }
}

.no-image {
  color: #8E8E93;
  font-size: 12px;
}

@media (max-width: 480px) {
  .manage-container {
    padding: 12px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  :deep(.el-table) {
    overflow-x: auto;
  }

  :deep(.el-table__header-wrapper) {
    min-width: 600px;
  }

  :deep(.el-table__body-wrapper) {
    min-width: 600px;
  }
}
</style>