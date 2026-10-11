package CRUD.MarcenariaRA.controller;

import CRUD.MarcenariaRA.model.Material;
import CRUD.MarcenariaRA.model.Movimentacao;
import CRUD.MarcenariaRA.repository.MaterialRepository;
import CRUD.MarcenariaRA.repository.MovimentacaoRepository;
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
        Material material = materialRepository.findById(movimentacao.getMaterial().getId())
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        if (movimentacao.getQuantidade() <= 0) {
            throw new RuntimeException(
                    "A quantidade deve ser maior que zero."
            );
        }
        if ("ENTRADA".equals(movimentacao.getTipo())) {

            material.setQuantidade(
                    material.getQuantidade() + movimentacao.getQuantidade()
            );

        } else if ("SAIDA".equals(movimentacao.getTipo())) {

            if (material.getQuantidade() < movimentacao.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente.");
            }

            material.setQuantidade(
                    material.getQuantidade() - movimentacao.getQuantidade()
            );
        }

        materialRepository.save(material);
        return movimentacaoRepository.save(movimentacao);
    }
}