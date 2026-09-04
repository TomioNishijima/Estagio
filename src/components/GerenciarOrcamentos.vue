<template>
  <div class="main-container">

    <Sidebar />

      <div class="main-content">

        <header class="top-header">
          <h1>Marcenaria Nishijima 西嶋</h1>
        </header>

          <div class="page-body">

            <div class="orcamento-header">

              <div>
                <h2>Gerenciar Orçamentos</h2>
                <p>Consulte e gerencie os orçamentos cadastrados.</p>
              </div>

                <button
                  class="btn-novo"
                  @click="abrirNovoOrcamento"
                >
                  + Novo Orçamento
                </button>

                </div>

              <div v-if="tela === 'criar'" class="card">

                <div class="criar-header">

                  <div>
                    <h2>
                      {{ modoOrcamento === 'ver' ? 'Visualizar orçamento' : nomeOrcamento }}
                    </h2>

                    <p v-if="modoOrcamento === 'novo'">
                      Adicione os materiais utilizados no orçamento.
                    </p>

                    <p v-else-if="modoOrcamento === 'editar'">
                      Edite os materiais e quantidades do orçamento.
                    </p>

                    <p v-else>
                      Visualização dos materiais utilizados neste orçamento.
                    </p>
                  </div>

                  <button
                    class="btn btn-cancel"
                    @click="tela = 'lista'"
                  >
                    Voltar
                  </button>

                </div>


                <div 
                  v-if="modoOrcamento !== 'ver'" 
                  class="form-item"
                >

                  <select
                    v-model.number="novoItem.materialId"
                    class="input-field"
                  >

                    <option :value="null">
                      Selecione um material
                    </option>

                    <option
                      v-for="material in materiais"
                      :key="material.id"
                      :value="material.id"
                    >
                      {{ material.nome }}
                    </option>

                  </select>


                  <input
                    v-model.number="novoItem.quantidade"
                    type="number"
                    min="1"
                    class="input-field"
                    placeholder="Quantidade"
                  />


                  <button
                    class="btn btn-save"
                    @click="adicionarItem"
                  >
                    Adicionar
                  </button>

                </div>


                <table>

                  <thead>

                    <tr>
                      <th>Material</th>
                      <th>Quantidade</th>
                      <th>Valor unitário</th>
                      <th>Subtotal</th>
                      <th>Ação</th>
                    </tr>

                  </thead>


                  <tbody>

                    <tr
                      v-for="(item, index) in itensOrcamento"
                      :key="index"
                    >

                      <td>
                        {{ item.material?.nome }}
                      </td>

                      <td>
                        {{ item.quantidade }}
                      </td>

                      <td>
                        R$ 
                        {{ formatarValor(item.valorUnitario ?? item.material?.valor) }}
                      </td>

                      <td>
                        {{ formatarValor(
                          Number(item.quantidade) *
                          Number(item.valorUnitario ?? item.material?.valor)
                        ) }}
                      </td>

                      <td>

                        <button
                          v-if="modoOrcamento !== 'ver'"
                          class="btn btn-delete"
                          @click="removerItem(index)"
                        >
                          Remover
                        </button>

                        <span v-else>
                          -
                        </span>

                      </td>

                    </tr>


                    <tr v-if="itensOrcamento.length === 0">

                      <td
                        colspan="5"
                        class="sem-orcamentos"
                      >
                        Nenhum material adicionado.
                      </td>

                    </tr>

                  </tbody>

                </table>


                <div class="total-orcamento">

                  <strong>
                    Custo final:
                  </strong>

                  <strong>
                    R$ {{ formatarValor(custoFinal) }}
                  </strong>

                </div>


                <div class="modal-actions">
                  
                  <button
                    type="button"
                    class="btn btn-cancel"
                    @click="cancelarCriacao"
                  >
                    Voltar
                  </button>
                  
                  <button
                    v-if="modoOrcamento !== 'ver'"
                    type="button"
                    class="btn btn-save"
                    @click="salvarOrcamento"
                  >
                    {{modoOrcamento === 'editar'
                    ? 'Salvar alterações'
                    : 'Salvar orçamento'
                    }}
                  </button>

                </div>

              </div>
                <!-- LISTA DE ORÇAMENTOS -->

              <div class="card">

                <table>

                  <thead>
                    <tr>
                      <th>Orçamento</th>
                      <th>Data</th>
                      <th>Custo</th>
                      <th style="text-align: center;">Ações</th>
                    </tr>
                  </thead>

                  <tbody>

                    <tr
                      v-for="orcamento in orcamentos"
                      :key="orcamento.id"
                    >

                      <td>
                        {{ orcamento.nome }}
                      </td>

                      <td>
                        {{ formatarData(orcamento.data) }}
                      </td>      
    
                      <td>
                        R$ {{ formatarValor(orcamento.custoFinal) }}
                      </td>

                      <td class="acoes">
                            
                        <button
                            class="btn btn-ver"
                            @click="verOrcamento(orcamento)"
                        >
                          Ver
                        </button>

                        <button
                            class="btn btn-edit"
                            @click="editarOrcamento(orcamento)"
                          >
                            Editar
                          </button>

                          <button
                            class="btn btn-delete"
                            @click="excluirOrcamento(orcamento.id)"
                          >
                            Excluir
                          </button>

                        </td>

                      </tr>


                        <!-- Quando não existem orçamentos -->

                      <tr v-if="orcamentos.length === 0">

                        <td
                          colspan="4"
                          class="sem-orcamentos"
                        >
                          Nenhum orçamento cadastrado.
                        </td>

                      </tr>

                    </tbody>

                  </table>
            </div>
          </div>
      </div>

  </div>


    
    <!-- NOTIFICAÇÃO -->

    <div
      v-if="notificacao.visivel"
      :class="['toast', notificacao.tipo]"
    >
      {{ notificacao.mensagem }}
    </div>
    
    <!-- NOVO ORÇAMENTO -->

    <div
      v-if="novoOrcamentoAberto"
      class="modal-overlay"
    >

      <div class="modal-content">

        <h2>Novo Orçamento</h2>

        <div class="form-group">

          <label>
            Nome do orçamento
          </label>

          <input
            v-model="nomeOrcamento"
            class="input-field"
            placeholder="Ex: Cozinha planejada"
          />

        </div>


        <div class="modal-actions">

          <button
            class="btn btn-cancel"
            @click="novoOrcamentoAberto = false"
          >
            Cancelar
          </button>

          <button
            class="btn btn-save"
            @click="continuarOrcamento"
          >
            Continuar
          </button>

        </div>

      </div>

        
    </div>
