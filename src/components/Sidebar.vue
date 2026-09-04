<template>
  <aside class="sidebar">
    <div class="sidebar-logo">
    </div>

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
            Catálogo de Projetos/Produtos
          </RouterLink>
        </li>

        <li>
          <RouterLink to="/realidade-aumentada">
            Visualizador de Realidade Aumentada
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

<style scoped>
.sidebar {
  width: 260px;
  background-color: #e0e0e0;
  border-right: 1px solid #ccc;
  display: flex;
  flex-direction: column;
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
  cursor: pointer;
  font-size: 14px;
  text-decoration: none;
}

.sidebar-nav a.router-link-active {
  background-color: #d0d0d0;
  box-shadow: inset 4px 0 0 #611811;
}
</style>