package edu.cibertec.taxihub.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_grupos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grupo extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_grupo", length = 50, nullable = false, unique = true)
    private String nombreGrupo;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @ManyToMany(mappedBy = "grupos", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "tbl_grupo_autoridades",
        joinColumns = @JoinColumn(name = "grupo_id"),
        inverseJoinColumns = @JoinColumn(name = "autoridad_id")
    )
    private Set<Autoridad> autoridades = new HashSet<>();

    @Override
    public Long getId() {
        return id;
    }
}