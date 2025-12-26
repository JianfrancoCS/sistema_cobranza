package edu.cibertec.taxihub.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_departamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Departamento extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Override
    public Long getId() {
        return id;
    }
}