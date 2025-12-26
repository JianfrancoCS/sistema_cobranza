package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.dao.entity.Auto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoRepository extends JpaRepository<Auto, Long>, JpaSpecificationExecutor<Auto> {
    Optional<Auto> findByPlacaAndActivoTrue(String placa);
    @Query("SELECT a FROM Auto a WHERE a.empleado.persona.numeroDocumento = :numeroDocumento AND a.activo = true")
    List<Auto> findByEmpleadoPersonaNumeroDocumentoAndActivoTrue(@Param("numeroDocumento") String numeroDocumento);
    

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Auto a WHERE a.empleado.persona.numeroDocumento = :numeroDocumento AND a.activo = true")
    boolean existsByEmpleadoPersonaNumeroDocumentoAndActivoTrue(@Param("numeroDocumento") String numeroDocumento);

    Long countByEsPropioEmpresaTrue();
    Long countByEsPropioEmpresaFalse();
}