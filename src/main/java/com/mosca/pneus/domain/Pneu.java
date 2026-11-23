package com.mosca.pneus.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(
        name = "pneus",
        uniqueConstraints = @UniqueConstraint(name = "uk_pneu_posicao", columnNames = {"equipamento_id", "posicao"})
)
public class Pneu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Column(nullable = false, length = 80)
    private String marca;

    @Column(nullable = false, length = 80)
    private String modelo;

    @Column(nullable = false, length = 40)
    private String medida;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal sulcoMm;

    @Column(nullable = false)
    private Integer km;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPneu status;

    private Integer posicao;

    private LocalDate dataAquisicao;

    @Column(length = 500)
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id")
    @JsonIgnoreProperties("pneus")
    private Equipamento equipamento;

    public void montar(Equipamento equipamento, Integer posicao) {
        this.equipamento = equipamento;
        this.posicao = posicao;
        this.status = StatusPneu.EM_USO;
        if (!equipamento.getPneus().contains(this)) {
            equipamento.getPneus().add(this);
        }
    }

    public void desmontar() {
        if (this.equipamento != null) {
            this.equipamento.getPneus().remove(this);
        }
        this.equipamento = null;
        this.posicao = null;
        if (this.status != StatusPneu.DESCARTADO) {
            this.status = StatusPneu.ESTOQUE;
        }
    }
}
