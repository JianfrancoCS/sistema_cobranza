package edu.cibertec.taxihub.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_autos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auto extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "placa", length = 10, nullable = false)
    private String placa;

    @Column(name = "marca", length = 50, nullable = false)
    private String marca;

    @Column(name = "modelo", length = 50, nullable = false)
    private String modelo;

    @Column(name = "serie", length = 50)
    private String serie;

    @Column(name = "color", length = 30, nullable = false)
    private String color;

    @Column(name = "motor", length = 50)
    private String motor;

    @Column(name = "vin", length = 17)
    private String vin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", referencedColumnName = "id")
    private Empleado empleado;

    @Lob
    @Column(name = "imagen", columnDefinition = "MEDIUMBLOB")
    private byte[] imagen;

    @Column(name = "imagen_tipo", length = 100)
    private String imagenTipo;

    @Column(name = "es_propio_empresa")
    private Boolean esPropioEmpresa;

    @Transient
    private String imagenBase64;

    @Override
    public Long getId() {
        return id;
    }

    public String getImagenBase64() {
        if (imagen != null && imagen.length > 0) {
            return java.util.Base64.getEncoder().encodeToString(imagen);
        }
        return imagenBase64;
    }

}
