package edu.cibertec.taxihub.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_provincias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Provincia extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Override
    public Long getId() {
        return id;
    }
}