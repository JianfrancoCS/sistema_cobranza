package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Pago;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public interface PagoPorEstadoSpec {

    static Specification<Pago> porEstado(String estado) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(estado)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("estado"), estado.trim());
        };
    }
}