
<template>
  <div class="login-container">

    <div class="login-card">

      <!-- LADO ESQUERDO: IDENTIDADE DA MARCENARIA -->
      <section class="brand-section">

        <div class="brand-content">
          <div class="brand-symbol">西嶋</div>

          <h1>Marcenaria<br />Nishijima</h1>

          <div class="brand-divider"></div>

          <p class="brand-description">
            Sistema de gestão de marcenaria
          </p>

          <p class="brand-details">
            Gerencie sua marcenaria em um só lugar ;)
          </p>
        </div>

        <span class="brand-footer">
          MARCENARIA NISHIJIMA © 2026
        </span>
      </section>

      <!-- LADO DIREITO: FORMULÁRIO -->
      <section class="form-section">

        <div class="form-content">

          <div class="form-heading">
            <span class="form-eyebrow">Login</span>
            <h2>Bem-vindo!</h2>
            <p>Entre com suas credenciais para continuar.</p>
          </div>

          <form @submit.prevent="entrar">

            <div class="field-group">
              <label for="email">
                E-mail <span class="obrigatorio" aria-hidden="true">*</span>
              </label>

              <input
                id="email"
                v-model.trim="email"
                type="email"
                placeholder="Digite seu e-mail"
                autocomplete="email"
                :disabled="carregando"
                required
              />
            </div>

            <div class="field-group">
              <label for="senha">
                Senha <span class="obrigatorio" aria-hidden="true">*</span>
              </label>

              <input
                id="senha"
                v-model="senha"
                type="password"
                placeholder="Digite sua senha"
                autocomplete="current-password"
                :disabled="carregando"
                required
              />
            </div>

            <p
              v-if="erro"
              class="erro"
              role="alert"
            >
              {{ erro }}
            </p>

            <button
              type="submit"
              class="btn-entrar"
              :disabled="carregando"
            >
              {{ carregando ? 'Entrando...' : 'Entrar' }}
            </button>
            <p class="campos-obrigatorios">
              <span class="obrigatorio">*</span>
              Campos obrigatórios
            </p>

          </form>

          <div class="cadastro-area">
            <p>Não possui uma conta?</p>

            <router-link to="/cadastro" class="cadastro-link">
              Cadastre-se
            </router-link>
          </div>

        </div>

      </section>

    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const email = ref('')
const senha = ref('')
const erro = ref('')
const carregando = ref(false)

