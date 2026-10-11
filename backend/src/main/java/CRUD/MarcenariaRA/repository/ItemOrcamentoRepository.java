package CRUD.MarcenariaRA.repository;

import CRUD.MarcenariaRA.model.ItemOrcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemOrcamentoRepository
        extends JpaRepository<ItemOrcamento, Long> {

    List<ItemOrcamento> findByOrcamentoId(Long orcamentoId);

    @Transactional
    void deleteByOrcamentoId(Long orcamentoId);
}