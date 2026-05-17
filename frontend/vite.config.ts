import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
      '@modules': resolve(__dirname, '../modules')
    }
  },
  server: {
    port: 5173,
    host: '0.0.0.0',
    proxy: {
      '/v1': {
        target: 'http://localhost:8888',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/v1/, '/api/v1')
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false,
    rollupOptions: {
      external: ['element-plus', '@element-plus/icons-vue']
    }
  }
})
