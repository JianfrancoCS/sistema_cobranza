package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Auto;
import org.springframework.data.jpa.domain.Specification;

public interface AutoPorEstadoSpecification {
    
    static Specification<Auto> porEstadoActivo(Boolean activo) {
        return (root, query, criteriaBuilder) -> {
            if (activo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("activo"), activo);
        };
    }

    static Specification<Auto> soloActivos() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("activo"), true);
    }
}