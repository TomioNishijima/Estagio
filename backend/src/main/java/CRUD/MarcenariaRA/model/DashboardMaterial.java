package CRUD.MarcenariaRA.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dashboard_material")
public class DashboardMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    public DashboardMaterial() {
    }

    public DashboardMaterial(Material material) {
        this.material = material;
    }

    public Long getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