</template>


<script setup>

import { ref, onMounted } from 'vue'
import axios from 'axios'
import Sidebar from './Sidebar.vue'

const API_ORCAMENTOS = 'http://localhost:8080/api/orcamentos'
const API_MATERIAIS = 'http://localhost:8080/api/materiais'

const orcamentos = ref([])
const materiais = ref([])
const itensOrcamento = ref([])

const tela = ref('lista')
const telaCriacao = ref(false)

const novoOrcamentoAberto = ref(false)
const nomeOrcamento = ref('')

const modoOrcamento = ref('novo')
const orcamentoAtual = ref(null)

const novoItem = ref({
  materialId: null,
  quantidade: 1
})

const custoFinal = ref(0)

const notificacao = ref({
  visivel: false,
  mensagem: '',
  tipo: 'sucesso'
})


// =====================================================
// NOTIFICAÇÃO
// =====================================================

const mostrarAviso = (mensagem, tipo = 'sucesso') => {

  notificacao.value = {
    visivel: true,
    mensagem,
    tipo
  }

  setTimeout(() => {
    notificacao.value.visivel = false
  }, 3000)
}


// =====================================================
// CARREGAR ORÇAMENTOS
// =====================================================

const carregarOrcamentos = async () => {

  try {

    const resposta = await axios.get(API_ORCAMENTOS)

    orcamentos.value = resposta.data

    console.log('Orçamentos:', resposta.data)

  } catch (error) {

    console.error('Erro ao carregar orçamentos:', error)

    mostrarAviso(
      'Erro ao carregar os orçamentos.',
      'erro'
    )

  }

}


// =====================================================
// CARREGAR MATERIAIS
// =====================================================

const carregarMateriais = async () => {

  try {

    const resposta = await axios.get(API_MATERIAIS)

    materiais.value = resposta.data

    console.log(
      'Materiais carregados:',
      materiais.value
    )

  } catch (error) {

    console.error(
      'Erro ao carregar materiais:',
      error
    )

    mostrarAviso(
      'Erro ao carregar materiais.',
      'erro'
    )

  }

}