async function entrar() {
  if (carregando.value) return

  erro.value = ''

  if (!email.value.trim() || !senha.value) {
    erro.value = 'Preencha o e-mail e a senha.'
    return
  }

  carregando.value = true

  try {
    const response = await axios.post(
      'http://localhost:8080/api/auth/login',
      {
        email: email.value.trim(),
        senha: senha.value
      }
    )

    const token = response.data.token

    if (!token) {
      throw new Error('Token não recebido do servidor.')
    }

    localStorage.setItem('token', token)

    localStorage.setItem(
      'usuario',
      JSON.stringify({
        id: response.data.id,
        nome: response.data.nome,
        email: response.data.email
      })
    )

    await router.push('/dashboard')

  } catch (error) {
    console.error('ERRO NO LOGIN:', error)

    if (error.response?.status === 401 ||
        error.response?.status === 403) {
      erro.value = 'E-mail ou senha incorretos.'
    } else if (!error.response) {
      erro.value = error.message === 'Token não recebido do servidor.'
        ? error.message
        : 'Não foi possível conectar ao servidor.'
    } else {
      erro.value = 'Não foi possível realizar o login. Tente novamente.'
    }

  } finally {
    carregando.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Lexend:wght@300;400;500;600;700&display=swap');

* {
  box-sizing: border-box;
  font-family: 'Lexend', sans-serif;
}

.login-container {
  min-height: 100vh;
  min-height: 100dvh;
  width: 100%;
  padding: 30px;

  display: flex;
  justify-content: center;
  align-items: center;

  background: #1a1a1a;
}

.login-card {
  display: grid;
  grid-template-columns: 1fr 1fr;

  width: 100%;
  max-width: 1050px;
  min-height: 590px;

  border-radius: 12px;
  overflow: hidden;

  background: #fff;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.35);
}

/* LADO ESQUERDO */

.brand-section {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  padding: 55px 45px;
  background: #611811;
  color: #fff;
}

.brand-content {
  margin: auto 0;
}

.brand-symbol {
  font-size: 42px;
  font-weight: 600;
  letter-spacing: 6px;
  margin-bottom: 35px;
}

.brand-label {
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 3px;
  color: #e6c5c1;
}

.brand-section h1 {
  margin: 15px 0 25px;

  font-size: clamp(32px, 3.5vw, 44px);
  font-weight: 600;
  line-height: 1.2;
  letter-spacing: -1px;
}

.brand-divider {
  width: 60px;
  height: 3px;
  background: #d8aaa4;
  margin-bottom: 25px;
}

.brand-description {
  max-width: 330px;

  font-size: 17px;
  font-weight: 400;
  line-height: 1.7;
}

.brand-details {
  max-width: 340px;
  margin-top: 18px;

  font-size: 13px;
  font-weight: 300;
  line-height: 1.8;
  color: #edd8d5;
}

.brand-footer {
  font-size: 10px;
  letter-spacing: 2px;
  color: #d8b3ae;
}

/* LADO DIREITO */

.form-section {
  display: flex;
  justify-content: center;
  align-items: center;

  padding: 55px 50px;
  background: #fff;
}

.form-content {
  width: 100%;
  max-width: 360px;
}

.form-heading {
  margin-bottom: 35px;
}

.form-eyebrow {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 2px;
  color: #611811;
}

.form-heading h2 {
  margin: 12px 0 8px;

  font-size: 32px;
  font-weight: 600;
  color: #252525;
}

.form-heading p {
  font-size: 13px;
  line-height: 1.6;
  color: #777;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 9px;
  margin-bottom: 22px;
}

.field-group label {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.field-group input {
  width: 100%;
  height: 48px;
  padding: 0 15px;

  border: 1px solid #ddd;
  border-radius: 6px;
  outline: none;

  font-size: 13px;
  color: #333;
  background: #fafafa;

  transition: border-color 0.2s, box-shadow 0.2s;
}

.field-group input:focus {
  border-color: #611811;
  box-shadow: 0 0 0 3px rgba(97, 24, 17, 0.1);
  background: #fff;
}

.field-group input::placeholder {
  color: #aaa;
}

/* BOTÃO */

.btn-entrar {
  width: 100%;
  height: 48px;
  margin-top: 5px;

  border: none;
  border-radius: 6px;

  background: #611811;
  color: #fff;

  font-size: 14px;
  font-weight: 600;
  cursor: pointer;

  transition: background 0.2s, transform 0.2s;
}

.btn-entrar:hover:not(:disabled) {
  background: #7d251b;
  transform: translateY(-1px);
}

.btn-entrar:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

/* CADASTRO */

.cadastro-area {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 5px;

  margin-top: 30px;
  padding-top: 25px;
  border-top: 1px solid #eee;
}

.cadastro-area p {
  font-size: 12px;
  color: #777;
  margin: 0;
}

.cadastro-link {
  font-size: 12px;
  font-weight: 600;
  color: #611811;
  text-decoration: none;
}

.cadastro-link:hover {
  text-decoration: underline;
}

/* MENSAGEM DE ERRO */

.erro {
  padding: 12px;
  margin: 0 0 15px;

  background: #fff1f0;
  border: 1px solid #f0c2bd;
  border-radius: 6px;

  color: #a52a20;
  font-size: 12px;
  line-height: 1.5;
  text-align: center;
}

/* RESPONSIVIDADE */

@media (max-width: 800px) {
  .login-card {
    grid-template-columns: 1fr;
    max-width: 500px;
  }

  .brand-section {
    padding: 28px 30px;
  }

  .brand-content {
    margin: 0;
  }

  .brand-symbol {
    font-size: 30px;
    margin-bottom: 12px;
  }

  .brand-section h1 {
    margin: 12px 0;
    font-size: 28px;
  }

  .brand-divider,
  .brand-description,
  .brand-details,
  .brand-footer {
    display: none;
  }

  .form-section {
    padding: 35px 30px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 15px;
  }

  .form-section {
    padding: 30px 22px;
  }

  .form-heading h2 {
    font-size: 27px;
  }
}
.obrigatorio {
  color: #d62828;
  font-weight: 700;
}

.campos-obrigatorios {
  font-size: 12px;
  color: #777;
  margin: 10px 0;
}
</style>
