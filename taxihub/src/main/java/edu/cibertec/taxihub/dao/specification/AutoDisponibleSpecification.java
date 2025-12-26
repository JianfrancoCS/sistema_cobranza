package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Auto;
import org.springframework.data.jpa.domain.Specification;

public interface AutoDisponibleSpecification {
    
    static Specification<Auto> disponibles(Boolean disponibles) {
        return (root, query, criteriaBuilder) -> {
            if (disponibles == null || !disponibles) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get("activo"), true),
                criteriaBuilder.isNull(root.get("empleado"))
            );
        };
    }
}