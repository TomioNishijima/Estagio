package CRUD.MarcenariaRA.controller;

import CRUD.MarcenariaRA.model.ItemOrcamento;
import CRUD.MarcenariaRA.model.Material;
import CRUD.MarcenariaRA.model.Orcamento;
import CRUD.MarcenariaRA.repository.ItemOrcamentoRepository;
import CRUD.MarcenariaRA.repository.MaterialRepository;
import CRUD.MarcenariaRA.repository.OrcamentoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orcamentos")
@CrossOrigin(origins = "http://localhost:5173")
public class OrcamentoController {

    private final OrcamentoRepository orcamentoRepository;
    private final ItemOrcamentoRepository itemOrcamentoRepository;
    private final MaterialRepository materialRepository;

    public OrcamentoController(
            OrcamentoRepository orcamentoRepository,
            ItemOrcamentoRepository itemOrcamentoRepository,
            MaterialRepository materialRepository) {

        this.orcamentoRepository = orcamentoRepository;
        this.itemOrcamentoRepository = itemOrcamentoRepository;
        this.materialRepository = materialRepository;
    }


    // =====================================================
    // 1. VER ORÇAMENTOS
    // =====================================================

    @GetMapping
    public ResponseEntity<?> listarOrcamentos() {

        List<Orcamento> orcamentos =
                orcamentoRepository.findByExcluidoFalse();

        return ResponseEntity.ok(orcamentos);
    }


    // =====================================================
    // 2. VER ITENS DE UM ORÇAMENTO
    // =====================================================

    @GetMapping("/{id}/itens")
    public ResponseEntity<?> listarItens(
            @PathVariable Long id) {

        Orcamento orcamento =
                orcamentoRepository.findById(id).orElse(null);

        if (orcamento == null || orcamento.isExcluido()) {

            return ResponseEntity
                    .badRequest()
                    .body("Orçamento não encontrado.");
        }

        List<ItemOrcamento> itens =
                itemOrcamentoRepository
                        .findByOrcamentoId(id);

        return ResponseEntity.ok(itens);
    }


    // =====================================================
    // 3. CRIAR ORÇAMENTO
    // =====================================================

