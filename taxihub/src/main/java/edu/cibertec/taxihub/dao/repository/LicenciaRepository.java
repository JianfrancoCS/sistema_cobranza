package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.dao.entity.Licencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenciaRepository extends JpaRepository<Licencia, String> {

    Optional<Licencia> findByPersonaNumeroDocumento(String personaNumeroDocumento);
}