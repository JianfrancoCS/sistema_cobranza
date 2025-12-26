package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Auto;
import org.springframework.data.jpa.domain.Specification;

public interface AutoPorPlacaSpecification {

     static Specification<Auto> porPlaca(String placa) {
        return (root, query, criteriaBuilder) -> {
            if (placa == null || placa.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.upper(root.get("placa")), 
                "%" + placa.trim().toUpperCase() + "%"
            );
        };
    }
}