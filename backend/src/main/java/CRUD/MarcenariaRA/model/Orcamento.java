package CRUD.MarcenariaRA.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orcamento")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private LocalDateTime data;

    private Double custoFinal;

    private boolean excluido = false;

    public Orcamento() {
    }

    public Orcamento(String nome) {
        this.nome = nome;
        this.data = LocalDateTime.now();
        this.custoFinal = 0.0;
        this.excluido = false;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Double getCustoFinal() {
        return custoFinal;
    }

    public void setCustoFinal(Double custoFinal) {
        this.custoFinal = custoFinal;
    }

    public boolean isExcluido() {
        return excluido;
    }

    public void setExcluido(boolean excluido) {
        this.excluido = excluido;
    }
}