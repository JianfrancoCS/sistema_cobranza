package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.ComisionPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface IComisionPagoUseCase {
    
    Page<ComisionPago> listarComisionesActivas(Pageable pageable);
    
    Optional<ComisionPago> buscarComisionPorCodigo(String codigo);
    
    BigDecimal obtenerMontoComisionPorCodigo(String codigo);
    
    ComisionPago crearComision(ComisionPago comision);
    
    ComisionPago actualizarComision(Long id, ComisionPago comision);
    
    void eliminarComision(Long id);
}