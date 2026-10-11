package CRUD.MarcenariaRA.repository;

import CRUD.MarcenariaRA.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrcamentoRepository
        extends JpaRepository<Orcamento, Long> {

    List<Orcamento> findByExcluidoFalse();
}