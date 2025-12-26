package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Empleado;
import org.springframework.data.jpa.domain.Specification;

public interface EmpleadoPorNombreSpec {

    static Specification<Empleado> conNombreContiene(String nombre) {
        return (root, query, criteriaBuilder) -> {
            if (nombre == null || nombre.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String searchTerm = "%" + nombre.trim().toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("persona").get("nombre")), searchTerm),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("persona").get("apePaterno")), searchTerm),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("persona").get("apeMaterno")), searchTerm)
            );
        };
    }

    static Specification<Empleado> conNombreExacto(String nombre) {
        return (root, query, criteriaBuilder) -> {
            if (nombre == null || nombre.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                criteriaBuilder.lower(root.get("persona").get("nombre")),
                nombre.trim().toLowerCase()
            );
        };
    }
}