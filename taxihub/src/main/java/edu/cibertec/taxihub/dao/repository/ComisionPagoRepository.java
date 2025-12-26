package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.dao.entity.ComisionPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComisionPagoRepository extends JpaRepository<ComisionPago, Long> {

    Optional<ComisionPago> findByCodigoAndActivoTrue(String codigo);

    @Query("SELECT c FROM ComisionPago c WHERE c.activo = true ORDER BY c.nombre")
    List<ComisionPago> findAllActive();

    @Query("SELECT c FROM ComisionPago c WHERE c.codigo IN :codigos AND c.activo = true")
    List<ComisionPago> findByCodigosAndActivoTrue(@Param("codigos") List<String> codigos);

    boolean existsByCodigoAndActivoTrue(String codigo);
}