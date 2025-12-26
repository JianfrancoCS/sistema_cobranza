package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Empleado;
import org.springframework.data.jpa.domain.Specification;

public interface EmpleadoPorDniSpec {

    static Specification<Empleado> conDni(String dni) {
        return (root, query, criteriaBuilder) -> {
            if (dni == null || dni.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("persona").get("numeroDocumento"), dni.trim());
        };
    }

    static Specification<Empleado> conDniContiene(String dni) {
        return (root, query, criteriaBuilder) -> {
            if (dni == null || dni.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.join("persona").get("numeroDocumento"), "%" + dni.trim() + "%");
        };
    }
}