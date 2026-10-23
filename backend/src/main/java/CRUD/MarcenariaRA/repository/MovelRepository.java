
package CRUD.MarcenariaRA.repository;

import CRUD.MarcenariaRA.model.Movel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovelRepository extends JpaRepository<Movel, Long> {

    // Lista somente os móveis ativos
    List<Movel> findByAtivoTrue();

    // Busca pelo nome, inclusive móveis desativados
    Optional<Movel> findByNomeIgnoreCase(String nome);

    // Verifica duplicidade ao editar,
    // ignorando o próprio móvel
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
