package CRUD.demo.controller;

import CRUD.demo.model.Material;
import CRUD.demo.repository.MaterialRepository;
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
        return repository.findAll();
    }

    // Mantivemos apenas UM PostMapping, com o @Valid para ativar as mensagens
    @PostMapping
    public Material salvar(@Valid @RequestBody Material material) {
        return repository.save(material);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        repository.deleteById(id);
    }
}