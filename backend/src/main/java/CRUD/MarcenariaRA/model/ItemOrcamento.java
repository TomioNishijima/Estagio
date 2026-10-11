package CRUD.MarcenariaRA.model;

import jakarta.persistence.*;

@Entity
@Table(name = "item_orcamento")
public class ItemOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    private Double quantidade;

    private Double valorUnitario;

    private Double subtotal;

    public ItemOrcamento() {
    }

    public ItemOrcamento(
            Orcamento orcamento,
            Material material,
            Double quantidade,
            Double valorUnitario) {

        this.orcamento = orcamento;
        this.material = material;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.subtotal = quantidade * valorUnitario;
    }

    public Long getId() {
        return id;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
