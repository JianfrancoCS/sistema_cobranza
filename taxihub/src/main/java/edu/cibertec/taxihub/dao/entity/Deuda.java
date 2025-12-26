package edu.cibertec.taxihub.dao.entity;

import edu.cibertec.taxihub.dao.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "tbl_deudas")
@Getter
@Setter
public class Deuda extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(name = "monto_deuda", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoDeuda;

    @Column(name = "monto_pagado", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoPagado = BigDecimal.ZERO;

    @OneToMany(mappedBy = "deuda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos;

    @Override
    public Long getId() {
        return id;
    }

    public BigDecimal getSaldoPendiente() {
        return montoDeuda.subtract(montoPagado);
    }

    public boolean estaSaldada() {
        return montoPagado.compareTo(montoDeuda) == 0;
    }

    public boolean estaPendiente() {
        return montoPagado.compareTo(montoDeuda) < 0;
    }

    public boolean estaSobrepagada() {
        return montoPagado.compareTo(montoDeuda) > 0;
    }

    public String getEstadoPago() {
        if (estaPendiente()) return "Pendiente";
        if (estaSaldada()) return "Saldada";
        return "Sobrepagada";
    }
}