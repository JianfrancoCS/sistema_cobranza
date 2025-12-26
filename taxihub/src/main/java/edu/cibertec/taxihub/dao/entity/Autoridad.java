package edu.cibertec.taxihub.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_autoridades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Autoridad extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_autoridad", length = 50, nullable = false, unique = true)
    private String nombreAutoridad;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @ManyToMany(mappedBy = "autoridades", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();

    @ManyToMany(mappedBy = "autoridades", fetch = FetchType.LAZY)
    private Set<Grupo> grupos = new HashSet<>();

    @Override
    public Long getId() {
        return id;
    }
}