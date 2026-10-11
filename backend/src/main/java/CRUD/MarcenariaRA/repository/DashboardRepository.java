package CRUD.MarcenariaRA.repository;

import CRUD.MarcenariaRA.model.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {

    boolean existsByMaterialId(Long materialId);

    Optional<Dashboard> findByMaterialId(Long materialId);

    @Transactional
    void deleteByMaterialId(Long materialId);
}