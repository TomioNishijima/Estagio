
<template>
  <div class="main-container">
    <Sidebar />

    <div class="main-content">
      <header class="top-header">
        <h1>Marcenaria Nishijima 西嶋</h1>
      </header>

      <main class="page-body">
        <div class="page-heading">
          <div>
            <h2>Catálogo de Móveis 3D</h2>
            <p>
              Explore os modelos disponíveis e visualize
              os móveis em três dimensões.
            </p>
          </div>
        </div>
        
        <p v-if="carregando" class="viewer-instrucao">
          Carregando catálogo...
        </p>

        <p v-else-if="erroCatalogo" class="viewer-instrucao">
          {{ erroCatalogo }}
        </p>

        <p
          v-else-if="moveis.length === 0"
          class="viewer-instrucao"
        >
          Nenhum móvel cadastrado ainda.
        </p>
        
        
        <!-- CADASTRAR MÓVEL -->
        <section class="cadastro-movel">
          <h3>
            {{ editandoMovelId !== null
              ? 'Editar móvel'
              : 'Cadastrar novo móvel' }}
          </h3>

          <form @submit.prevent="cadastrarMovel">

            <input
              v-model="novoMovel.nome"
              type="text"
              placeholder="Nome do móvel"
              required
            />

            <input
              v-model="novoMovel.categoria"
              type="text"
              placeholder="Categoria (ex.: Cadeiras)"
              required
            />

            <textarea
              v-model="novoMovel.descricao"
              placeholder="Descrição do móvel"
              rows="3"
            ></textarea>

            
            <label for="arquivo-modelo">
              Arquivo do modelo 3D (.glb)
            </label>

            <input
              id="arquivo-modelo"
              type="file"
              accept=".glb,model/gltf-binary"
              @change="enviarModelo3D"
            />

            <p v-if="enviandoModelo">
              Enviando modelo 3D...
            </p>

            <p v-if="novoMovel.modelo">
              Modelo selecionado: {{ novoMovel.modelo }}
            </p>


            <button
              type="submit"
              :disabled="enviandoModelo"
            >
              + Cadastrar móvel
            </button>

            <button
              v-if="editandoMovelId !== null"
              type="button"
              class="btn-cancelar-edicao"
              @click="cancelarEdicao"
            >
              Cancelar edição
            </button>
          </form>

          <p v-if="mensagemCadastro">
            {{ mensagemCadastro }}
          </p>
        </section>


        <!-- CATÁLOGO -->
        <section class="catalogo-grid">
          <article
            v-for="movel in moveis"
            :key="movel.id"
            class="movel-card"
          >
            <div class="card-visual">
              <div v-if="movel.modelo" class="sem-modelo">
                <span>MODELO 3D</span>
                <small>Clique em Ver em 3D</small>
              </div>

              <div v-else class="sem-modelo">
                <span>MODELO 3D</span>
                <small>Em breve</small>
              </div>
            </div>

            <div class="card-info">
              
              <div class="acoes-gerenciar">
                <button
                  type="button"
                  class="btn-editar-movel"
                  @click="editarMovel(movel)"
                >
                  Editar
                </button>

                <button
                  type="button"
                  class="btn-desativar-movel"
                  @click="desativarMovel(movel)"
                >
                  Desativar
                </button>
              </div>

              <span class="categoria">
                {{ movel.categoria }}
              </span>
              <h3>{{ movel.nome }}</h3>
              <p>{{ movel.descricao }}</p>

              <!-- BOTÕES DO CARD -->
              <div class="card-acoes">
                <button
                  type="button"
                  class="btn-visualizar"
                  :disabled="!movel.modelo"
                  @click="visualizarMovel(movel)"
                >
                  {{ movel.modelo ? 'Ver em 3D' : 'Em breve' }}
                </button>

                <button
                  v-if="movel.modelo && suporteAR[movel.id]"
                  type="button"
                  class="btn-ar-card"
                  @click="abrirAR($event, movel)"
                >
                  📱 Ver em AR
                </button>

                <span
                  v-else-if="movel.modelo"
                  class="ar-aviso"
                >
                  📱 AR disponível em celular compatível
                </span>
              </div>
            </div>
          </article>
        </section>

        <!-- VISUALIZADOR -->
        <section v-if="movelSelecionado" class="viewer-section">
          <div class="viewer-header">
            <div>
              <span class="viewer-label">VISUALIZAÇÃO 3D</span>
              <h2>{{ movelSelecionado.nome }}</h2>
              <p>{{ movelSelecionado.descricao }}</p>
            </div>

            <button
              type="button"
              class="btn-fechar"
              @click="movelSelecionado = null"
            >
              Fechar
            </button>
          </div>

          <model-viewer
            :key="movelSelecionado.id"
            :src="movelSelecionado.modelo"
            :alt="`Visualização 3D de ${movelSelecionado.nome}`"
            camera-controls
            auto-rotate
            ar
            ar-modes="webxr scene-viewer quick-look"
            ar-placement="floor"
            shadow-intensity="1"
            exposure="1"
            interaction-prompt="auto"
            class="viewer-principal"
          >
            <button
              slot="ar-button"
              type="button"
              class="btn-ar"
            >
              📱 Ver no meu ambiente
            </button>
          </model-viewer>

          <p class="viewer-instrucao">
            Arraste para girar o móvel. Use o scroll
            do mouse ou o gesto de pinça para aproximar.
          </p>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'
