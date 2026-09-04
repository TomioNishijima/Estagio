package CRUD.MarcenariaRA.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

    @Entity
public class Material {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "O nome é obrigatório")
        private String nome;

        @Min(value = 0, message = "A quantidade mínima é 0")
        private Integer quantidade=0;

        @Positive(message = "O valor deve ser positivo")
        private Double valor;

        private Boolean ativo = true;

        public Boolean getAtivo() { return ativo; }

        public void setAtivo(Boolean ativo) { this.ativo = ativo; }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Integer quantidade) {
            this.quantidade = quantidade;
        }

        public Double getValor() {
            return valor;
        }

        public void setValor(Double valor) {
            this.valor = valor;
        }


    }