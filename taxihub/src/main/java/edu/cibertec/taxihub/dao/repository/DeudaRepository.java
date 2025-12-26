package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.dao.entity.Deuda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeudaRepository extends JpaRepository<Deuda, Long>, JpaSpecificationExecutor<Deuda> {

    List<Deuda> findByEmpleadoPersonaNumeroDocumentoAndActivoTrueOrderByFechaCreacionDesc(String numeroDocumento);

    @Query("SELECT d FROM Deuda d WHERE d.empleado.persona.numeroDocumento = :empleadoId AND d.activo = true AND d.montoPagado < d.montoDeuda ORDER BY d.fechaCreacion DESC")
    List<Deuda> findDeudasPendientesByEmpleadoId(@Param("empleadoId") String empleadoId);

    @Query("SELECT d FROM Deuda d WHERE d.empleado.persona.numeroDocumento = :empleadoId AND d.activo = true AND d.montoPagado >= d.montoDeuda ORDER BY d.fechaCreacion DESC")
    List<Deuda> findDeudasSaldadasByEmpleadoId(@Param("empleadoId") String empleadoId);

    @Query("SELECT COALESCE(SUM(d.montoDeuda - d.montoPagado), 0) FROM Deuda d WHERE d.empleado.persona.numeroDocumento = :empleadoId AND d.activo = true AND d.montoPagado < d.montoDeuda")
    BigDecimal getTotalDeudaPendienteByEmpleadoPersonaNumeroDocumento(@Param("empleadoId") String empleadoId);

    @Query("SELECT d FROM Deuda d WHERE d.activo = true AND DATE(d.fechaCreacion) = :fecha")
    List<Deuda> findByFechaCreacion(@Param("fecha") LocalDate fecha);

    @Query("SELECT d FROM Deuda d WHERE d.activo = true AND d.montoPagado < d.montoDeuda")
    Page<Deuda> findAllDeudasPendientes(Pageable pageable);

    boolean existsByEmpleadoPersonaNumeroDocumentoAndActivoTrueAndFechaCreacionBetween(
        String numeroDocumento,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin
    );

    Optional<Deuda> findByEmpleadoPersonaNumeroDocumentoAndActivoTrueAndFechaCreacionBetween(
        String numeroDocumento, 
        LocalDateTime fechaInicio, 
        LocalDateTime fechaFin
    );

    @Query("SELECT COALESCE(SUM(d.montoDeuda - d.montoPagado), 0) FROM Deuda d WHERE d.activo = true AND d.montoPagado < d.montoDeuda")
    Optional<BigDecimal> sumSaldoPendienteByActivoTrue();

    @Query("SELECT COUNT(d) FROM Deuda d WHERE d.activo = true AND (d.montoDeuda - d.montoPagado) > :saldo")
    Long countByActivoTrueAndSaldoPendienteGreaterThan(@Param("saldo") BigDecimal saldo);
}