// =====================================================
// CALCULAR CUSTO
// =====================================================

const calcularCustoFinal = () => {

  custoFinal.value =
    itensOrcamento.value.reduce(
      (total, item) => {

        const quantidade =
          Number(item.quantidade) || 0

        const valor =
          Number(item.material?.valor ?? item.valorUnitario) || 0

        return total + quantidade * valor

      },
      0
    )

}


// =====================================================
// ADICIONAR ITEM
// =====================================================

const adicionarItem = () => {

  console.log('BOTÃO ADICIONAR CLICADO')

  if (!novoItem.value.materialId) {

    mostrarAviso(
      'Selecione um material.',
      'erro'
    )

    return
  }

  if (
    !novoItem.value.quantidade ||
    novoItem.value.quantidade <= 0
  ) {

    mostrarAviso(
      'Informe uma quantidade válida.',
      'erro'
    )

    return
  }

  const material = materiais.value.find(
    m => m.id === Number(novoItem.value.materialId)
  )

  if (!material) {

    mostrarAviso(
      'Material não encontrado.',
      'erro'
    )

    return
  }

  // Impede adicionar o mesmo material duas vezes
  const existente = itensOrcamento.value.find(
    item => item.material?.id === material.id
  )

  if (existente) {

    existente.quantidade =
      Number(existente.quantidade) +
      Number(novoItem.value.quantidade)

    mostrarAviso(
      'Material já estava no orçamento. Quantidade atualizada!'
    )

    calcularCustoFinal()

    novoItem.value = {
      materialId: null,
      quantidade: 1
    }

    return
  }

  const quantidade =
    Number(novoItem.value.quantidade)

  const subtotal =
    quantidade * Number(material.valor)

  const item = {

    material: material,

    quantidade: quantidade,

    valorUnitario: Number(material.valor),

    subtotal: subtotal

  }

  itensOrcamento.value.push(item)

  novoItem.value = {

    materialId: null,

    quantidade: 1

  }

  calcularCustoFinal()

  mostrarAviso(
    'Material adicionado ao orçamento!'
  )

}


// =====================================================
// REMOVER ITEM
// =====================================================

const removerItem = (index) => {

  itensOrcamento.value.splice(index, 1)

  calcularCustoFinal()

}


// =====================================================
// NOVO ORÇAMENTO
// =====================================================

const abrirNovoOrcamento = () => {

  nomeOrcamento.value = ''

  itensOrcamento.value = []

  custoFinal.value = 0

  novoItem.value = {
    materialId: null,
    quantidade: 1
  }

  modoOrcamento.value = 'novo'

  orcamentoAtual.value = null

  novoOrcamentoAberto.value = true

}


const continuarOrcamento = async () => {

  if (!nomeOrcamento.value.trim()) {

    mostrarAviso(
      'Digite um nome para o orçamento.',
      'erro'
    )

    return
  }

  await carregarMateriais()

  novoOrcamentoAberto.value = false

  tela.value = 'criar'

  telaCriacao.value = true

}


// =====================================================
// VER ORÇAMENTO
// =====================================================

const verOrcamento = async (orcamento) => {

  try {

    console.log(
      'Carregando orçamento:',
      orcamento
    )

    const resposta = await axios.get(
      `${API_ORCAMENTOS}/${orcamento.id}/itens`
    )

    console.log(
      'Itens recebidos:',
      resposta.data
    )

    orcamentoAtual.value = orcamento

    nomeOrcamento.value = orcamento.nome

    itensOrcamento.value =
      resposta.data.map(item => ({

        id: item.id,

        material: item.material,

        quantidade: Number(item.quantidade),

        valorUnitario: Number(item.valorUnitario),

        subtotal: Number(item.subtotal)

      }))

    calcularCustoFinal()

    modoOrcamento.value = 'ver'

    tela.value = 'criar'

    telaCriacao.value = true

    // Carrega materiais caso o usuário queira voltar para edição
    await carregarMateriais()

  } catch (error) {

    console.error(
      'Erro ao carregar orçamento:',
      error
    )

    mostrarAviso(
      'Erro ao carregar os itens do orçamento.',
      'erro'
    )

  }

}


