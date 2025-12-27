/// <reference types="node" />
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// 检测是否在 Docker 环境中运行
const isDocker = process.env.DOCKER_ENV === 'true'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',  // 允许外部访问（Docker 环境需要）
    port: 5173,
    watch: {
      usePolling: true,  // Docker 环境下的文件监听
    },
    proxy: {
      '/api': {
        // 在 Docker 环境中使用服务名 backend，本地开发时使用 localhost
        target: isDocker ? 'http://backend:8080' : 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  }
})
