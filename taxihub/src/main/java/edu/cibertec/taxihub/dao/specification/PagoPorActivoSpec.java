package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Pago;
import org.springframework.data.jpa.domain.Specification;

public interface PagoPorActivoSpec {

    static Specification<Pago> conActivo(Boolean activo) {
        return (root, query, criteriaBuilder) -> {
            if (activo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("activo"), activo);
        };
    }

    static Specification<Pago> activos() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("activo"), true);
    }

    static Specification<Pago> inactivos() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("activo"), false);
    }
}