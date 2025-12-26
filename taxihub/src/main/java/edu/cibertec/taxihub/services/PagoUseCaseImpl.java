package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.constantes.EstadoPagoEnum;
import edu.cibertec.taxihub.constantes.ModalidadPagoEnum;
import edu.cibertec.taxihub.dao.entity.Deuda;
import edu.cibertec.taxihub.dao.entity.Pago;
import edu.cibertec.taxihub.dao.repository.DeudaRepository;
import edu.cibertec.taxihub.dao.repository.PagoRepository;
import edu.cibertec.taxihub.dao.specification.PagoPorActivoSpec;
import edu.cibertec.taxihub.dao.specification.PagoPorEmpleadoSpec;
import edu.cibertec.taxihub.dao.specification.PagoPorEstadoSpec;
import edu.cibertec.taxihub.dao.specification.PagoPorFechaSpec;
import edu.cibertec.taxihub.dao.specification.PagoPorModalidadSpec;
import edu.cibertec.taxihub.usecase.IPagoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagoUseCaseImpl implements IPagoUseCase {

    private final PagoRepository pagoRepository;
    private final DeudaRepository deudaRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Pago> buscarPagoPorId(Long id) {
        return pagoRepository.findById(id).filter(Pago::isActivo);
    }

    @Override
    @Transactional
    public Optional<Pago> crearPago(Long deudaId, ModalidadPagoEnum modalidad, BigDecimal monto, String observacion, byte[] imagen, String imagenTipo) {
        Optional<Deuda> deudaOpt = deudaRepository.findById(deudaId).filter(Deuda::isActivo);
        if (deudaOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Deuda deuda = deudaOpt.get();
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }
        BigDecimal saldoPendiente = deuda.getSaldoPendiente();
        if (monto.compareTo(saldoPendiente) > 0) {
            return Optional.empty();
        }

        if (modalidad == ModalidadPagoEnum.BILLETERA_VIRTUAL && (imagen == null || imagen.length == 0)) {
            return Optional.empty();
        }

        Pago pago = new Pago();
        pago.setDeudaId(deudaId);
        pago.setModalidadPago(modalidad.getDescripcion());
        pago.setMontoPago(monto);
        pago.setObservacion(observacion);
        pago.setImagen(imagen);
        pago.setImagenTipo(imagenTipo);
        pago.setEstado(EstadoPagoEnum.POR_REVISAR.getDescripcion());

        return Optional.of(pagoRepository.save(pago));
    }

    @Override
    @Transactional
    public Pago cambiarEstadoPago(Long pagoId, EstadoPagoEnum nuevoEstado, String observacion) {
        return pagoRepository.findById(pagoId)
                .filter(Pago::isActivo)
                .map(pago -> {
                    String estadoAnterior = pago.getEstado();
                    pago.setEstado(nuevoEstado.getDescripcion());
                    
                    if (observacion != null && !observacion.trim().isEmpty()) {
                        String observacionActual = pago.getObservacion() != null ? pago.getObservacion() : "";
                        String nuevaObservacion = observacionActual.isEmpty() ? 
                            observacion : observacionActual + "\n" + observacion;
                        pago.setObservacion(nuevaObservacion);
                    }

                    Pago pagoActualizado = pagoRepository.save(pago);
                    if (nuevoEstado == EstadoPagoEnum.APROBADO && !EstadoPagoEnum.APROBADO.getDescripcion().equals(estadoAnterior)) {
                        actualizarDeudaConPagoAprobado(pago);
                    }
                    else if (EstadoPagoEnum.APROBADO.getDescripcion().equals(estadoAnterior) && nuevoEstado == EstadoPagoEnum.RECHAZADO) {
                        revertirDeudaConPagoRechazado(pago);
                    }

                    return pagoActualizado;
                })
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + pagoId));
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPagosPendientesRevision() {
        return pagoRepository.countPagosPendientesRevision();
    }

    private void actualizarDeudaConPagoAprobado(Pago pago) {
        deudaRepository.findById(pago.getDeudaId())
                .filter(Deuda::isActivo)
                .ifPresent(deuda -> {
                    BigDecimal nuevoMontoPagado = deuda.getMontoPagado().add(pago.getMontoPago());
                    deuda.setMontoPagado(nuevoMontoPagado);
                    deudaRepository.save(deuda);
                });
    }

    private void revertirDeudaConPagoRechazado(Pago pago) {
        deudaRepository.findById(pago.getDeudaId())
                .filter(Deuda::isActivo)
                .ifPresent(deuda -> {
                    BigDecimal nuevoMontoPagado = deuda.getMontoPagado().subtract(pago.getMontoPago());
                    if (nuevoMontoPagado.compareTo(BigDecimal.ZERO) < 0) {
                        nuevoMontoPagado = BigDecimal.ZERO;
                    }
                    deuda.setMontoPagado(nuevoMontoPagado);
                    deudaRepository.save(deuda);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Pago> listarPagos(String numeroDocumento, LocalDate fecha, String modalidad, String estado, Boolean activo, Pageable pageable) {
        Specification<Pago> spec = Specification
                .where(PagoPorEmpleadoSpec.porNumeroDocumento(numeroDocumento))
                .and(PagoPorFechaSpec.porFecha(fecha))
                .and(PagoPorModalidadSpec.porModalidadString(modalidad))
                .and(PagoPorEstadoSpec.porEstado(estado))
                .and(activo != null ? PagoPorActivoSpec.conActivo(activo) : PagoPorActivoSpec.activos());
        
        return pagoRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarPropietarioPago(Long pagoId, String numeroDocumento) {
        return pagoRepository.findById(pagoId)
                .filter(Pago::isActivo)
                .map(pago -> {
                    return deudaRepository.findById(pago.getDeudaId())
                            .map(deuda -> deuda.getEmpleado().getPersona().getNumeroDocumento().equals(numeroDocumento))
                            .orElse(false);
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public Optional<Pago> crearPago(Long deudaId, BigDecimal monto, String descripcion) {
        return crearPago(deudaId, ModalidadPagoEnum.BILLETERA_VIRTUAL, monto, descripcion, null, null);
    }

    @Override
    @Transactional
    public Optional<Pago> crearPagoFisico(Long deudaId, BigDecimal monto, String descripcion) {
        return crearPago(deudaId, ModalidadPagoEnum.EFECTIVO, monto, descripcion, null, null);
    }

    @Override
    @Transactional
    public void eliminarPago(Long id) {
        pagoRepository.findById(id)
                .filter(Pago::isActivo)
                .ifPresent(pago -> {
                    pago.setActivo(false);
                    pagoRepository.save(pago);
                });
    }


}