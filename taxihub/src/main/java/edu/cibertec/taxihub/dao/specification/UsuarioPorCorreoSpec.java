package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Usuario;
import org.springframework.data.jpa.domain.Specification;

public interface UsuarioPorCorreoSpec {

    static Specification<Usuario> conCorreo(String correo) {
        return (root, query, criteriaBuilder) -> {
            if (correo == null || correo.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("correo")), 
                "%" + correo.toLowerCase() + "%"
            );
        };
    }
}