package edu.cibertec.taxihub.dao.repository;

import edu.cibertec.taxihub.dao.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {


    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.autoridades LEFT JOIN FETCH u.grupos WHERE u.id = :id AND u.activo = true")
    Optional<Usuario> findByIdWithRelations(@Param("id") Long id);

    Optional<Usuario> findByCorreoAndActivoTrue(String correo);

    boolean existsByCorreoAndActivoTrue(String correo);
    
    @Query("SELECT u FROM Usuario u WHERE u.empleado.persona.numeroDocumento = :numeroDocumento AND u.activo = true")
    Optional<Usuario> findByEmpleadoPersonaNumeroDocumentoAndActivoTrue(@Param("numeroDocumento") String numeroDocumento);
}