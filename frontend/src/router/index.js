import { createRouter, createWebHistory } from 'vue-router'
import Catalogo3D from '../components/Catalogo3d.vue'
import Dashboard from '../components/Dashboard.vue'
import GerenciarMateriais from '../components/GerenciarMateriais.vue'
import Login from '../components/Login.vue'
import GerenciarOrcamentos from '../components/GerenciarOrcamentos.vue'
import Cadastro from '../components/Cadastro.vue'

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
    },

    {
      path: '/cadastro',
      name: 'Cadastro',
      component: Cadastro
    },

    {
      path: '/catalogo',
      name: 'catalogo',
      component: Catalogo3D
    }
  ]

})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')

  const paginasPublicas = ['/login', '/cadastro']

  // Sem login: permite apenas páginas públicas
  if (!token && !paginasPublicas.includes(to.path)) {
    return '/login'
  }

  // Já logado: não precisa voltar ao login
  if (token && paginasPublicas.includes(to.path)) {
    return '/dashboard'
  }
})

export default router