package CRUD.MarcenariaRA.repository;

import CRUD.MarcenariaRA.model.DashboardMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DashboardMaterialRepository
        extends JpaRepository<DashboardMaterial, Long> {

    List<DashboardMaterial> findAll();

    void deleteByMaterialId(Long materialId);
}
