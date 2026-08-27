<template>
  <div class="main-container">    
    <Sidebar />
    <div class="main-content">
      <header class="top-header">
        <h1> Marcenaria Nishijima 西嶋</h1>
      </header>

      <div class="page-body">
        <div class="card">
          <div class="cadastro-section">
            <h3>Adicionar Item</h3>
            <div class="form-row">
              <input v-model="novoMaterial.nome" placeholder="Nome do Material" class="input-field name-field" />
              <input v-model="novoMaterial.quantidade" type="number" placeholder="Qtd" class="input-field" />
              <input v-model="novoMaterial.valor" type="number" placeholder="Valor" class="input-field" />
              <button @click="salvarMaterial" class="btn-save">Salvar</button>
            </div>
          </div>

          <div class="table-section">
            <table>
              <thead>
                <tr>
                  <th>Material</th>
                  <th style="text-align: center;">Estoque</th>
                  <th>Valor</th>
                  <th style="text-align: center;"> Ações</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="m in materiais" :key="m.id">
                  <td>
                    <template v-if="editandoId === m.id">
                      <input
                        v-model="m.nome"
                        class="input-field"
                      />
                    </template>

                    <template v-else>
                      {{ m.nome }}
                    </template>
                  </td>
                  <td style="text-align: center;">
                    <button @click="abrirModal(m, 'SAIDA')" class="btn-controle btn-menos">-</button>
                    
                    <span style="font-weight: bold; margin: 0 10px;">{{ m.quantidade }}</span>
                    
                    <button @click="abrirModal(m, 'ENTRADA')" class="btn-controle btn-mais">+</button>
                  </td>
                  <td>
                    <template v-if="editandoId === m.id">
                      <input
                        v-model="m.valor"
                        type="number"
                        class="input-field"
                      />
                    </template>

                    <template v-else>
                      R$ {{ m.valor }}
                    </template>
                  </td>
                  <td style="text-align: center;">

                    <template v-if="editandoId === m.id">

                      <button
                        @click="editarMaterial(m)"
                        class="btn btn-save"
                      >
                        Salvar
                      </button>

                      <button
                        @click="editandoId = null"
                        class="btn btn-delete"
                      >
                        Cancelar
                      </button>

                    </template>

                    <template v-else>

                      <button
                        @click="editandoId = m.id"
                        class="btn btn-edit"
                      >
                        Editar
                      </button>

                      <button
                        @click="excluirMaterial(m.id)"
                        class="btn btn-delete"
                      >
                        Excluir
                      </button>

                    </template>

                  </td>
                </tr>
              </tbody>
            </table>

            <div v-if="modalAberto" class="modal-overlay">
              <div class="modal-content">
                <h3>{{ movimentacaoForm.tipo === 'ENTRADA' ? 'Entrada de' : 'Saída de' }}</h3>
                <p style="color: #611811; font-weight: bold; font-size: 1.2rem;">{{ movimentacaoForm.nome }}</p>
                
                <div style="margin: 20px 0;">
                  <label>Quantidade:</label>
                  <input type="number" v-model="movimentacaoForm.quantidade" min="1" class="input-field" style="width: 100%; margin-top: 5px;">
                </div>
                
                <div class="modal-actions">
                  <button @click="registrarMovimentacao" class="btn btn-save">Confirmar</button>
                  <button @click="modalAberto = false" class=" btn btn-cancel">Cancelar</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="notificacao.visivel" :class="['toast', notificacao.tipo]">
    {{ notificacao.mensagem }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import Sidebar from './Sidebar.vue'

const API_MATERIAIS = 'http://localhost:8080/api/materiais'
const API_MOVIMENTACOES = 'http://localhost:8080/api/movimentacoes'
const modalAberto = ref(false);
const movimentacaoForm = ref({ materialId: null, nome: '', quantidade: 1, tipo: '' });


const abrirModal = (material, tipo) => {
  movimentacaoForm.value = { 
    materialId: material.id, 
    nome: material.nome, 
    quantidade: 1, 
    tipo: tipo 
  };
  modalAberto.value = true;
};

const registrarMovimentacao = async () => {
  try {
    await axios.post(API_MOVIMENTACOES, {
      material: { id: movimentacaoForm.value.materialId },
      quantidade: movimentacaoForm.value.quantidade,
      tipo: movimentacaoForm.value.tipo
    });
    
    modalAberto.value = false;
    carregarMateriais(); 
    mostrarAviso(`${movimentacaoForm.value.tipo} registrada!`);
  } catch (error) {
    const mensagem =
    error.response?.data?.message || 
    error.response?.data ||
    "Erro ao registrar movimentação";

    mostrarAviso(
      mensagem,
      "erro"
    );
  }
};

const materiais = ref([])
const novoMaterial = ref({ nome: '', quantidade: null, valor: null })
const notificacao = ref({ visivel: false, mensagem: '', tipo: 'sucesso' });

const mostrarAviso = (msg, tipo = 'sucesso') => {
  notificacao.value = { visivel: true, mensagem: msg, tipo };
  
  setTimeout(() => { notificacao.value.visivel = false; }, 3000);
};


const salvarMaterial = async () => {

  if (
    novoMaterial.value.quantidade === null ||
    novoMaterial.value.valor === null ||
    !novoMaterial.value.nome
  ) {
    mostrarAviso("Preencha todos os campos.", "erro");
    return;
  }

  try {

    await axios.post(
      API_MATERIAIS,
      novoMaterial.value
    );

    novoMaterial.value = {
      nome: '',
      quantidade: null,
      valor: null
    };

    carregarMateriais();

    mostrarAviso(
      "Material cadastrado com sucesso!"
    );

  } catch (error) {

    mostrarAviso(
      "Já existe um material com esse nome.",
      "erro"
    );

    console.error(error);
  }
};

const carregarMateriais = async () => {
  try {
    const res = await axios.get(API_MATERIAIS)
    materiais.value = res.data
  } catch (e) {
    mostrarAviso("Erro ao conectar com backend", "erro")
  }
}

const excluirMaterial = async (id) => {

  console.log("clicou", id);

  if (confirm("Deseja excluir?")) {

    console.log("confirmou");

    await axios.delete(`${API_MATERIAIS}/${id}`);

    carregarMateriais();
  }
}

const editandoId = ref(null);
const editarMaterial = async (material) => {
  try {

    await axios.put(
      `http://localhost:8080/api/materiais/${material.id}`,
      {
        nome: material.nome,
        valor: material.valor
      }
    )

    editandoId.value = null
    carregarMateriais()

    mostrarAviso("Material atualizado!")

  } catch (error) {
    const mensagem =
      error.response?.data?.message ||
      error.response?.data ||
      "Erro ao editar material";

    mostrarAviso(mensagem, "erro");
  }finally {
    carregarMateriais();
  }
}

onMounted(carregarMateriais)
</script>

<style scoped>

@import url('https://fonts.googleapis.com/css2?family=Lexend:wght@300;400;600&display=swap');


* {
  font-family: 'Lexend', sans-serif;
}

.main-container {
  display: flex;
  height: 100vh;
  background-color: #1a1a1a; 
  color: #333;
}



.btn {
  min-width: 90px;
  height: 36px;
  padding: 0 16px;

  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.btn-save {
  background-color: #42b983;
  color: white;
}

.btn-edit {
  background-color: #3498db;
  color: white;
}

.btn-delete {
  background-color: #ff4757;
  color: white;
}

.btn-cancel {
  background-color: #ccc;
  color: black;
}

.btn-controle {
  border: none;
  width: 25px;
  height: 25px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  color: white;
}
.btn-mais { background-color: #42b983; }
.btn-menos { background-color: #e67e22; }
.modal-overlay {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0,0,0,0.6);
  display: flex; justify-content: center; align-items: center;
  z-index: 2000;
}
.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 320px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0,0,0,0.5);
}
.btn-cancel {
  background: #ccc;
  border: none;
  padding: 10px 15px;
  margin-left: 10px;
  border-radius: 4px;
  cursor: pointer;
}
.modal-actions { margin-top: 20px; }

.main-container {
  display: flex;
  height: 100vh;
  background-color: #1a1a1a; 
  color: #333;
}


.sidebar {
  width: 260px;
  background-color: #e0e0e0;
  border-right: 1px solid #ccc;
  display: flex;
  flex-direction: column;
}

.sidebar-nav ul { list-style: none; }

.sidebar-nav li {
  padding: 18px 25px;
  color: #611811;
  font-weight: 600;
  border-bottom: 1px solid #d0d0d0;
  cursor: pointer;
  font-size: 14px;
}

.sidebar-nav li.active {
  background-color: #d0d0d0;
  box-shadow: inset 4px 0 0 #611811; 
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
  width: 100%;
}

.top-header h1 {
  font-size: 22px;
  font-weight: 400;
  letter-spacing: 1px;
}


.page-body {
  padding: 30px;
}

.card {
  background: white;
  border-radius: 4px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

.cadastro-section h3 {
  margin-bottom: 15px;
  color: #444;
}

.form-row {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
}

.input-field {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fdfdfd;
}

.name-field { flex: 2; }

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  background-color: #f8f8f8;
  color: #888;
  text-align: left;
  padding: 12px;
  font-size: 13px;
  text-transform: uppercase;
}

td {
  padding: 14px 12px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

.toast {
  position: fixed;
  bottom: 20px;
  right: 20px;
  padding: 15px 25px;
  border-radius: 4px;
  color: white;
  font-family: 'Lexend', sans-serif;
  font-weight: 400;
  box-shadow: 0 4px 12px rgba(0,0,0,0.5);
  z-index: 1000;
  animation: slideIn 0.3s ease-out;
}

.sucesso { background-color: #42b983; } 
.erro { background-color: #611811; }    

@keyframes slideIn {
  from { transform: translateX(100%); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}
</style>
