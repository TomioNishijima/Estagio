package CRUD.MarcenariaRA.controller;
import CRUD.MarcenariaRA.model.Material;
import CRUD.MarcenariaRA.repository.MaterialRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/materiais")
@CrossOrigin("*")
public class MaterialController {

    @Autowired
    private MaterialRepository repository;

    @GetMapping
    public List<Material> listar() {
        return repository.findByAtivoTrue();
    }

    @PostMapping
    public Material salvar(@Valid @RequestBody Material material) {

        if (repository.existsByNomeIgnoreCaseAndAtivoTrue(material.getNome())) {
            throw new RuntimeException("Já existe um material com esse nome.");
        }

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
    public Material editar(@PathVariable Long id,
                           @RequestBody Material materialEditado) {

        Material material = repository.findById(id).orElseThrow(() -> new RuntimeException(
                "Material não encontrado!"));

        boolean nomeExiste = repository.existsByNomeIgnoreCaseAndAtivoTrue(materialEditado.getNome());

        if (nomeExiste && !material.getNome().equalsIgnoreCase(materialEditado.getNome())) {
            throw new RuntimeException(
                    "Já existe um material com esse nome.");
        }

        material.setNome(materialEditado.getNome());
        material.setValor(materialEditado.getValor());

        return repository.save(material);
    }
}