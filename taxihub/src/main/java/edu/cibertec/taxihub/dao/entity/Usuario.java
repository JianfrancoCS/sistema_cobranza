package edu.cibertec.taxihub.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correo", length = 100, nullable = false)
    private String correo;

    @Column(name = "contrasena", length = 255, nullable = false)
    private String contrasena;

    @Column(name = "contrasena_temporal", nullable = false)
    private Boolean contrasenaTemporal = false;

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "empleado_id", referencedColumnName = "id")
    private Empleado empleado;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "tbl_usuario_autoridades",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "autoridad_id")
    )
    private Set<Autoridad> autoridades = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "tbl_usuario_grupos",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "grupo_id")
    )
    private Set<Grupo> grupos = new HashSet<>();

    @Override
    public Long getId() {
        return id;
    }


    public boolean tieneContrasenaTemporalActiva() {
        return contrasenaTemporal != null && contrasenaTemporal;
    }

    @Transient
    public byte[] getImagen() {
        return empleado != null && empleado.getPersona() != null ? 
               empleado.getPersona().getImagen() : null;
    }

    @Transient 
    public String getImagenTipo() {
        return empleado != null && empleado.getPersona() != null ? 
               empleado.getPersona().getImagenTipo() : null;
    }

    @Transient
    public String getImagenBase64() {
        if (empleado != null && empleado.getPersona() != null && empleado.getPersona().getImagen() != null) {
            return java.util.Base64.getEncoder().encodeToString(empleado.getPersona().getImagen());
        }
        return null;
    }

    @Transient
    public void setImagenBase64(String imagenBase64) {
        if (empleado != null && empleado.getPersona() != null) {
            empleado.getPersona().setImagenBase64(imagenBase64);
        }
    }
}
