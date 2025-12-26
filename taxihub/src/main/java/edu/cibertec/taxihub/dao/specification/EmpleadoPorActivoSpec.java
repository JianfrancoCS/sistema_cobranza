package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Empleado;
import org.springframework.data.jpa.domain.Specification;

public interface EmpleadoPorActivoSpec {

    static Specification<Empleado> conActivo(Boolean activo) {
        return (root, query, criteriaBuilder) -> {
            if (activo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("activo"), activo);
        };
    }

    static Specification<Empleado> activos() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("activo"), true);
    }

    static Specification<Empleado> inactivos() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("activo"), false);
    }
}