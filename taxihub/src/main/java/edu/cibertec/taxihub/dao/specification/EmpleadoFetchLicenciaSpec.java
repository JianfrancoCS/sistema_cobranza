package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Empleado;
import org.springframework.data.jpa.domain.Specification;

public interface EmpleadoFetchLicenciaSpec {

    static Specification<Empleado> conLicencia() {
        return (root, query, criteriaBuilder) -> {
            if (query.getResultType() != Long.class && !query.isDistinct()) {
                try {
                    root.fetch("licencia", jakarta.persistence.criteria.JoinType.LEFT);
                } catch (Exception e) {
                    root.join("licencia", jakarta.persistence.criteria.JoinType.LEFT);
                }
            }
            return criteriaBuilder.conjunction();
        };
    }
}