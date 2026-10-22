<template>
  <button 
    class="menu-mobile"
    @click="aberto = !aberto"
  >
    ☰
  </button>

  <aside 
    class="sidebar"
    :class="{ aberto }"
  >
    <nav class="sidebar-nav">
      <ul>
        <li>
          <RouterLink to="/dashboard">
            Dashboard de Estoque
          </RouterLink>
        </li>

        <li>
          <RouterLink to="/materiais">
            Gestão de Inventário
          </RouterLink>
        </li>

        <li>
          <RouterLink to="/catalogo">
            Catálogo 3D e Realidade Aumentada
          </RouterLink>
        </li>



        <li>
          <RouterLink to="/orcamentos">
            Gerador de Orçamentos
          </RouterLink>
        </li>
      </ul>
    </nav>
  </aside>
</template>


<script setup>
import { ref } from 'vue'

const aberto = ref(false)
</script>


<style scoped>

.sidebar {
  width: 260px;
  height: 100vh;

  background-color: #e0e0e0;
  border-right: 1px solid #ccc;

  display: flex;
  flex-direction: column;

  transition: transform .3s ease;
}


.sidebar-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}


.sidebar-nav li {
  border-bottom: 1px solid #d0d0d0;
}


.sidebar-nav a {
  display: block;

  padding: 18px 25px;

  color: #611811;

  font-weight: 600;
  font-size: 14px;

  text-decoration: none;
}


.sidebar-nav a.router-link-active {
  background-color: #d0d0d0;
  box-shadow: inset 4px 0 0 #611811;
}


/* BOTÃO MOBILE */

.menu-mobile {
  display: none;

  position: fixed;

  top: 15px;
  left: 15px;

  z-index: 1000;

  background: #611811;
  color: white;

  border: none;

  width: 45px;
  height: 45px;

  border-radius: 8px;

  font-size: 24px;
}


/* CELULAR */

@media (max-width: 700px) {

  .menu-mobile {
    display: block;
  }


  .sidebar {

    position: fixed;

    top: 0;
    left: 0;

    z-index: 999;

    transform: translateX(-100%);
  }


  .sidebar.aberto {

    transform: translateX(0);
  }

}

</style>