import Sidebar from './Sidebar.vue'
import '@google/model-viewer'

const moveis = ref([])
const carregando = ref(true)
const erroCatalogo = ref('')
const enviandoModelo = ref(false)

const carregarMoveis = async () => {
  try {
    const resposta = await api.get('/api/moveis')

    moveis.value = resposta.data
    erroCatalogo.value = ''

  } catch (error) {
    console.error('Erro ao carregar móveis:', error)
    erroCatalogo.value = 'Erro ao carregar o catálogo.'

  } finally {
    carregando.value = false
  }
}

onMounted(carregarMoveis)

const novoMovel = ref({
  nome: '',
  categoria: '',
  descricao: '',
  modelo: ''
})

const mensagemCadastro = ref('')
const movelSelecionado = ref(null)
const suporteAR = ref({})

const editandoMovelId = ref(null)

const editarMovel = (movel) => {
  editandoMovelId.value = movel.id

  novoMovel.value = {
    nome: movel.nome || '',
    categoria: movel.categoria || '',
    descricao: movel.descricao || '',
    modelo: movel.modelo || ''
  }

  mensagemCadastro.value = ''
}

const cancelarEdicao = () => {
  editandoMovelId.value = null

  novoMovel.value = {
    nome: '',
    categoria: '',
    descricao: '',
    modelo: ''
  }
}

function visualizarMovel(movel) {
  if (!movel.modelo) return

  movelSelecionado.value = movel
}

function abrirAR(event, movel) {
  if (!movel.modelo) return

  const card = event.currentTarget.closest('.movel-card')
  const viewer = card?.querySelector('model-viewer')

  if (viewer?.canActivateAR) {
    viewer.activateAR()
  } else {
    visualizarMovel(movel)
  }
}

async function verificarSuporteAR(event, id) {
  const viewer = event.currentTarget

  await viewer.updateComplete

  suporteAR.value[id] = viewer.canActivateAR === true
}



