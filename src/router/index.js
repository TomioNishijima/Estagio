import { createRouter, createWebHistory } from 'vue-router'

import Dashboard from '../components/Dashboard.vue'
import GerenciarMateriais from '../components/GerenciarMateriais.vue'
import Login from '../components/Login.vue'
import GerenciarOrcamentos from '../components/GerenciarOrcamentos.vue'

const router = createRouter({

  history: createWebHistory(import.meta.env.BASE_URL),

  routes: [

    {
      path: '/',
      redirect: '/login'
    },

    {
      path: '/login',
      name: 'Login',
      component: Login
    },

    {
      path: '/dashboard',
      name: 'Dashboard',
      component: Dashboard
    },

    {
      path: '/materiais',
      name: 'GerenciarMateriais',
      component: GerenciarMateriais
    },

    {
      path: '/orcamentos',
      name: 'GerenciarOrcamentos',
      component: GerenciarOrcamentos
    }

  ]

})

export default router