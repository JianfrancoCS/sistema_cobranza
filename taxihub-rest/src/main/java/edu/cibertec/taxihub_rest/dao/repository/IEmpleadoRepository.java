package edu.cibertec.taxihub_rest.dao.repository;

import edu.cibertec.taxihub_rest.dao.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEmpleadoRepository extends JpaRepository<Empleado, Long> {
    
    @Query("""
        SELECT e FROM Empleado e 
        WHERE e.persona.numeroDocumento = :numeroDocumento 
        AND e.cargo.nombre = :cargoNombre
        AND e.fechaEliminacion IS NULL
    """)
    Optional<Empleado> findByNumeroDocumentoAndCargo(@Param("numeroDocumento") String numeroDocumento, 
                                                     @Param("cargoNombre") String cargoNombre);
}