package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Auto;
import org.springframework.data.jpa.domain.Specification;

public interface AutoPorMarcaSpecification {

    public static Specification<Auto> porMarca(String marca) {
        return (root, query, criteriaBuilder) -> {
            if (marca == null || marca.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.upper(root.get("marca")), 
                "%" + marca.trim().toUpperCase() + "%"
            );
        };
    }
}