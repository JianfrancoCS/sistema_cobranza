package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Pago;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public interface PagoPorEmpleadoSpec {

    static Specification<Pago> porNumeroDocumento(String numeroDocumento) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(numeroDocumento)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("deuda").join("empleado").join("persona").get("numeroDocumento"), numeroDocumento.trim());
        };
    }
}