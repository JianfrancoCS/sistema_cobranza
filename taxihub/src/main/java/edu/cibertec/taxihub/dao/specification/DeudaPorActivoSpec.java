package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Deuda;
import org.springframework.data.jpa.domain.Specification;

public interface DeudaPorActivoSpec {

    static Specification<Deuda> conActivo(Boolean activo) {
        return (root, query, criteriaBuilder) -> {
            if (activo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("activo"), activo);
        };
    }

    static Specification<Deuda> activas() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("activo"), true);
    }

    static Specification<Deuda> inactivas() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("activo"), false);
    }
}