package CRUD.demo.controller;

import CRUD.demo.model.Material;
import CRUD.demo.model.Movimentacao;
import CRUD.demo.repository.MaterialRepository;
import CRUD.demo.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movimentacoes")
@CrossOrigin("*")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @PostMapping
    public Movimentacao registrar(@RequestBody Movimentacao movimentacao) {
        // 1. Busca o material no banco
        Material material = materialRepository.findById(movimentacao.getMaterial().getId())
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        // 2. Atualiza a quantidade do material baseada no tipo
        if ("ENTRADA".equals(movimentacao.getTipo())) {
            material.setQuantidade(material.getQuantidade() + movimentacao.getQuantidade());
        } else if ("SAIDA".equals(movimentacao.getTipo())) {
            material.setQuantidade(material.getQuantidade() - movimentacao.getQuantidade());
        }

        // 3. Salva o material atualizado e a movimentação
        materialRepository.save(material);
        return movimentacaoRepository.save(movimentacao);
    }
}