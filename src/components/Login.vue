<template>
  <div class="login-container">
    <div class="login-box">
      <h1>Login</h1>

      <input
        type="text"
        placeholder="Usuário"
      />

      <input
        type="password"
        placeholder="Senha"
      />

      <button @click="entrar">
        Entrar
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function entrar() {
  router.push('/dashboard')
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 300px;
}

input {
  padding: 10px;
}

button {
  padding: 10px;
  cursor: pointer;
}
</style>