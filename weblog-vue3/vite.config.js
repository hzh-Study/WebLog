import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import WindiCSS from 'vite-plugin-windicss'
import path from 'node:path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
// dev：后端 application-dev 为 8081、无 context-path，故 /api 需 rewrite 后转发到 8081 根路径。
// preview：联调「打包后的 prod 后端」时用 8080 + context-path=/api（与 application-prod 一致），不要 strip 前缀。
const apiProxyDev = {
  '/api': {
    target: 'http://127.0.0.1:8081',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, ''),
  },
}
const apiProxyPreview = {
  '/api': {
    target: 'http://127.0.0.1:8080',
    changeOrigin: true,
  },
}

export default defineConfig({
  server: {
    proxy: apiProxyDev,
  },
  // 本地验证生产包：先 java -jar ... --spring.profiles.active=prod，再 pnpm build && pnpm preview
  preview: {
    port: 4173,
    proxy: apiProxyPreview,
  },
  plugins: [
    vue(), 
    WindiCSS(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      // 指定 src 文件夹别名，以便快速定位到此文件夹
      // "~": path.resolve(__dirname, "src")
    }
  }
})
