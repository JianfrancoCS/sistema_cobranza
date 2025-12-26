package edu.cibertec.taxihub_rest.dao.repository;

import edu.cibertec.taxihub_rest.dao.entity.Marcacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IMarcacionRepository extends JpaRepository<Marcacion, Long> {
    
    @Query("""
        SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END
        FROM Marcacion m
        WHERE m.empleado.persona.numeroDocumento = :numeroDocumento
          AND m.tipoMarcacion = true
          AND NOT EXISTS (
            SELECT 1 FROM Marcacion m2
            WHERE m2.empleado.persona.numeroDocumento = m.empleado.persona.numeroDocumento
              AND m2.tipoMarcacion = false
              AND m2.fechaCreacion > m.fechaCreacion
          )
    """)
    boolean tieneIngresoSinSalida(@Param("numeroDocumento") String numeroDocumento);

    @Query("""
        SELECT m FROM Marcacion m
        WHERE m.empleado.persona.numeroDocumento = :numeroDocumento
          AND m.tipoMarcacion = true
          AND NOT EXISTS (
            SELECT 1 FROM Marcacion m2
            WHERE m2.empleado.persona.numeroDocumento = m.empleado.persona.numeroDocumento
              AND m2.tipoMarcacion = false
              AND m2.fechaCreacion > m.fechaCreacion
          )
        ORDER BY m.fechaCreacion DESC
    """)
    Optional<Marcacion> buscarUltimoIngresoPendiente(@Param("numeroDocumento") String numeroDocumento);

    @Query("""
        SELECT COUNT(m) FROM Marcacion m
        WHERE m.empleado.persona.numeroDocumento = :numeroDocumento
          AND m.tipoMarcacion = false
    """)
    Long contarSalidas(@Param("numeroDocumento") String numeroDocumento);
}
