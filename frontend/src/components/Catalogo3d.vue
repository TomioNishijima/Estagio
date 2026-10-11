
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

        <!-- CATÁLOGO -->
        <section class="catalogo-grid">
          <article
            v-for="movel in moveis"
            :key="movel.id"
            class="movel-card"
          >
            <div class="card-visual">
              <model-viewer
                v-if="movel.modelo"
                :src="movel.modelo"
                :alt="`Modelo 3D de ${movel.nome}`"
                camera-controls
                auto-rotate
                shadow-intensity="1"
                interaction-prompt="none"
                class="mini-viewer"
              ></model-viewer>

              <div v-else class="sem-modelo">
                <span>MODELO 3D</span>
                <small>Em breve</small>
              </div>
            </div>

            <div class="card-info">
              <span class="categoria">
                {{ movel.categoria }}
              </span>

              <h3>{{ movel.nome }}</h3>
              <p>{{ movel.descricao }}</p>

              <button
                type="button"
                class="btn-visualizar"
                :disabled="!movel.modelo"
                @click="visualizarMovel(movel)"
              >
                {{ movel.modelo ? 'Visualizar em 3D' : 'Em breve' }}
              </button>
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
            shadow-intensity="1"
            exposure="1"
            interaction-prompt="auto"
            class="viewer-principal"
          ></model-viewer>

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
import { ref } from 'vue'
import Sidebar from './Sidebar.vue'
import '@google/model-viewer'

const moveis = ref([
  {
    id: 1,
    nome: 'Cadeira 3D',
    categoria: 'Cadeiras',
    descricao: 'Modelo demonstrativo para testar a visualização 3D.',
    modelo: '/modelos/cadeira.glb'
  },
  {
    id: 2,
    nome: 'Armário Planejado',
    categoria: 'Armários',
    descricao: 'Modelo de armário planejado.',
    modelo: null
  },
  {
    id: 3,
    nome: 'Mesa de Escritório',
    categoria: 'Mesas',
    descricao: 'Modelo de mesa para escritório.',
    modelo: null
  }
])

const movelSelecionado = ref(null)

function visualizarMovel(movel) {
  if (!movel.modelo) return

  movelSelecionado.value = movel
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Lexend:wght@300;400;500;600;700&display=swap');

* {
  box-sizing: border-box;
  font-family: 'Lexend', sans-serif;
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
</style>
