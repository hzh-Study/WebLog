<template>
  <el-config-provider :locale="locale">
    <div :class="['app-shell', appClass]">
      <router-view></router-view>
    </div>
  </el-config-provider>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

import zhCn from 'element-plus/es/locale/lang/zh-cn'
let locale = zhCn
const route = useRoute()

const appClass = computed(() => {
  if (route.path === '/login') return 'is-admin-login'
  if (route.path.startsWith('/admin')) return 'is-admin-route'
  return 'is-frontend-route'
})

</script>

<style>
body {
  font-family: -apple-system-font, BlinkMacSystemFont, "Helvetica Neue", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei UI", "Microsoft YaHei", Arial, sans-serif;
  color: #172033;
  font-size: 16px;
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-rendering: optimizeLegibility;
}

#app {
  min-height: 100vh;
}

#nprogress .bar {
   background: linear-gradient(90deg, #2563eb 0%, #6366f1 100%)!important;
}
</style>