// =====================================================
// EDITAR ORÇAMENTO
// =====================================================

const editarOrcamento = async (orcamento) => {

  try {

    console.log(
      'Editando orçamento:',
      orcamento
    )

    const resposta = await axios.get(
      `${API_ORCAMENTOS}/${orcamento.id}/itens`
    )

    console.log(
      'Itens para edição:',
      resposta.data
    )

    await carregarMateriais()

    orcamentoAtual.value = orcamento

    nomeOrcamento.value = orcamento.nome

    itensOrcamento.value =
      resposta.data.map(item => ({

        id: item.id,

        material: item.material,

        quantidade: Number(item.quantidade),

        valorUnitario: Number(item.valorUnitario),

        subtotal: Number(item.subtotal)

      }))

    calcularCustoFinal()

    modoOrcamento.value = 'editar'

    tela.value = 'criar'

    telaCriacao.value = true

  } catch (error) {

    console.error(
      'Erro ao carregar orçamento para edição:',
      error
    )

    mostrarAviso(
      'Erro ao carregar o orçamento.',
      'erro'
    )

  }

}


// =====================================================
// SALVAR ORÇAMENTO
// =====================================================

const salvarOrcamento = async () => {

  console.log(
    'BOTÃO SALVAR ORÇAMENTO CLICADO'
  )

  if (!nomeOrcamento.value.trim()) {

    mostrarAviso(
      'Nome do orçamento inválido.',
      'erro'
    )

    return
  }

  if (itensOrcamento.value.length === 0) {

    mostrarAviso(
      'Adicione pelo menos um material.',
      'erro'
    )

    return
  }

  const dadosOrcamento = {

    nome: nomeOrcamento.value.trim(),

    itens: itensOrcamento.value.map(item => ({

      materialId: item.material.id,

      quantidade: Number(item.quantidade)

    }))

  }

  console.log(
    'DADOS ENVIADOS:',
    dadosOrcamento
  )

  try {

    let resposta

    // NOVO
    if (modoOrcamento.value === 'novo') {

      resposta = await axios.post(
        API_ORCAMENTOS,
        dadosOrcamento
      )

      mostrarAviso(
        'Orçamento criado com sucesso!'
      )

    }

    // EDITAR
    else if (modoOrcamento.value === 'editar') {

      resposta = await axios.put(
        `${API_ORCAMENTOS}/${orcamentoAtual.value.id}`,
        dadosOrcamento
      )

      mostrarAviso(
        'Orçamento editado com sucesso!'
      )

    }

    console.log(
      'RESPOSTA DO BACKEND:',
      resposta?.data
    )

    cancelarCriacao()

    await carregarOrcamentos()

  } catch (error) {

    console.error(
      'ERRO AO SALVAR ORÇAMENTO:',
      error
    )

    console.log(
      'STATUS:',
      error.response?.status
    )

    console.log(
      'RESPOSTA:',
      error.response?.data
    )

    mostrarAviso(
      error.response?.data ||
      'Erro ao salvar orçamento.',
      'erro'
    )

  }

}


// =====================================================
// CANCELAR / VOLTAR
// =====================================================

const cancelarCriacao = () => {

  telaCriacao.value = false

  tela.value = 'lista'

  nomeOrcamento.value = ''

  itensOrcamento.value = []

  custoFinal.value = 0

  novoItem.value = {
    materialId: null,
    quantidade: 1
  }

  modoOrcamento.value = 'novo'

  orcamentoAtual.value = null

}


// =====================================================
// EXCLUIR ORÇAMENTO
// =====================================================

const excluirOrcamento = async (id) => {

  const confirmar = confirm(
    'Deseja realmente excluir este orçamento?'
  )

  if (!confirmar) {
    return
  }

  try {

    await axios.delete(
      `${API_ORCAMENTOS}/${id}`
    )

    mostrarAviso(
      'Orçamento excluído com sucesso!'
    )

    await carregarOrcamentos()

  } catch (error) {

    console.error(
      'Erro ao excluir orçamento:',
      error
    )

    mostrarAviso(
      error.response?.data ||
      'Erro ao excluir orçamento.',
      'erro'
    )

  }

}


// =====================================================
// FORMATAÇÃO
// =====================================================

const formatarValor = (valor) => {

  return Number(valor || 0)
    .toFixed(2)
    .replace('.', ',')

}


