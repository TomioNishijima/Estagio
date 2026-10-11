package CRUD.MarcenariaRA.controller;
import CRUD.MarcenariaRA.model.Material;
import CRUD.MarcenariaRA.repository.MaterialRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.text.Normalizer;
import java.util.Locale;

@RestController
@RequestMapping("/api/materiais")
@CrossOrigin("*")
public class MaterialController {

    @Autowired
    private MaterialRepository repository;

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
    public Material salvar(@Valid @RequestBody Material material) {

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

        material.setNome(nomePadronizado);

        return repository.save(material);
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
        material.setCategoria(materialEditado.getCategoria());
        material.setValor(materialEditado.getValor());

        return repository.save(material);
    }
}