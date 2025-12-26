package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.dao.entity.Deuda;
import org.springframework.data.jpa.domain.Specification;

public interface DeudaPorEstadoSpec {

    static Specification<Deuda> pendientes() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.lessThan(root.get("montoPagado"), root.get("montoDeuda"));
    }

    static Specification<Deuda> saldadas() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("montoPagado"), root.get("montoDeuda"));
    }

    static Specification<Deuda> sobrepagadas() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.greaterThan(root.get("montoPagado"), root.get("montoDeuda"));
    }
}