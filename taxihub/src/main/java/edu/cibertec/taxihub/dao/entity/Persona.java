package edu.cibertec.taxihub.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_personas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Persona extends BaseEntity<String> {

    @Id
    @Column(name = "numero_documento", length = 20, nullable = false)
    private String numeroDocumento;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "ape_paterno", length = 100, nullable = false)
    private String apePaterno;

    @Column(name = "ape_materno", length = 100)
    private String apeMaterno;

    @Column(name = "genero", length = 1, nullable = false)
    private String genero;

    @Column(name = "fecha_nac")
    private LocalDate fechaNac;

    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_documento_id", nullable = false)
    private TipoDocumento tipoDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distrito_id")
    private Distrito distrito;

    @Lob
    @Column(name = "imagen", columnDefinition = "MEDIUMBLOB")
    private byte[] imagen;

    @Column(name = "imagen_tipo", length = 100)
    private String imagenTipo;

    @Transient
    private String imagenBase64;

    @OneToOne(mappedBy = "persona", fetch = FetchType.EAGER)
    private Licencia licencia;

    @Override
    public String getId() {
        return numeroDocumento;
    }

    public String getImagenBase64() {
        if (imagen != null && imagen.length > 0) {
            return java.util.Base64.getEncoder().encodeToString(imagen);
        }
        return imagenBase64;
    }
}