const cadastrarMovel = async () => {
  if (enviandoModelo.value) return
  if (!novoMovel.value.nome.trim() ||
      !novoMovel.value.categoria.trim()) {
    mensagemCadastro.value = 'Preencha nome e categoria.'
    return
  }

  const dados = {
    nome: novoMovel.value.nome.trim(),
    categoria: novoMovel.value.categoria.trim(),
    descricao: novoMovel.value.descricao.trim(),
    modelo: novoMovel.value.modelo.trim() || null
  }

  const eraEdicao = editandoMovelId.value !== null

  try {
    let resposta

    if (eraEdicao) {
      resposta = await api.put(
        `/api/moveis/${editandoMovelId.value}`,
        dados
      )
    } else {
      resposta = await api.post('/api/moveis', dados)
    }

    mensagemCadastro.value = eraEdicao
      ? 'Móvel atualizado com sucesso!'
      : resposta.status === 200
        ? 'Móvel reativado com sucesso!'
        : 'Móvel cadastrado com sucesso!'

    cancelarEdicao()

    await carregarMoveis()

    // Atualiza o visualizador se o móvel editado estiver aberto
    if (movelSelecionado.value) {
      movelSelecionado.value =
        moveis.value.find(
          m => m.id === movelSelecionado.value.id
        ) || null
    }

  } catch (error) {
    mensagemCadastro.value =
      error.response?.data?.message ||
      'Erro ao salvar móvel.'

    console.error(error)
  }
}


const desativarMovel = async (movel) => {
  const confirmou = confirm(
    `Deseja desativar o móvel "${movel.nome}"?`
  )

  if (!confirmou) return

  try {
    await api.delete(`/api/moveis/${movel.id}`)

    if (movelSelecionado.value?.id === movel.id) {
      movelSelecionado.value = null
    }

    if (editandoMovelId.value === movel.id) {
      cancelarEdicao()
    }

    await carregarMoveis()

    mensagemCadastro.value = 'Móvel desativado com sucesso!'

  } catch (error) {
    mensagemCadastro.value =
      error.response?.data?.message ||
      'Erro ao desativar móvel.'

    console.error(error)
  }
}


const enviarModelo3D = async (event) => {
  const arquivo = event.target.files?.[0]

  if (!arquivo) return

  if (!arquivo.name.toLowerCase().endsWith('.glb')) {
    mensagemCadastro.value = 'Selecione um arquivo .glb.'
    event.target.value = ''
    return
  }

  if (arquivo.size > 25 * 1024 * 1024) {
    mensagemCadastro.value = 'O arquivo deve ter no máximo 25 MB.'
    event.target.value = ''
    return
  }

  enviandoModelo.value = true
  mensagemCadastro.value = ''

  try {
    const dados = new FormData()
    dados.append('arquivo', arquivo)

    const resposta = await api.post(
      '/api/modelos/upload',
      dados
    )

    // Guarda automaticamente o caminho retornado pelo backend
    novoMovel.value.modelo = resposta.data.modelo

    mensagemCadastro.value =
      'Modelo 3D enviado! Agora salve o móvel.'

  } catch (error) {
    console.error('Erro no upload:', error)

    mensagemCadastro.value =
      error.response?.data?.message ||
      'Erro ao enviar o modelo 3D.'

  } finally {
    enviandoModelo.value = false
    event.target.value = ''
  }
}


</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Lexend:wght@300;400;500;600;700&display=swap');

* {
  box-sizing: border-box;
  font-family: 'Lexend', sans-serif;
}

/* BOTÕES DOS CARDS */

.card-acoes {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-top: 15px;
}

.card-acoes .btn-visualizar {
  margin-top: 0;
}

.btn-ar-card {
  width: 100%;
  min-height: 42px;
  border: none;
  border-radius: 6px;

  background: #611811;
  color: white;

  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-ar-card:hover:not(:disabled) {
  background: #7d251b;
}

.btn-ar-card:disabled {
  background: #b9b9b9;
  cursor: not-allowed;
}

/* Esconde o botão AR padrão dentro da miniatura */
.mini-viewer::part(default-ar-button) {
  display: none;
}

@media (max-width: 400px) {
  .card-acoes {
    grid-template-columns: 1fr;
  }
}

/* BOTÃO DE REALIDADE AUMENTADA */

.btn-ar {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);

  padding: 14px 24px;
  border: none;
  border-radius: 8px;

  background: #611811;
  color: white;

  font-family: 'Lexend', sans-serif;
  font-size: 13px;
  font-weight: 600;

  cursor: pointer;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.25);
  white-space: nowrap;
}

.btn-ar:hover {
  background: #7d251b;
}