    @PostMapping
    @Transactional
    public ResponseEntity<?> criarOrcamento(
            @RequestBody OrcamentoRequest request) {

        try {

            // Verifica o nome

            if (request.nome == null ||
                    request.nome.trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("O nome do orçamento é obrigatório.");
            }


            // Verifica se existem itens

            if (request.itens == null ||
                    request.itens.isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "O orçamento precisa ter pelo menos um material."
                        );
            }


            // Cria o orçamento

            Orcamento orcamento =
                    new Orcamento(request.nome);

            double custoFinal = 0;


            /*
             * Salvamos primeiro o orçamento.
             *
             * Isso garante que ele tenha um ID
             * antes dos ItemOrcamento serem salvos.
             */

            orcamentoRepository.save(orcamento);


            // Processa cada material

            for (ItemRequest itemRequest : request.itens) {

                if (itemRequest.materialId == null) {

                    return ResponseEntity
                            .badRequest()
                            .body("Material inválido.");
                }


                if (itemRequest.quantidade == null ||
                        itemRequest.quantidade <= 0) {

                    return ResponseEntity
                            .badRequest()
                            .body(
                                    "A quantidade deve ser maior que zero."
                            );
                }


                // Busca o material no banco

                Material material =
                        materialRepository
                                .findById(itemRequest.materialId)
                                .orElse(null);


                if (material == null) {

                    return ResponseEntity
                            .badRequest()
                            .body(
                                    "Material não encontrado: "
                                            + itemRequest.materialId
                            );
                }


                // Pega o valor atual do material

                double valorUnitario =
                        material.getValor();


                // Calcula o subtotal

                double subtotal =
                        itemRequest.quantidade
                                * valorUnitario;


                // Cria o item

                ItemOrcamento item =
                        new ItemOrcamento(
                                orcamento,
                                material,
                                itemRequest.quantidade,
                                valorUnitario
                        );


                item.setSubtotal(subtotal);


                // Soma ao custo final

                custoFinal += subtotal;


                // Salva o item

                itemOrcamentoRepository.save(item);
            }


            // Atualiza o custo final

            orcamento.setCustoFinal(custoFinal);

            orcamentoRepository.save(orcamento);


            return ResponseEntity.ok(orcamento);


        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Erro ao criar orçamento: "
                                    + e.getMessage()
                    );
        }
    }


    // =====================================================
    // 4. EDITAR ORÇAMENTO
    // =====================================================

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> editarOrcamento(
            @PathVariable Long id,
            @RequestBody OrcamentoRequest request) {

        try {

            Orcamento orcamento =
                    orcamentoRepository
                            .findById(id)
                            .orElse(null);


            if (orcamento == null ||
                    orcamento.isExcluido()) {

                return ResponseEntity
                        .badRequest()
                        .body("Orçamento não encontrado.");
            }


            if (request.nome == null ||
                    request.nome.trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "O nome do orçamento é obrigatório."
                        );
            }


            if (request.itens == null ||
                    request.itens.isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "O orçamento precisa ter pelo menos um material."
                        );
            }


            // Atualiza o nome

            orcamento.setNome(request.nome);


            /*
             * Remove os itens antigos.
             *
             * Como estamos reconstruindo a composição
             * do orçamento, podemos apagar apenas
             * os itens, não o orçamento.
             */

            itemOrcamentoRepository
                    .deleteByOrcamentoId(id);


            double custoFinal = 0;


            // Cria novamente os itens

            for (ItemRequest itemRequest : request.itens) {

                if (itemRequest.materialId == null) {

                    return ResponseEntity
                            .badRequest()
                            .body("Material inválido.");
                }


                if (itemRequest.quantidade == null ||
                        itemRequest.quantidade <= 0) {

                    return ResponseEntity
                            .badRequest()
                            .body(
                                    "A quantidade deve ser maior que zero."
                            );
                }


                Material material =
                        materialRepository
                                .findById(itemRequest.materialId)
                                .orElse(null);


                if (material == null) {

                    return ResponseEntity
                            .badRequest()
                            .body(
                                    "Material não encontrado: "
                                            + itemRequest.materialId
                            );
                }


                double valorUnitario =
                        material.getValor();


                double subtotal =
                        itemRequest.quantidade
                                * valorUnitario;


                ItemOrcamento item =
                        new ItemOrcamento(
                                orcamento,
                                material,
                                itemRequest.quantidade,
                                valorUnitario
                        );


                item.setSubtotal(subtotal);


                custoFinal += subtotal;


                itemOrcamentoRepository.save(item);
            }


            // Atualiza o custo final

            orcamento.setCustoFinal(custoFinal);

            orcamentoRepository.save(orcamento);


            return ResponseEntity.ok(orcamento);


        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Erro ao editar orçamento: "
                                    + e.getMessage()
                    );
        }
    }


    // =====================================================
    // 5. EXCLUIR ORÇAMENTO — SOFT DELETE
    // =====================================================

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> excluirOrcamento(
            @PathVariable Long id) {

        Orcamento orcamento =
                orcamentoRepository
                        .findById(id)
                        .orElse(null);


        if (orcamento == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Orçamento não encontrado.");
        }


        if (orcamento.isExcluido()) {

            return ResponseEntity
                    .badRequest()
                    .body("O orçamento já foi excluído.");
        }


        /*
         * SOFT DELETE
         *
         * Não fazemos:
         *
         * orcamentoRepository.delete(...)
         *
         * Apenas marcamos como excluído.
         */

        orcamento.setExcluido(true);

        orcamentoRepository.save(orcamento);


        return ResponseEntity.ok(
                "Orçamento excluído com sucesso."
        );
    }


    // =====================================================
    // DTO DO ORÇAMENTO
    // =====================================================

    public static class OrcamentoRequest {

        public String nome;

        public List<ItemRequest> itens;
    }


    // =====================================================
    // DTO DOS ITENS
    // =====================================================

    public static class ItemRequest {

        public Long materialId;

        public Double quantidade;
    }
}