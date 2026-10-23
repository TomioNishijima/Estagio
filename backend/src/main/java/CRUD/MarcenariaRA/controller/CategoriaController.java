
package CRUD.MarcenariaRA.controller;

import CRUD.MarcenariaRA.model.Categoria;
import CRUD.MarcenariaRA.repository.CategoriaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository repository;

    public CategoriaController(CategoriaRepository repository) {
        this.repository = repository;
    }

    // Padronizar o nome da categoria
    private String padronizarNome(String nome) {
        if (nome == null) {
            return "";
        }

        return nome.trim()
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }

    // 1. GET - LISTAR CATEGORIAS ATIVAS
    @GetMapping
    public List<Categoria> listar() {
        return repository.findByAtivoTrue();
    }

    // 2. POST - CADASTRAR CATEGORIA
    @PostMapping
    public ResponseEntity<Categoria> cadastrar(
            @RequestBody Categoria dados) {

        String nome = padronizarNome(dados.getNome());

        if (nome.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da categoria é obrigatório."
            );
        }


        Categoria existente = repository
                .findByNomeIgnoreCase(nome)
                .orElse(null);

        if (existente != null) {

            // Se já está ativa, continua sendo duplicidade.
            if (Boolean.TRUE.equals(existente.getAtivo())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Já existe uma categoria ativa com esse nome."
                );
            }

            // Se estava desativada, reativa o registro antigo.
            existente.setAtivo(true);

            Categoria reativada = repository.save(existente);

            return ResponseEntity.ok(reativada);
        }


        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setAtivo(true);

        Categoria salva = repository.save(categoria);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(salva);
    }

    // 3. PUT - EDITAR CATEGORIA
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> editar(
            @PathVariable Long id,
            @RequestBody Categoria dados) {

        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoria não encontrada."
                ));

        if (!Boolean.TRUE.equals(categoria.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível editar uma categoria desativada."
            );
        }

        String nome = padronizarNome(dados.getNome());

        if (nome.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da categoria é obrigatório."
            );
        }

        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma categoria com esse nome."
            );
        }

        categoria.setNome(nome);

        Categoria atualizada = repository.save(categoria);

        return ResponseEntity.ok(atualizada);
    }

    // 4. DELETE - SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Categoria> excluir(
            @PathVariable Long id) {

        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoria não encontrada."
                ));

        if (!Boolean.TRUE.equals(categoria.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta categoria já está desativada."
            );
        }

        // Não exclui fisicamente do banco!
        categoria.setAtivo(false);

        Categoria atualizada = repository.save(categoria);

        return ResponseEntity.ok(atualizada);
    }
}
