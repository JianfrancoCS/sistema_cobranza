package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Usuario;
import org.springframework.data.jpa.domain.Specification;

public interface UsuarioPorActivoSpec {

    static Specification<Usuario> conActivo(Boolean activo) {
        return (root, query, criteriaBuilder) -> {
            if (activo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("activo"), activo);
        };
    }
}