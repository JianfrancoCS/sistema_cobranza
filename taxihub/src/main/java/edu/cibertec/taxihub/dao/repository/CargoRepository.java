package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.dao.entity.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {

    @Query("SELECT c FROM Cargo c WHERE c.activo = true ORDER BY c.nombre")
    List<Cargo> findAllActive();

    List<Cargo> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}
