import axios from 'axios'

const api = axios.create({
  baseURL: '/'
})

api.interceptors.request.use(
  (config) => {

    const token = localStorage.getItem('token')

    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (resposta) => resposta,

  (erro) => {
    const status = erro.response?.status
    const rota = erro.config?.url || ''

    // Não interferir no login ou cadastro
    const rotaPublica = rota.includes('/api/auth/')

    if (
      (status === 401 || status === 403) &&
      !rotaPublica &&
      localStorage.getItem('token')
    ) {
      // Limpa a sessão inválida
      localStorage.removeItem('token')
      localStorage.removeItem('usuario')

      // Redireciona para o login
      if (window.location.pathname !== '/login') {
        window.location.replace('/login')
      }
    }

    return Promise.reject(erro)
  }
)

export default api