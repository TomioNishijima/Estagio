package CRUD.MarcenariaRA.controller;

import CRUD.MarcenariaRA.model.Material;
import CRUD.MarcenariaRA.model.Movimentacao;
import CRUD.MarcenariaRA.repository.MaterialRepository;
import CRUD.MarcenariaRA.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/movimentacoes")
@CrossOrigin("*")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private MaterialRepository materialRepository;


    @PostMapping
    @Transactional
    public Movimentacao registrar(@RequestBody Movimentacao movimentacao) {

        // Valida se foi informado um material
        if (movimentacao == null ||
                movimentacao.getMaterial() == null ||
                movimentacao.getMaterial().getId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecione um material válido."
            );
        }

        // Valida a quantidade
        if (movimentacao.getQuantidade() == null ||
                movimentacao.getQuantidade() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade deve ser maior que zero."
            );
        }

        // Valida o tipo da movimentação
        String tipo = movimentacao.getTipo();

        if (!"ENTRADA".equals(tipo) && !"SAIDA".equals(tipo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de movimentação inválido."
            );
        }

        // Busca o material
        Material material = materialRepository
                .findById(movimentacao.getMaterial().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Material não encontrado."
                ));

        // Impede movimentação de material desativado
        if (!Boolean.TRUE.equals(material.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível movimentar um material desativado."
            );
        }

        if ("ENTRADA".equals(tipo)) {

            material.setQuantidade(
                    material.getQuantidade() + movimentacao.getQuantidade()
            );

        } else {

            // Verifica estoque antes de registrar a saída
            if (material.getQuantidade() < movimentacao.getQuantidade()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Estoque insuficiente."
                );
            }

            material.setQuantidade(
                    material.getQuantidade() - movimentacao.getQuantidade()
            );
        }

        movimentacao.setMaterial(material);

        materialRepository.save(material);

        return movimentacaoRepository.save(movimentacao);
    }

}