package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Deuda;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface DeudaPorFechaSpec {

     static Specification<Deuda> porFechaCreacion(LocalDate fecha) {
        return (root, query, criteriaBuilder) -> {
            if (fecha == null) {
                return criteriaBuilder.conjunction();
            }
            
            LocalDateTime inicioDia = fecha.atStartOfDay();
            LocalDateTime finDia = fecha.atTime(LocalTime.MAX);
            
            return criteriaBuilder.between(root.get("fechaCreacion"), inicioDia, finDia);
        };
    }
}