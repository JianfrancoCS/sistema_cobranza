package edu.cibertec.taxihub.dao.entity;

import edu.cibertec.taxihub.constantes.EstadoPagoEnum;
import edu.cibertec.taxihub.constantes.ModalidadPagoEnum;
import edu.cibertec.taxihub.dao.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tbl_pagos")
@Getter
@Setter
public class Pago extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deuda_id", nullable = false)
    private Long deudaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deuda_id", insertable = false, updatable = false)
    private Deuda deuda;

    @Column(name = "modalidad_pago", nullable = false, length = 50)
    private String modalidadPago;

    @Column(name = "monto_pago", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoPago;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "imagen", columnDefinition = "MEDIUMBLOB")
    private byte[] imagen;

    @Column(name = "imagen_tipo", length = 100)
    private String imagenTipo;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @Override
    public Long getId() {
        return id;
    }

    public boolean requiereImagen() {
        return ModalidadPagoEnum.BILLETERA_VIRTUAL.getDescripcion().equals(modalidadPago);
    }

    public boolean estaAprobado() {
        return EstadoPagoEnum.APROBADO.getDescripcion().equals(estado);
    }

    public boolean estaRechazado() {
        return EstadoPagoEnum.RECHAZADO.getDescripcion().equals(estado);
    }

    public boolean estaPendiente() {
        return EstadoPagoEnum.POR_REVISAR.getDescripcion().equals(estado) || 
               EstadoPagoEnum.EN_REVISION.getDescripcion().equals(estado);
    }

    public String getImagenBase64() {
        if (imagen != null && imagen.length > 0) {
            return java.util.Base64.getEncoder().encodeToString(imagen);
        }
        return null;
    }
}