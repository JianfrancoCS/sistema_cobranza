package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.dao.entity.Persona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {

    @Query("SELECT p FROM Persona p LEFT JOIN FETCH p.tipoDocumento LEFT JOIN FETCH p.distrito WHERE p.numeroDocumento = :numeroDocumento AND p.activo = true")
    Optional<Persona> findByNumeroDocumentoAndActivoTrueWithRelations(@Param("numeroDocumento") String numeroDocumento);


    Optional<Persona> findByNumeroDocumento(String numeroDocumento);
}