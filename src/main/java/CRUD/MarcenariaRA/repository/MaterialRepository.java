package CRUD.MarcenariaRA.repository;

import CRUD.MarcenariaRA.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByAtivoTrue();

    boolean existsByNomeIgnoreCaseAndAtivoTrue(String nome);
}