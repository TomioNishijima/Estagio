
package CRUD.MarcenariaRA.repository;

import CRUD.MarcenariaRA.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CategoriaRepository
        extends JpaRepository<Categoria, Long> {

    boolean existsByNomeIgnoreCaseAndAtivoTrue(String nome);

    Optional<Categoria> findByNomeIgnoreCase(String nome);

    // Listar somente categorias ativas
    List<Categoria> findByAtivoTrue();

    // Verificar se o nome já está cadastrado
    boolean existsByNomeIgnoreCase(String nome);

    // Verificar duplicidade ao editar,
    // ignorando a própria categoria
    boolean existsByNomeIgnoreCaseAndIdNot(
            String nome,
            Long id
    );
}
