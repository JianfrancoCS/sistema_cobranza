package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.constantes.EstadoPagoEnum;
import edu.cibertec.taxihub.constantes.ModalidadPagoEnum;
import edu.cibertec.taxihub.dao.entity.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> , JpaSpecificationExecutor<Pago> {
    @Query("SELECT COUNT(p) FROM Pago p WHERE p.estado = 'POR_REVISAR' AND p.activo = true")
    long countPagosPendientesRevision();

    @Query("SELECT COALESCE(SUM(p.montoPago), 0) FROM Pago p WHERE p.estado = :estado AND p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin AND p.activo = true")
    Optional<BigDecimal> sumMontoByEstadoAndFechaCreacionBetween(
        @Param("estado") String estado,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
}