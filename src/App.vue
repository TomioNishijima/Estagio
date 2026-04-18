<template>
  <div class="main-container">
    <aside class="sidebar">
      <div class="sidebar-logo">
        </div>
      <nav class="sidebar-nav">
        <ul>
          <li>Dashboard de Estoque</li>
          <li class="active">Gestão de Inventário</li>
          <li>Catálogo de Projetos/Produtos</li>
          <li>Visualizador de Realidade Aumentada</li>
          <li>Gerador de Orçamentos</li>
        </ul>
      </nav>
    </aside>

    <div class="main-content">
      <header class="top-header">
        <h1>Marcenaria Nishijima 西嶋</h1>
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
                  <th style="text-align: center;">Ações</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="m in materiais" :key="m.id">
                  <td>{{ m.nome }}</td>
                  <td style="text-align: center;">
                    <button @click="abrirModal(m, 'SAIDA')" class="btn-controle btn-menos">-</button>
                    
                    <span style="font-weight: bold; margin: 0 10px;">{{ m.quantidade }}</span>
                    
                    <button @click="abrirModal(m, 'ENTRADA')" class="btn-controle btn-mais">+</button>
                  </td>
                  <td>R$ {{ m.valor }}</td>
                  <td style="text-align: center;">
                    <button @click="excluirMaterial(m.id)" class="btn-delete">Excluir</button>
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
                  <button @click="registrarMovimentacao" class="btn-save">Confirmar</button>
                  <button @click="modalAberto = false" class="btn-cancel">Cancelar</button>
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
    await axios.post('http://localhost:8080/api/movimentacoes', {
      material: { id: movimentacaoForm.value.materialId },
      quantidade: movimentacaoForm.value.quantidade,
      tipo: movimentacaoForm.value.tipo
    });
    
    modalAberto.value = false;
    carregarMateriais(); // Recarrega a tabela para ver o novo estoque
    mostrarAviso(`${movimentacaoForm.value.tipo} registrada!`);
  } catch (error) {
    mostrarAviso("Erro ao registrar movimentação", "erro");
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
  
  if (!novoMaterial.value.nome || !novoMaterial.value.quantidade || !novoMaterial.value.valor) {
    alert("Por favor, preencha todos os campos antes de salvar.");
    return;
  }


  if (novoMaterial.value.quantidade < 0) {
    alert("A quantidade não pode ser negativa.");
    return;
  }

  if (novoMaterial.value.valor <= 0) {
    alert("O valor deve ser maior que zero.");
    return;
  }


  await axios.post('http://localhost:8080/api/materiais', novoMaterial.value);
  novoMaterial.value = { nome: '', quantidade: null, valor: null };
  carregarMateriais();
};

const carregarMateriais = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/materiais')
    materiais.value = res.data
  } catch (e) {
    console.error("Erro ao conectar com o backend.")
  }
}

const excluirMaterial = async (id) => {
  if (confirm("Deseja excluir?")) {
    await axios.delete(`http://localhost:8080/api/materiais/${id}`)
    carregarMateriais()
  }
}



onMounted(carregarMateriais)
</script>

<style scoped>

@import url('https://fonts.googleapis.com/css2?family=Lexend:wght@300;400;600&display=swap');

/* Aplica para tudo dentro do componente */
* {
  font-family: 'Lexend', sans-serif;
}

.main-container {
  display: flex;
  height: 100vh;
  background-color: #1a1a1a; 
  color: #333;
}
/* ... resto do seu CSS */

/* Estilos para os novos botões de controle na tabela */
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

/* Estilos para o Modal */
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

.btn-save {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 0 25px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}


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

.btn-delete {
  background: white;
  color: #ff4757;
  border: 1px solid #ff4757;
  padding: 5px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: 0.2s;
}

.btn-delete:hover {
  background: #ff4757;
  color: white;
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
