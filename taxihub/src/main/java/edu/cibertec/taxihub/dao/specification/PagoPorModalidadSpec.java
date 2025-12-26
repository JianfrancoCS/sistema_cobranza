package edu.cibertec.taxihub.dao.specification;

import edu.cibertec.taxihub.constantes.ModalidadPagoEnum;
import edu.cibertec.taxihub.dao.entity.Pago;
import org.springframework.data.jpa.domain.Specification;

public interface PagoPorModalidadSpec {

    static Specification<Pago> porModalidad(ModalidadPagoEnum modalidad) {
        return (root, query, criteriaBuilder) -> {
            if (modalidad == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("modalidadPago"), modalidad);
        };
    }

    static Specification<Pago> porModalidadString(String modalidad) {
        return (root, query, criteriaBuilder) -> {
            if (modalidad == null || modalidad.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("modalidadPago"), modalidad.trim());
        };
    }

    static Specification<Pago> billetera() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("modalidadPago"), ModalidadPagoEnum.BILLETERA_VIRTUAL);
    }

    static Specification<Pago> efectivo() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("modalidadPago"), ModalidadPagoEnum.EFECTIVO);
    }
}