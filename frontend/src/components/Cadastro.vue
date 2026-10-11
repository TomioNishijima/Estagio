
<template>
  <div class="cadastro-container">

    <div class="cadastro-card">

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
            <span class="form-eyebrow">CADASTRO</span>

            <h2>Crie sua conta</h2>

            <p>
              Preencha seus dados para acessar o sistema.
            </p>
          </div>

          <form @submit.prevent="cadastrar">

            <!-- NOME -->
            <div class="field-group">
              <label for="nome">
                Nome completo <span class="obrigatorio" aria-hidden="true">*</span>
              </label>

              <input
                id="nome"
                v-model.trim="nome"
                type="text"
                placeholder="Digite seu nome"
                autocomplete="name"
                :disabled="carregando"
                required
              />
            </div>

            <!-- EMAIL -->
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

            <!-- SENHA -->
            <div class="field-group">
              <label for="senha">
                Senha <span class="obrigatorio" aria-hidden="true">*</span>
              </label>

              <input
                id="senha"
                v-model="senha"
                type="password"
                placeholder="Crie uma senha"
                autocomplete="new-password"
                minlength="6"
                :disabled="carregando"
                required
              />
            </div>

            <!-- CONFIRMAR SENHA -->
            <div class="field-group">
              <label for="confirmarSenha">
                Confirmar senha <span class="obrigatorio" aria-hidden="true">*</span>
              </label>

              <input
                id="confirmarSenha"
                v-model="confirmarSenha"
                type="password"
                placeholder="Digite a senha novamente"
                autocomplete="new-password"
                minlength="6"
                :disabled="carregando"
                required
              />
            </div>

            <!-- MENSAGENS -->
            <p
              v-if="mensagem"
              :class="[
                'mensagem',
                sucesso ? 'mensagem-sucesso' : 'mensagem-erro'
              ]"
              role="status"
            >
              {{ mensagem }}
            </p>

            <!-- BOTÃO -->
            <button
              type="submit"
              class="btn-cadastrar"
              :disabled="carregando"
            >
              {{ carregando ? 'Cadastrando...' : 'Criar conta' }}
            </button>

          </form>

          <!-- LINK PARA LOGIN -->
          <div class="login-area">
            <p>Já possui uma conta?</p>

            <router-link
              to="/login"
              class="login-link"
            >
              Entrar
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
import api from '../services/api'

const router = useRouter()

const nome = ref('')
const email = ref('')
const senha = ref('')
const confirmarSenha = ref('')

const mensagem = ref('')
const sucesso = ref(false)
const carregando = ref(false)

async function cadastrar() {
  if (carregando.value) return

  mensagem.value = ''
  sucesso.value = false

  if (
    !nome.value.trim() ||
    !email.value.trim() ||
    !senha.value ||
    !confirmarSenha.value
  ) {
    mensagem.value = 'Preencha todos os campos.'
    return
  }

  if (senha.value.length < 6) {
    mensagem.value = 'A senha deve ter pelo menos 6 caracteres.'
    return
  }

  if (senha.value !== confirmarSenha.value) {
    mensagem.value = 'As senhas não coincidem.'
    return
  }

  carregando.value = true

  try {
    await api.post(
      '/api/auth/registrar',
      {
        nome: nome.value.trim(),
        email: email.value.trim(),
        senha: senha.value
      }
    )

    sucesso.value = true
    mensagem.value = 'Cadastro realizado com sucesso! Redirecionando para o login...'

    await new Promise(resolve => setTimeout(resolve, 1000))
    await router.push('/login')

  } catch (error) {
    console.error('ERRO AO CADASTRAR:', error)

    sucesso.value = false

    const dados = error.response?.data

    if (error.response?.status === 409) {
      mensagem.value = 'Este e-mail já está cadastrado.'
    } else if (typeof dados === 'string' && dados.trim()) {
      mensagem.value = dados
    } else if (typeof dados?.message === 'string') {
      mensagem.value = dados.message
    } else if (!error.response) {
      mensagem.value = 'Não foi possível conectar ao servidor.'
    } else {
      mensagem.value = 'Não foi possível realizar o cadastro.'
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

.cadastro-container {
  min-height: 100vh;
  min-height: 100dvh;
  width: 100%;
  padding: 30px;

  display: flex;
  justify-content: center;
  align-items: center;

  background: #1a1a1a;
}

.cadastro-card {
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

  padding: 45px 50px;
  background: #fff;
}

.form-content {
  width: 100%;
  max-width: 360px;
}

.form-heading {
  margin-bottom: 25px;
}

.form-eyebrow {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 2px;
  color: #611811;
}

.form-heading h2 {
  margin: 12px 0 8px;

  font-size: 30px;
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
  gap: 8px;
  margin-bottom: 17px;
}

.field-group label {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.field-group input {
  width: 100%;
  height: 46px;
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

.btn-cadastrar {
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

.btn-cadastrar:hover:not(:disabled) {
  background: #7d251b;
  transform: translateY(-1px);
}

.btn-cadastrar:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

/* LINK PARA LOGIN */

.login-area {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 5px;

  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.login-area p {
  font-size: 12px;
  color: #777;
  margin: 0;
}

.login-link {
  font-size: 12px;
  font-weight: 600;
  color: #611811;
  text-decoration: none;
}

.login-link:hover {
  text-decoration: underline;
}

/* MENSAGENS */

.mensagem {
  padding: 12px;
  margin: 0 0 15px;
  border-radius: 6px;

  font-size: 12px;
  line-height: 1.5;
  text-align: center;
}

.mensagem-erro {
  background: #fff1f0;
  border: 1px solid #f0c2bd;
  color: #a52a20;
}

.mensagem-sucesso {
  background: #edf9f1;
  border: 1px solid #b7dfc3;
  color: #267c44;
}

/* RESPONSIVIDADE */

@media (max-width: 800px) {
  .cadastro-card {
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
  .cadastro-container {
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
