package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.constantes.EstadoPagoEnum;
import edu.cibertec.taxihub.constantes.ModalidadPagoEnum;
import edu.cibertec.taxihub.dao.entity.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface IPagoUseCase {
    
    Page<Pago> listarPagos(String numeroDocumento, LocalDate fecha, String modalidad, String estado, Boolean activo, Pageable pageable);
    
    Optional<Pago> buscarPagoPorId(Long id);
    
    Optional<Pago> crearPago(Long deudaId, ModalidadPagoEnum modalidad, BigDecimal monto, String observacion, byte[] imagen, String imagenTipo);
    
    Pago cambiarEstadoPago(Long pagoId, EstadoPagoEnum nuevoEstado, String observacion);
    
    long contarPagosPendientesRevision();
    

    Optional<Pago> crearPago(Long deudaId, BigDecimal monto, String descripcion);
    
    Optional<Pago> crearPagoFisico(Long deudaId, BigDecimal monto, String descripcion);
    
    void eliminarPago(Long id);
    
    boolean verificarPropietarioPago(Long pagoId, String numeroDocumento);
}