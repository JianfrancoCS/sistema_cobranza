package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.dao.entity.Autoridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoridadRepository extends JpaRepository<Autoridad, Long> {

    @Query("SELECT a FROM Autoridad a WHERE a.activo = true ORDER BY a.nombreAutoridad")
    List<Autoridad> findAllActive();

    List<Autoridad> findByNombreAutoridadContainingIgnoreCaseAndActivoTrue(String nombreAutoridad);
}