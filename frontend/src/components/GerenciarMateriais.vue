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
            <div class="cadastro-header">
              <h3>Adicionar Item</h3>

              <span class="campos-obrigatorios">
                <span class="obrigatorio">*</span>
                Campos obrigatórios
              </span>
            </div>

            <div class="form-row">

              <div class="campo-cadastro name-field">
                <label for="material-nome">
                  Nome do material <span class="obrigatorio" aria-hidden="true">*</span>
                </label>

                <input
                  id="material-nome"
                  v-model="novoMaterial.nome"
                  @blur="novoMaterial.nome = padronizarNome(novoMaterial.nome)"
                  type="text"
                  placeholder="Nome do Material"
                  class="input-field nome-maiusculo"
                  required
                />
              </div>
              <div class="campo-cadastro">
                <label for="material-categoria">
                  Categoria <span class="obrigatorio" aria-hidden="true">*</span>
                </label>

                <select
                  id="material-categoria"
                  v-model="novoMaterial.categoria"
                  class="input-field"
                  required
                >
                  <option value="" disabled>Selecione a categoria</option>
                  <option value="MDF 15MM">MDF 15MM</option>
                  <option value="MDF 6MM">MDF 6MM</option>
                  <option value="FITA DE BORDA">FITA DE BORDA</option>
                  <option value="FERRAGEM">FERRAGEM</option>
                  <option value="ACESSÓRIOS">ACESSÓRIOS</option>
                  <option value="OUTROS">OUTROS</option>
                </select>
              </div>
              <div class="campo-cadastro campo-numero">
                <label for="material-quantidade">
                  Quantidade <span class="obrigatorio" aria-hidden="true">*</span>
                </label>

                <input
                  id="material-quantidade"
                  v-model.number="novoMaterial.quantidade"
                  type="number"
                  min="0"
                  step="1"
                  placeholder="Qtd"
                  class="input-field"
                  required
                />
              </div>
              <div class="campo-cadastro campo-numero">
                <label for="material-valor">
                  Valor (R$) <span class="obrigatorio" aria-hidden="true">*</span>
                </label>

                <input
                  id="material-valor"
                  v-model.number="novoMaterial.valor"
                  type="number"
                  min="0.01"
                  step="0.01"
                  placeholder="Valor"
                  class="input-field"
                  required
                />
              </div>

              <button @click="salvarMaterial" class="btn-save btn-cadastro">
                Salvar
              </button>
            </div>
          </div>

          <div class="table-section">
            <!-- BUSCA E FILTROS -->

            <div class="filtros-container">

              <div class="campo-busca">

                <label>Pesquisar material</label>

                <input
                  v-model="busca"
                  type="text"
                  placeholder="Digite o nome do material..."
                  class="input-field"
                />

              </div>
              <!-- FILTRO DE CATEGORIA -->
              <div class="campo-filtro">

                <label>Categoria</label>

                <select
                  v-model="filtroCategoria"
                  class="input-field"
                >
                <option value="todas">Todas as categorias</option>
                <option value="MDF 15MM">MDF 15MM</option>
                <option value="MDF 6MM">MDF 6MM</option>
                <option value="FITA DE BORDA">FITA DE BORDA</option>
                <option value="FERRAGEM">FERRAGEM</option>
                <option value="ACESSÓRIOS">ACESSÓRIOS</option>
                <option value="OUTROS">OUTROS</option>
                </select>

              </div>
              <div class="campo-filtro">

                <label>Estoque</label>

                <select
                  v-model="filtroEstoque"
                  class="input-field"
                >
                  <option value="todos">Todos</option>
                  <option value="disponivel">Disponíveis</option>
                  <option value="zerado">Estoque zerado</option>
                </select>
              </div>

              <div class="campo-filtro">
                <label>Ordenar por</label>

                <select
                  v-model="ordenacao"
                  class="input-field"
                >
                  <option value="nome-asc">Nome (A-Z)</option>
                  <option value="nome-desc">Nome (Z-A)</option>
                  <option value="maior-estoque">Maior estoque</option>
                  <option value="menor-estoque">Menor estoque</option>
                </select>

              </div>

              <button
                type="button"
                class="btn-limpar"
                @click="
                  busca = '';
                  filtroCategoria = 'todas';
                  filtroEstoque = 'todos';
                  ordenacao = 'nome-asc'
                "
              >
                Limpar filtros
              </button>

            </div>

              <p class="resultado-busca">
                Exibindo {{ materiaisFiltrados.length }}
                de {{ materiais.length }} materiais
              </p>

              <!-- SUA TABELA COMEÇA AQUI -->
            <table>
              <thead>
                <tr>
                  <th>Material</th>
                  <th>Categoria</th>
                  <th style="text-align: center;">Estoque</th>
                  <th>Valor</th>
                  <th style="text-align: center;"> Ações</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="m in materiaisFiltrados" :key="m.id">
                  <td>
                    <template v-if="editandoId === m.id">
                      <input
                        v-model="m.nome"
                        @blur="m.nome = padronizarNome(m.nome)"
                        class="input-field nome-maiusculo"
                       />
                    </template>

                    <template v-else>
                      {{ m.nome }}
                    </template>
                  </td>
                  <td>
                    <template v-if="editandoId === m.id">
                      <select v-model="m.categoria" class="input-field">
                        <option value="MDF 15MM">MDF 15MM</option>
                        <option value="MDF 6MM">MDF 6MM</option>
                        <option value="FITA DE BORDA">FITA DE BORDA</option>
                        <option value="FERRAGEM">FERRAGEM</option>
                        <option value="ACESSÓRIOS">ACESSÓRIOS</option>
                        <option value="OUTROS">OUTROS</option>
                      </select>
                    </template>

                    <template v-else>
                      {{ m.categoria || 'SEM CATEGORIA' }}
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

                  <td class="coluna-acoes">

                    <div class="acoes-material">

                      <template v-if="editandoId === m.id">

                        <button
                          @click="editarMaterial(m)"
                          class="btn btn-save"
                        >
                          Salvar
                        </button>

                        <button
                          @click="editandoId = null"
                          class="btn btn-cancel"
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

                    </div>

                  </td>

                </tr>
                <tr v-if="materiaisFiltrados.length === 0">
                  <td colspan="5" class="sem-resultados">
                    Nenhum material encontrado com os filtros selecionados.
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
import { ref, computed, onMounted } from 'vue'
import api from '../services/api'
import Sidebar from './Sidebar.vue'

