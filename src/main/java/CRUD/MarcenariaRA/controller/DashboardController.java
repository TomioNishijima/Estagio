package CRUD.MarcenariaRA.controller;

import CRUD.MarcenariaRA.model.Material;
import CRUD.MarcenariaRA.model.Dashboard;
import CRUD.MarcenariaRA.repository.DashboardRepository;
import CRUD.MarcenariaRA.repository.MaterialRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardRepository dashboardRepository;
    private final MaterialRepository materialRepository;

    public DashboardController(
            DashboardRepository dashboardRepository,
            MaterialRepository materialRepository) {

        this.dashboardRepository = dashboardRepository;
        this.materialRepository = materialRepository;
    }

    @GetMapping
    public ResponseEntity<?> visualizarDashboard() {

        List<Dashboard> materiais =
                dashboardRepository.findAll();

        return ResponseEntity.ok(materiais);
    }

    @PostMapping("/materiais/{materialId}")
    public ResponseEntity<?> adicionarMaterial(
            @PathVariable Long materialId) {

        if (dashboardRepository.existsByMaterialId(materialId)) {

            return ResponseEntity.badRequest()
                    .body("O material já está na dashboard.");
        }

        Material material = materialRepository
                .findById(materialId)
                .orElse(null);

        if (material == null) {

            return ResponseEntity.badRequest().body("Material não encontrado.");
        }

        Dashboard dashboard = new Dashboard(material);

        dashboardRepository.save(dashboard);

        return ResponseEntity.ok("Material adicionado à dashboard com sucesso."
        );
    }

    @DeleteMapping("/materiais/{materialId}")
    public ResponseEntity<?> removerMaterial(
            @PathVariable Long materialId) {

        if (!dashboardRepository.existsByMaterialId(materialId)) {

            return ResponseEntity.badRequest().body("O material não está na dashboard.");
        }

        dashboardRepository.deleteByMaterialId(materialId);

        return ResponseEntity.ok(
                "Material removido da dashboard com sucesso."
        );
    }
}
