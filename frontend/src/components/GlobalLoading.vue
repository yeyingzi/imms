<template>
  <Teleport to="body">
    <Transition name="loading-fade">
      <div v-if="loadingStore.loading" class="global-loading">
        <div class="loading-spinner">
          <div class="spinner-ring"></div>
          <p class="loading-text">加载中...</p>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { useLoadingStore } from '@/stores/loading'

const loadingStore = useLoadingStore()
</script>

<style scoped>
.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-spinner {
  text-align: center;
}

.spinner-ring {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(255, 255, 255, 0.1);
  border-top-color: #409eff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

.loading-text {
  color: #fff;
  font-size: 14px;
  margin: 0;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-fade-enter-active,
.loading-fade-leave-active {
  transition: opacity 0.3s ease;
}

.loading-fade-enter-from,
.loading-fade-leave-to {
  opacity: 0;
}
</style>