const API_MATERIAIS = 'http://localhost:8080/api/materiais'
const API_MOVIMENTACOES = 'http://localhost:8080/api/movimentacoes'
const modalAberto = ref(false);
const movimentacaoForm = ref({ materialId: null, nome: '', quantidade: 1, tipo: '' });

const padronizarNome = (nome) => {
  return String(nome ?? '')
    .trim()
    .replace(/\s+/g, ' ')
    .toUpperCase()
}

const testarCriacao = async () => {

  try {

    const resposta = await api.post(
      'http://localhost:8080/api/orcamentos',
      {
        nome: 'Orçamento Teste',

        itens: [
          {
            materialId: 34,
            quantidade: 2
          },
          {
            materialId: 33,
            quantidade: 1
          }
        ]
      }
    )

    console.log("ORÇAMENTO CRIADO:", resposta.data)

  } catch (error) {

    console.error("Erro ao cadastrar material:", error);
    console.error("Resposta do servidor:", error.response?.data);

    const mensagem =
      error.response?.data?.message ||
      (typeof error.response?.data === 'string'
        ? error.response.data
        : null) ||
      "Erro ao cadastrar material.";

    mostrarAviso(mensagem, "erro");
  }
};
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
    await api.post(API_MOVIMENTACOES, {
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
const busca = ref('')
const filtroEstoque = ref('todos')
const ordenacao = ref('nome-asc')
const novoMaterial = ref({ nome: '', quantidade: null, valor: null, categoria: ''})
const filtroCategoria = ref('todas')
const notificacao = ref({ visivel: false, mensagem: '', tipo: 'sucesso' });

const mostrarAviso = (msg, tipo = 'sucesso') => {
  notificacao.value = { visivel: true, mensagem: msg, tipo };

  setTimeout(() => { notificacao.value.visivel = false; }, 3000);
};

const normalizarTexto = (texto) => {
  return String(texto ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toUpperCase()
    .trim()
}

const materiaisFiltrados = computed(() => {

  let resultado = [...materiais.value]

  // BUSCA POR NOME
  const termoBusca = normalizarTexto(busca.value)

  resultado = resultado.filter(material =>
    normalizarTexto(material.nome).includes(termoBusca)
  )

  // FILTRO POR ESTOQUE
  if (filtroEstoque.value === 'disponivel') {

    resultado = resultado.filter(
      material => Number(material.quantidade) > 0
    )

  } else if (filtroEstoque.value === 'zerado') {

    resultado = resultado.filter(
      material => Number(material.quantidade) === 0
    )

  }

  // FILTRO POR CATEGORIA
  if (filtroCategoria.value !== 'todas') {

    resultado = resultado.filter(material =>
      material.categoria === filtroCategoria.value
    )

  }

  // ORDENAÇÃO
  switch (ordenacao.value) {

    case 'nome-asc':
      resultado.sort((a, b) =>
        a.nome.localeCompare(b.nome, 'pt-BR')
      )
      break

    case 'nome-desc':
      resultado.sort((a, b) =>
        b.nome.localeCompare(a.nome, 'pt-BR')
      )
      break

    case 'maior-estoque':
      resultado.sort((a, b) =>
        Number(b.quantidade) - Number(a.quantidade)
      )
      break

    case 'menor-estoque':
      resultado.sort((a, b) =>
        Number(a.quantidade) - Number(b.quantidade)
      )
      break
  }

  return resultado
})

const salvarMaterial = async () => {

  if (
    novoMaterial.value.quantidade === null ||
    novoMaterial.value.valor === null ||
    !novoMaterial.value.nome ||
    !novoMaterial.value.categoria
  ) {
    mostrarAviso("Preencha todos os campos.", "erro");
    return;
  }

  try {
    console.log("Material enviado:", novoMaterial.value);

    await api.post(
      API_MATERIAIS,
      novoMaterial.value
    );

    novoMaterial.value = {
      nome: '',
      categoria: '',
      quantidade: null,
      valor: null
    };

    carregarMateriais();

    mostrarAviso("Material cadastrado com sucesso!");

  }  catch (error) {
    console.error("Erro ao cadastrar material:", error);

    mostrarAviso("Erro ao cadastrar material.", "erro");
  }
};

const carregarMateriais = async () => {
  try {
    const res = await api.get('/api/materiais')

    console.log("MATERIAIS RECEBIDOS:", res.data)

    materiais.value = res.data
  } catch (e) {
    mostrarAviso("Erro ao conectar com backend", "erro")
  }
}

const excluirMaterial = async (id) => {

  console.log("clicou", id);

  if (confirm("Deseja excluir?")) {

    console.log("confirmou");

    await api.delete(`${API_MATERIAIS}/${id}`);

    carregarMateriais();
  }
}

const editandoId = ref(null);
const editarMaterial = async (material) => {
  try {

    await api.put(
      `http://localhost:8080/api/materiais/${material.id}`,
      {
        nome: material.nome,
        categoria: material.categoria,
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

.nome-maiusculo {
  text-transform: uppercase;
}

/* =====================================================
   BUSCA E FILTROS
   ===================================================== */

.filtros-container {
  display: flex;
  gap: 15px;
  align-items: flex-end;
  flex-wrap: wrap;

  background-color: #f8f8f8;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 15px;
}

.campo-busca {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 2;
  min-width: 200px;
}

.campo-filtro {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  min-width: 150px;
}

.campo-busca label,
.campo-filtro label {
  font-size: 13px;
  font-weight: 600;
  color: #611811;
}

.campo-busca input,
.campo-filtro select {
  width: 100%;
  box-sizing: border-box;
  height: 40px;
}

.btn-limpar {
  background-color: #611811;
  color: white;

  border: none;
  border-radius: 4px;

  height: 40px;
  padding: 0 15px;

  cursor: pointer;
  font-weight: 600;
  white-space: nowrap;
}

.btn-limpar:hover {
  background-color: #4d0e0a;
}

.resultado-busca {
  font-size: 13px;
  color: #777;
  margin-bottom: 15px;
}

.sem-resultados {
  text-align: center;
  padding: 30px;
  color: #888;
}

@media (max-width: 700px) {
  .filtros-container {
    flex-direction: column;
    align-items: stretch;
  }

  .campo-busca,
  .campo-filtro {
    width: 100%;
    min-width: 0;
  }
}

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

/* SALVAR - VERDE */
.btn-save {
  background-color: #328767;
  color: white;
}

.btn-save:hover {
  background-color: #276d53;
}

/* EDITAR - GRAFITE */
.btn-edit {
  background-color: #444b54;
  color: white;
}

.btn-edit:hover {
  background-color: #30363d;
}

/* EXCLUIR - VERMELHO */
.btn-delete {
  background-color: #b93e47;
  color: white;
}

.btn-delete:hover {
  background-color: #982e36;
}

/* CANCELAR - CINZA */
.btn-cancel {
  background-color: #6b7280;
  color: white;
}

.btn-cancel:hover {
  background-color: #505866;
}

.btn-controle {
  border: none;
  width: 34px;
  height: 34px;
  border-radius: 6px;

  display: inline-flex;
  justify-content: center;
  align-items: center;

  cursor: pointer;
  font-size: 19px;
  font-weight: 600;
  color: white;

  transition: background-color 0.2s;
}

/* ENTRADA - VERDE */
.btn-mais {
  background-color: #328767;
}

.btn-mais:hover {
  background-color: #276d53;
}

/* SAÍDA - VINHO */
.btn-menos {
  background-color: #611811;
}

.btn-menos:hover {
  background-color: #7d251b;
}

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

.modal-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}

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
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px 24px;
  margin-bottom: 30px;
  align-items: end;
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

.obrigatorio {
  color: #d62828;
  font-weight: 700;
}

.campos-obrigatorios {
  font-size: 12px;
  color: #777;
  margin: 10px 0;
}

/* ==========================================
   FORMULÁRIO DE CADASTRO DE MATERIAIS
   ========================================== */

.form-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px 24px;
  align-items: end;
  margin-bottom: 30px;
}

.campo-cadastro {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.campo-cadastro.name-field,
.campo-cadastro.campo-numero {
  flex: initial;
  min-width: 0;
}

.campo-cadastro label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #444;
}

.campo-cadastro .input-field {
  display: block;
  width: 100%;
  height: 42px;
  box-sizing: border-box;
}

.btn-cadastro {
  grid-column: 1 / -1;
  justify-self: end;

  height: 42px;
  padding: 0 24px;

  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}

.btn-cadastro:hover {
  background-color: #36986c;
}

/* CELULAR */

@media (max-width: 700px) {
  .form-row {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .btn-cadastro {
    grid-column: 1;
    width: 100%;
  }
}

.cadastro-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.cadastro-header h3 {
  margin: 0;
}

/* ORGANIZAÇÃO DOS BOTÕES DE AÇÃO */

.acoes-material {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.coluna-acoes {
  text-align: center;
}

/* CONTROLE DE ESTOQUE */

.controle-estoque {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.quantidade-estoque {
  min-width: 32px;
  text-align: center;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}

/* ESPAÇAMENTO DOS BOTÕES DE AÇÃO */

.coluna-acoes {
  text-align: center;
  min-width: 220px;
}

.acoes-material {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

.acoes-material .btn {
  min-width: 88px;
  height: 36px;
  margin: 0;
  border-radius: 6px;
}

</style>