const formatarData = (data) => {

  if (!data) {
    return '-'
  }

  const dataObj = new Date(data)

  return dataObj.toLocaleDateString(
    'pt-BR'
  )

}


// =====================================================
// INICIALIZAÇÃO
// =====================================================

onMounted(() => {

  carregarOrcamentos()

})

</script>


<style scoped>

@import url('https://fonts.googleapis.com/css2?family=Lexend:wght@300;400;600&display=swap');


* {
  font-family: 'Lexend', sans-serif;
}


/* =====================================================
   LAYOUT
   ===================================================== */

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


/* =====================================================
   CABEÇALHO
   ===================================================== */

.orcamento-header {

  display: flex;

  align-items: center;

  justify-content: space-between;

  margin-bottom: 25px;

}


.orcamento-header h2 {

  color: white;

  margin: 0 0 5px 0;

}


.orcamento-header p {

  color: #aaa;

  margin: 0;

}


/* =====================================================
   BOTÃO NOVO
   ===================================================== */

.btn-novo {

  background-color: #42b983;

  color: white;

  border: none;

  border-radius: 4px;

  padding: 12px 18px;

  font-family: 'Lexend', sans-serif;

  font-weight: 600;

  cursor: pointer;

}


.btn-novo:hover {

  opacity: 0.9;

}


/* =====================================================
   CARD
   ===================================================== */

.card {

  background: white;

  border-radius: 4px;

  padding: 25px;

  box-shadow:
    0 4px 20px rgba(0,0,0,0.3);

}


/* =====================================================
   TABELA
   ===================================================== */

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


.acoes {

  text-align: center;

}


/* =====================================================
   BOTÕES
   ===================================================== */

.btn {

  min-width: 70px;

  height: 34px;

  padding: 0 12px;

  border: none;

  border-radius: 4px;

  cursor: pointer;

  font-weight: bold;

  margin: 0 3px;

}


.btn-ver {

  background-color: #7f8c8d;

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


/* =====================================================
   SEM ORÇAMENTOS
   ===================================================== */

.sem-orcamentos {

  text-align: center;

  color: #888;

  padding: 30px;

}


/* =====================================================
   TOAST
   ===================================================== */

.toast {

  position: fixed;

  bottom: 20px;

  right: 20px;

  padding: 15px 25px;

  border-radius: 4px;

  color: white;

  font-weight: 400;

  box-shadow:
    0 4px 12px rgba(0,0,0,0.5);

  z-index: 1000;

}


.sucesso {

  background-color: #42b983;

}


.erro {

  background-color: #611811;

}

/* =====================================================
   MODAL NOVO ORÇAMENTO
   ===================================================== */

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

  width: 450px;

  max-width: 90%;

  padding: 30px;

  border-radius: 8px;

  box-shadow:
    0 10px 30px rgba(0, 0, 0, 0.5);
}


.modal-content h2 {
  color: #611811;

  margin-top: 0;

  margin-bottom: 25px;
}


.form-group {
  display: flex;

  flex-direction: column;

  gap: 8px;

  margin-bottom: 25px;
}


.form-group label {
  font-weight: 600;

  color: #444;
}


.input-field {
  padding: 10px;

  border: 1px solid #ddd;

  border-radius: 4px;

  font-family: 'Lexend', sans-serif;

  font-size: 14px;
}


.modal-actions {
  display: flex;

  justify-content: flex-end;

  gap: 10px;
}


.btn-save {
  background-color: #42b983;

  color: white;
}


.btn-cancel {
  background-color: #ccc;

  color: black;
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

  width: 450px;

  box-shadow:
    0 10px 30px rgba(0, 0, 0, 0.5);
}


.modal-content h2 {
  color: #611811;

  margin-top: 0;

  margin-bottom: 25px;
}


.form-group {
  display: flex;

  flex-direction: column;

  gap: 8px;

  margin-bottom: 25px;
}


.form-group label {
  font-weight: 600;

  color: #444;
}


.input-field {
  padding: 10px;

  border: 1px solid #ddd;

  border-radius: 4px;

  font-family: 'Lexend', sans-serif;
}


.modal-actions {
  display: flex;

  justify-content: flex-end;

  gap: 10px;
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


.btn-cancel {
  background-color: #ccc;

  color: black;
}

</style>