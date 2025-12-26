package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Deuda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface IDeudaUseCase {
    
    Page<Deuda> listarDeudasConFiltros(String numeroDocumento, Boolean pendiente, Boolean activo, LocalDate fecha, Pageable pageable);
    
    Page<Deuda> listarDeudasPendientesPorEmpleado(String numeroDocumento, Pageable pageable);
    
    Page<Deuda> listarDeudasSaldadasPorEmpleado(String numeroDocumento, Pageable pageable);
    
    BigDecimal obtenerTotalDeudaPendientePorEmpleado(String numeroDocumento);
    
    Optional<Deuda> buscarDeudaPorId(Long id);
    
    Deuda crearDeuda(String empleadoId, BigDecimal montoDeuda);
    
    Deuda actualizarMontoPagado(Long deudaId, BigDecimal montoAdicional);
    
    void procesarDeudasDiarias();
    
    void generarDeudasManualmente();
}