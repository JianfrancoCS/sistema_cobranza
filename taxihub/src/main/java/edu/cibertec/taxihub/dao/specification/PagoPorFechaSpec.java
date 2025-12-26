package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Pago;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface PagoPorFechaSpec {

    static Specification<Pago> porFecha(LocalDate fecha) {
        return (root, query, criteriaBuilder) -> {
            if (fecha == null) {
                return criteriaBuilder.conjunction();
            }
            
            LocalDateTime inicioDia = fecha.atStartOfDay();
            LocalDateTime finDia = fecha.atTime(23, 59, 59);
            
            return criteriaBuilder.between(
                root.get("fechaCreacion"), 
                inicioDia, 
                finDia
            );
        };
    }
}