package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.dao.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    @Query("SELECT g FROM Grupo g WHERE g.activo = true ORDER BY g.nombreGrupo")
    List<Grupo> findAllActive();

    List<Grupo> findByNombreGrupoContainingIgnoreCaseAndActivoTrue(String nombreGrupo);
    
    Optional<Grupo> findByNombreGrupo(String nombreGrupo);
}