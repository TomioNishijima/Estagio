import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue({
      template: {
        compilerOptions: {
          isCustomElement: (tag) => tag === 'model-viewer'
        }
      }
    }),

    vueDevTools(),
  ],

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },

  server: {
    host: '0.0.0.0',
    port: 5173,
    strictPort: true,

    allowedHosts: [
      'bookmark-geographic-hamilton-telecommunications.trycloudflare.com'
    ],
    

    
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },

      '/modelos/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }

  }
})