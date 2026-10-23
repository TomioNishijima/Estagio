package CRUD.MarcenariaRA.controller;
import CRUD.MarcenariaRA.model.Material;
import CRUD.MarcenariaRA.repository.MaterialRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import CRUD.MarcenariaRA.repository.CategoriaRepository;
import java.util.List;
import java.text.Normalizer;
import java.util.Locale;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/materiais")
@CrossOrigin("*")
public class MaterialController {

    @Autowired
    private MaterialRepository repository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private String padronizarNome(String nome) {
        if (nome == null) {
            return "";
        }

        return nome
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }

    private String normalizarParaComparacao(String nome) {
        String padronizado = padronizarNome(nome);

        return Normalizer
                .normalize(padronizado, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private boolean nomeDuplicado(String nome, Long idIgnorado) {
        String nomeNormalizado = normalizarParaComparacao(nome);

        return repository.findByAtivoTrue()
                .stream()
                .filter(material ->
                        idIgnorado == null ||
                                !material.getId().equals(idIgnorado)
                )
                .anyMatch(material ->
                        normalizarParaComparacao(material.getNome())
                                .equals(nomeNormalizado)
                );
    }

    @GetMapping
    public List<Material> listar() {
        return repository.findByAtivoTrue();
    }

    @PostMapping
    public ResponseEntity<Material> salvar(@Valid @RequestBody Material material) {

        String nomePadronizado = padronizarNome(material.getNome());

        if (nomePadronizado.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome do material é obrigatório."
            );
        }

        if (nomeDuplicado(nomePadronizado, null)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um material com esse nome."
            );
        }


        // Procura um material desativado com o mesmo nome
        Material materialInativo = repository.findAll()
                .stream()
                .filter(m -> Boolean.FALSE.equals(m.getAtivo()))
                .filter(m ->
                        normalizarParaComparacao(m.getNome())
                                .equals(normalizarParaComparacao(nomePadronizado))
                )
                .findFirst()
                .orElse(null);

// Se encontrou, reativa o registro antigo
        if (materialInativo != null) {

            materialInativo.setAtivo(true);

            return ResponseEntity.ok(
                    repository.save(materialInativo)
            );
        }


        material.setNome(nomePadronizado);

        material.setCategoria(
                validarCategoriaAtiva(material.getCategoria())
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repository.save(material));
    }

    @DeleteMapping("/{id}")
    public Material excluir(@PathVariable Long id) {

        Material material = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        material.setAtivo(false);

        return repository.save(material);
    }

    @PutMapping("/{id}")
    public Material editar(
            @PathVariable Long id,
            @RequestBody Material materialEditado
    ) {

        Material material = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Material não encontrado."
                ));

        String nomePadronizado = padronizarNome(materialEditado.getNome());

        if (nomePadronizado.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome do material é obrigatório."
            );
        }

        if (nomeDuplicado(nomePadronizado, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um material com esse nome."
            );
        }

        material.setNome(nomePadronizado);
        String categoriaRecebida = materialEditado.getCategoria();

        if (categoriaRecebida == null || categoriaRecebida.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecione uma categoria."
            );
        }

// Se a categoria não mudou, preserva a antiga.
        if (!padronizarNome(categoriaRecebida)
                .equals(padronizarNome(material.getCategoria()))) {

            // Se mudou, a nova categoria precisa estar ativa.
            material.setCategoria(
                    validarCategoriaAtiva(categoriaRecebida)
            );
        }
        material.setValor(materialEditado.getValor());

        return repository.save(material);
    }


    private String validarCategoriaAtiva(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecione uma categoria."
            );
        }

        String categoriaPadronizada = nome.trim()
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);

        if (!categoriaRepository
                .existsByNomeIgnoreCaseAndAtivoTrue(categoriaPadronizada)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Categoria inexistente ou desativada."
            );
        }

        return categoriaPadronizada;
    }

}