package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.dao.entity.Marcacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MarcacionRepository extends JpaRepository<Marcacion, Long> {

    @Query("SELECT COUNT(m) FROM Marcacion m WHERE m.empleado.persona.numeroDocumento = :numeroDocumento AND m.tipoMarcacion = true AND m.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    long countSalidasByEmpleadoIdAndFechaBetween(
        @Param("numeroDocumento") String numeroDocumento,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("SELECT m FROM Marcacion m WHERE m.empleado.persona.numeroDocumento = :numeroDocumento AND m.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fechaCreacion DESC")
    List<Marcacion> findByEmpleadoIdAndFechaBetween(
        @Param("numeroDocumento") String numeroDocumento,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("SELECT m FROM Marcacion m WHERE m.empleado.persona.numeroDocumento = :numeroDocumento AND m.tipoMarcacion = :tipoMarcacion AND m.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fechaCreacion DESC")
    List<Marcacion> findByEmpleadoIdAndTipoAndFechaBetween(
        @Param("numeroDocumento") String numeroDocumento,
        @Param("tipoMarcacion") boolean tipoMarcacion,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
}