.main-container {
  display: flex;
  height: 100vh;
  background: #1a1a1a;
  color: #333;
}

.main-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.top-header {
  background: #611811;
  color: white;
  min-height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px;
}

.top-header h1 {
  font-size: 22px;
  font-weight: 400;
  letter-spacing: 1px;
  text-align: center;
}

.page-body {
  padding: 30px;
}

.page-heading {
  margin-bottom: 25px;
}

.page-heading h2 {
  margin: 0 0 8px;
  color: white;
  font-size: 25px;
  font-weight: 600;
}

.page-heading p {
  color: #aaa;
  font-size: 13px;
  line-height: 1.6;
}

/* CATÁLOGO */

.catalogo-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 22px;
}

.movel-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.25);
  transition: transform 0.2s;
}

.movel-card:hover {
  transform: translateY(-3px);
}

.card-visual {
  height: 220px;
  background: #f1eeeb;
}

.mini-viewer {
  display: block;
  width: 100%;
  height: 100%;
}

.sem-modelo {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 10px;
  color: #777;
}

.sem-modelo span {
  font-size: 14px;
  letter-spacing: 2px;
  font-weight: 600;
}

.sem-modelo small {
  font-size: 12px;
  color: #999;
}

.card-info {
  padding: 20px;
}

.categoria {
  display: inline-block;
  color: #611811;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.card-info h3 {
  font-size: 17px;
  margin: 10px 0;
  color: #333;
}

.card-info p {
  color: #777;
  font-size: 12px;
  line-height: 1.6;
  min-height: 40px;
}

.btn-visualizar {
  width: 100%;
  height: 42px;
  margin-top: 15px;
  border: none;
  border-radius: 6px;
  background: #444b54;
  color: white;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-visualizar:hover:not(:disabled) {
  background: #30363d;
}

.btn-visualizar:disabled {
  background: #b9b9b9;
  cursor: not-allowed;
}

/* VISUALIZADOR PRINCIPAL */

.viewer-section {
  margin-top: 30px;
  padding: 25px;
  background: white;
  border-radius: 8px;
}

.viewer-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 20px;
}

.viewer-label {
  color: #611811;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 2px;
}

.viewer-header h2 {
  color: #333;
  font-size: 22px;
  margin: 10px 0;
}

.viewer-header p {
  color: #777;
  font-size: 13px;
}

.btn-fechar {
  background: #611811;
  color: white;
  border: none;
  padding: 11px 20px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}

.btn-fechar:hover {
  background: #7d251b;
}

.viewer-principal {
  display: block;
  width: 100%;
  height: 480px;
  background: #f3f1ef;
  border-radius: 8px;
}

.viewer-instrucao {
  text-align: center;
  margin: 15px 0 0;
  color: #777;
  font-size: 12px;
  line-height: 1.6;
}

/* RESPONSIVIDADE */

@media (max-width: 1100px) {
  .catalogo-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 700px) {
  .page-body {
    padding: 18px;
  }

  .catalogo-grid {
    grid-template-columns: 1fr;
  }

  .viewer-header {
    flex-direction: column;
  }

  .viewer-principal {
    height: 350px;
  }

  .viewer-section {
    padding: 15px;
  }
}


.cadastro-movel {
  background: white;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 30px;
}

.cadastro-movel h3 {
  color: #611811;
  margin: 0 0 18px;
}

.cadastro-movel form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cadastro-movel input,
.cadastro-movel textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font: inherit;
}

.cadastro-movel button {
  background: #611811;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}

.cadastro-movel p {
  margin-top: 12px;
  color: #611811;
}


.acoes-gerenciar {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}

.btn-editar-movel,
.btn-desativar-movel {
  flex: 1;
  border: none;
  border-radius: 6px;
  padding: 10px;
  color: white;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}

.btn-editar-movel {
  background: #444b54;
}

.btn-desativar-movel {
  background: #611811;
}

.cadastro-movel .btn-cancelar-edicao {
  background: #777;
  color: white;
}


</style>
