import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/Home.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue')
    },
    {
      path: '/products',
      name: 'products',
      component: () => import('@/views/Products.vue')
    },
    {
      path: '/product/:id',
      name: 'product-detail',
      component: () => import('@/views/ProductDetail.vue')
    },
    {
      path: '/cart',
      name: 'cart',
      component: () => import('@/views/Cart.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('@/views/Orders.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/Profile.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/notices/manage',
      name: 'notice-manage',
      component: () => import('@/views/NoticeManage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/products/manage',
      name: 'product-manage',
      component: () => import('@/views/ProductManage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/orders/manage',
      name: 'order-manage',
      component: () => import('@/views/OrderManage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/users/manage',
      name: 'user-manage',
      component: () => import('@/views/UserManage.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    next({ name: 'login' })
  } else {
    next()
  }
})

export default router
