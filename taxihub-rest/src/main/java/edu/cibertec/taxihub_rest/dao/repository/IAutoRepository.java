package edu.cibertec.taxihub_rest.dao.repository;

import edu.cibertec.taxihub_rest.dao.entity.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IAutoRepository extends JpaRepository<Auto, Long> {
    
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Auto a 
        WHERE a.empleado.id = :empleadoId
    """)
    boolean existsByEmpleadoId(@Param("empleadoId") Long empleadoId);
}