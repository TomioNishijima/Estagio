<template>
  <div class="main-container">

    <Sidebar />

    <div class="main-content">

      <header class="top-header">
        <h1>Marcenaria Nishijima 西嶋</h1>
      </header>

      <div class="page-body">

        <div class="dashboard-header">
          <h2>Dashboard de Estoque</h2>
          <p>Acompanhe os materiais selecionados.</p>
        </div>

        <button class="btn-add"@click="abrirModal">+ Adicionar material</button>
        <div class="materiais-grid">

          <div
            v-for="material in materiaisExibidos"
            :key="material.id"
            class="material-card"
          >
            <h3>{{ material.nome }}</h3>

            <div class="quantidade">
              {{ material.quantidade }}
            </div>

            <span>em estoque</span>
          </div>

        </div>

      </div>

    </div>

  </div>
  <div v-if="modalAberto" class="modal-overlay">

    <div class="modal-content">

      <h3>Adicionar material ao Dashboard</h3>

      <div class="materiais-lista">

        <label
          v-for="material in materiais"
          :key="material.id"
          class="material-option"
        >

          <input
            type="checkbox"
            :value="material.id"
            v-model="materiaisSelecionados"
          />

          <span>{{ material.nome }}</span>

        </label>

      </div>

      <div class="modal-actions">

        <button
          class="btn btn-cancel"
          @click="modalAberto = false"
        >
          Cancelar
        </button>

        <button
          class="btn btn-save"
          @click="salvarSelecao"
        >
          Salvar
        </button>

      </div>

    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import Sidebar from './Sidebar.vue'

const API_MATERIAIS = 'http://localhost:8080/api/materiais'
const API_DASHBOARD = 'http://localhost:8080/api/dashboard'

const materiais = ref([])
const modalAberto = ref(false)
const materiaisSelecionados = ref([])
const materiaisExibidos = ref([])


const abrirModal = () => {
  materiaisSelecionados.value =
    materiaisExibidos.value.map(
      material => material.id
    )

  modalAberto.value = true
}

const carregarMateriais = async () => {
  try {
    const res = await axios.get(API_MATERIAIS)

    console.log("Materiais recebidos:", res.data)

    materiais.value = res.data

  } catch (error) {
    console.error("ERRO AO CARREGAR MATERIAIS:", error)
  }
}

const salvarSelecao = async () => {

  try {

    // IDs que atualmente estão na Dashboard
    const idsAtuais = materiaisExibidos.value.map(
      material => material.id
    )

    // IDs que o usuário selecionou
    const idsSelecionados = materiaisSelecionados.value

    console.log("Atuais:", idsAtuais)
    console.log("Selecionados:", idsSelecionados)


    // =========================
    // ADICIONAR
    // =========================

    for (const id of idsSelecionados) {

      if (!idsAtuais.includes(id)) {

        console.log("Adicionando:", id)

        await axios.post(
          `${API_DASHBOARD}/materiais/${id}`
        )

      }

    }


    // =========================
    // REMOVER
    // =========================

    for (const id of idsAtuais) {

      if (!idsSelecionados.includes(id)) {

        console.log("Removendo:", id)

        await axios.delete(
          `${API_DASHBOARD}/materiais/${id}`
        )

      }

    }


    // =========================
    // ATUALIZAR DASHBOARD
    // =========================

    await carregarDashboard()


    // Fecha o modal
    modalAberto.value = false


    console.log("Dashboard salva com sucesso!")

  } catch (error) {

    console.error(
      "Erro ao salvar Dashboard:",
      error
    )

    console.error(
      "Resposta:",
      error.response?.data
    )

  }

}


const testeClique = () => {
  alert("O botão está funcionando!")
}

const carregarDashboard = async () => {

  try {

    const res = await axios.get(API_DASHBOARD)

    materiaisExibidos.value =
      res.data.map(item => item.material)

    materiaisSelecionados.value =
      materiaisExibidos.value.map(
        material => material.id
      )

  } catch (error) {

    console.error(
      'Erro ao carregar Dashboard:',
      error
    )

  }

}

onMounted(() => {

  carregarMateriais()
  carregarDashboard()

})
</script>

<style scoped>

@import url('https://fonts.googleapis.com/css2?family=Lexend:wght@300;400;600&display=swap');

* {
  font-family: 'Lexend', sans-serif;
}

.btn-add {
  background-color: #611811;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 10px 16px;
  font-family: 'Lexend', sans-serif;
  font-weight: 600;
  cursor: pointer;
  margin-bottom: 20px;
}

.btn-add:hover {
  background-color: #4d0e0a;
}

.main-container {
  display: flex;
  height: 100vh;
  background-color: #1a1a1a;
  color: #333;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.top-header {
  background-color: #611811;
  color: white;
  min-height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.top-header h1 {
  font-size: 22px;
  font-weight: 400;
  letter-spacing: 1px;
}

.page-body {
  padding: 30px;
}

.dashboard-header {
  margin-bottom: 25px;
}

.dashboard-header h2 {
  color: white;
  margin-bottom: 5px;
}

.dashboard-header p {
  color: #aaa;
}

.materiais-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.material-card {
  background: white;
  border-radius: 6px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

.material-card h3 {
  color: #611811;
  margin: 0;
}

.quantidade {
  font-size: 40px;
  font-weight: 600;
  margin-top: 20px;
}

.material-card span {
  color: #888;
  font-size: 14px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;

  background: rgba(0, 0, 0, 0.6);

  display: flex;
  justify-content: center;
  align-items: center;

  z-index: 2000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;

  width: 400px;
  max-width: 90%;

  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}

.modal-content h3 {
  color: #611811;
  margin-top: 0;
  margin-bottom: 20px;
}

.materiais-lista {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.material-option {
  display: flex;
  align-items: center;
  gap: 10px;

  padding: 10px;

  border-bottom: 1px solid #eee;

  cursor: pointer;
}

.material-option input {
  width: 18px;
  height: 18px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;

  margin-top: 25px;
  gap: 10px;
}

.btn {
  border: none;
  border-radius: 4px;

  padding: 10px 18px;

  cursor: pointer;
  font-weight: bold;
}

.btn-save {
  background-color: #42b983;
  color: white;
}

.btn-cancel {
  background-color: #ccc;
  color: black;
}
</style>