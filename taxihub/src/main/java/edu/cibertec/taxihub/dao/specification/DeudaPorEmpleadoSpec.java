package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Deuda;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public interface DeudaPorEmpleadoSpec {

    static Specification<Deuda> porNumeroDocumento(String numeroDocumento) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(numeroDocumento)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("empleado").join("persona").get("numeroDocumento"), numeroDocumento.trim());
        };
    }
}