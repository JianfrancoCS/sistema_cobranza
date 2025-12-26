package edu.cibertec.taxihub_rest.dao.entity;

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
public class Persona {

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
}