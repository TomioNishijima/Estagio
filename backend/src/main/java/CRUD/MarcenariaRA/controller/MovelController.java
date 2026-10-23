
package CRUD.MarcenariaRA.controller;

import CRUD.MarcenariaRA.model.Movel;
import CRUD.MarcenariaRA.repository.MovelRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/moveis")
public class MovelController {

    private final MovelRepository repository;

    public MovelController(MovelRepository repository) {
        this.repository = repository;
    }

    private String padronizar(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.trim()
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }

    // 1. GET - LISTAR MÓVEIS ATIVOS
    @GetMapping
    public List<Movel> listar() {
        return repository.findByAtivoTrue();
    }

    // 2. POST - CADASTRAR OU REATIVAR MÓVEL
    @PostMapping
    public ResponseEntity<Movel> cadastrar(
            @RequestBody Movel dados) {

        String nome = padronizar(dados.getNome());
        String categoria = padronizar(dados.getCategoria());

        if (nome.isBlank() || categoria.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nome e categoria são obrigatórios."
            );
        }

        Movel existente = repository
                .findByNomeIgnoreCase(nome)
                .orElse(null);

        if (existente != null) {

            if (Boolean.TRUE.equals(existente.getAtivo())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Já existe um móvel ativo com esse nome."
                );
            }

            // Reativa preservando os dados anteriores
            existente.setAtivo(true);

            return ResponseEntity.ok(
                    repository.save(existente)
            );
        }

        Movel movel = new Movel();
        movel.setNome(nome);
        movel.setCategoria(categoria);
        movel.setDescricao(dados.getDescricao());
        movel.setModelo(dados.getModelo());
        movel.setAtivo(true);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repository.save(movel));
    }

    // 3. PUT - EDITAR MÓVEL
    @PutMapping("/{id}")
    public ResponseEntity<Movel> editar(
            @PathVariable Long id,
            @RequestBody Movel dados) {

        Movel movel = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Móvel não encontrado."
                ));

        if (!Boolean.TRUE.equals(movel.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível editar um móvel desativado."
            );
        }

        String nome = padronizar(dados.getNome());
        String categoria = padronizar(dados.getCategoria());

        if (nome.isBlank() || categoria.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nome e categoria são obrigatórios."
            );
        }

        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe outro móvel com esse nome."
            );
        }

        movel.setNome(nome);
        movel.setCategoria(categoria);
        movel.setDescricao(dados.getDescricao());
        movel.setModelo(dados.getModelo());

        return ResponseEntity.ok(repository.save(movel));
    }

    // 4. DELETE - SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Movel> excluir(
            @PathVariable Long id) {

        Movel movel = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Móvel não encontrado."
                ));

        if (!Boolean.TRUE.equals(movel.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este móvel já está desativado."
            );
        }

        // Mantém o registro e o arquivo 3D
        movel.setAtivo(false);

        return ResponseEntity.ok(repository.save(movel));
    }
}
