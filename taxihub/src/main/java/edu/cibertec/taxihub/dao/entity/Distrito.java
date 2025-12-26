package edu.cibertec.taxihub.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_distritos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Distrito extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provincia_id", nullable = false)
    private Provincia provincia;

    @Column(name = "ubigeo_inei", length = 6, nullable = false)
    private String ubigeoInei;

    @Column(name = "ubigeo_reniec", length = 6, nullable = false)
    private String ubigeoReniec;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Override
    public Long getId() {
        return id;
    }
}