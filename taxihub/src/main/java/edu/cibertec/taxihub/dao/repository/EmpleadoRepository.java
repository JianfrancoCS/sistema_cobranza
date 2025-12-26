package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.dao.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long>, JpaSpecificationExecutor<Empleado> {

    @Query("SELECT e FROM Empleado e WHERE e.persona.numeroDocumento = :numeroDocumento")
    Optional<Empleado> findByPersonaNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);

    @Query("SELECT e FROM Empleado e WHERE e.persona.numeroDocumento = :numeroDocumento AND e.activo = true")
    Optional<Empleado> findByPersonaNumeroDocumentoAndActivoTrue(@Param("numeroDocumento") String numeroDocumento);

    @Query("SELECT e FROM Empleado e WHERE e.cargo.nombre LIKE '%CONDUCTOR%' " +
           "AND e.activo = true " +
           "AND NOT EXISTS (SELECT a FROM Auto a WHERE a.empleado = e AND a.activo = true)")
    List<Empleado> findConductoresDisponibles();

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Empleado e " +
           "WHERE e.persona.numeroDocumento = ?1 AND e.cargo.nombre LIKE '%CONDUCTOR%' AND e.activo = true")
    boolean esConductor(String numeroDocumento);

    Long countByActivoTrue();

    @Query("SELECT COUNT(DISTINCT e) FROM Empleado e JOIN Auto a ON e = a.empleado " +
           "WHERE e.activo = true AND a.activo = true AND a.esPropioEmpresa = true")
    Long countEmpleadosActivosConAutoEmpresa();

    @Query("SELECT COUNT(DISTINCT e) FROM Empleado e JOIN Auto a ON e = a.empleado " +
           "WHERE e.activo = true AND a.activo = true AND a.esPropioEmpresa = false")
    Long countEmpleadosActivosConAutoPropio();
}
