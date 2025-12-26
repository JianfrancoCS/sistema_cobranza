package edu.cibertec.taxihub.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_licencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Licencia extends BaseEntity<String> {

    @Id
    @Column(name = "numero_licencia", length = 20, nullable = false)
    private String numeroLicencia;

    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_documento", referencedColumnName = "numero_documento", insertable = false, updatable = false)
    private Persona persona;
    @Column(name = "categoria_licencia", length = 10, nullable = false)
    private String categoriaLicencia;

    @Column(name = "fecha_vencimiento_licencia", nullable = false)
    private LocalDate fechaVencimientoLicencia;


    @Override
    public String getId() {
        return numeroLicencia;
    }

    public boolean isLicenciaVigente() {
        return fechaVencimientoLicencia != null &&
               fechaVencimientoLicencia.isAfter(java.time.LocalDate.now());
    }

}
