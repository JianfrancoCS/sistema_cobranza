package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Empleado;
import org.springframework.data.jpa.domain.Specification;

public interface EmpleadoPorCargoSpec {

    static Specification<Empleado> conCargoId(Long cargoId) {
        return (root, query, criteriaBuilder) -> {
            if (cargoId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("cargo").get("id"), cargoId);
        };
    }

    static Specification<Empleado> conCargoNombre(String cargoNombre) {
        return (root, query, criteriaBuilder) -> {
            if (cargoNombre == null || cargoNombre.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("cargo").get("nombre")),
                "%" + cargoNombre.trim().toLowerCase() + "%"
            );
        };
    }

    static Specification<Empleado> esConductor() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("cargo").get("nombre")),
                "%conductor%"
            );
    }
}