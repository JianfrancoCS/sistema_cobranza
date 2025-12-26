package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.dao.entity.Auto;
import edu.cibertec.taxihub.dao.entity.ComisionPago;
import edu.cibertec.taxihub.dao.entity.Deuda;
import edu.cibertec.taxihub.dao.entity.Empleado;
import edu.cibertec.taxihub.dao.repository.*;
import edu.cibertec.taxihub.dao.specification.DeudaPorActivoSpec;
import edu.cibertec.taxihub.dao.specification.DeudaPorEmpleadoSpec;
import edu.cibertec.taxihub.dao.specification.DeudaPorEstadoSpec;
import edu.cibertec.taxihub.dao.specification.DeudaPorFechaSpec;
import edu.cibertec.taxihub.usecase.IDeudaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeudaUseCaseImpl implements IDeudaUseCase {

    private final DeudaRepository deudaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final AutoRepository autoRepository;
    private final MarcacionRepository marcacionRepository;
    private final ComisionPagoRepository comisionPagoRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Deuda> listarDeudasConFiltros(String numeroDocumento, Boolean pendiente, Boolean activo, LocalDate fecha, Pageable pageable) {
        Specification<Deuda> spec = Specification
                .where(DeudaPorEmpleadoSpec.porNumeroDocumento(numeroDocumento))
                .and(DeudaPorActivoSpec.conActivo(activo))
                .and(DeudaPorFechaSpec.porFechaCreacion(fecha));

        if (pendiente != null) {
            if (pendiente) {
                spec = spec.and(DeudaPorEstadoSpec.pendientes());
            } else {
                spec = spec.and(DeudaPorEstadoSpec.saldadas().or(DeudaPorEstadoSpec.sobrepagadas()));
            }
        }

        return deudaRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Deuda> listarDeudasPendientesPorEmpleado(String numeroDocumento, Pageable pageable) {
        Specification<Deuda> spec = Specification
                .where(DeudaPorEmpleadoSpec.porNumeroDocumento(numeroDocumento))
                .and(DeudaPorActivoSpec.activas())
                .and(DeudaPorEstadoSpec.pendientes());

        return deudaRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Deuda> listarDeudasSaldadasPorEmpleado(String numeroDocumento, Pageable pageable) {
        Specification<Deuda> spec = Specification
                .where(DeudaPorEmpleadoSpec.porNumeroDocumento(numeroDocumento))
                .and(DeudaPorActivoSpec.activas())
                .and(DeudaPorEstadoSpec.saldadas());

        return deudaRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalDeudaPendientePorEmpleado(String numeroDocumento) {
        return deudaRepository.getTotalDeudaPendienteByEmpleadoPersonaNumeroDocumento(numeroDocumento);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Deuda> buscarDeudaPorId(Long id) {
        return deudaRepository.findById(id).filter(Deuda::isActivo);
    }

    @Override
    @Transactional
    public Deuda crearDeuda(String empleadoId, BigDecimal montoDeuda) {
        Optional<Empleado> byPersonaNumeroDocumento = empleadoRepository.findByPersonaNumeroDocumento(empleadoId);
        Empleado empleado = byPersonaNumeroDocumento.get();
        Deuda deuda = new Deuda();
        deuda.setEmpleado(empleado);
        deuda.setMontoDeuda(montoDeuda);
        deuda.setMontoPagado(BigDecimal.ZERO);
        return deudaRepository.save(deuda);
    }

    @Override
    @Transactional
    public Deuda actualizarMontoPagado(Long deudaId, BigDecimal montoAdicional) {
        return deudaRepository.findById(deudaId)
                .filter(Deuda::isActivo)
                .map(deuda -> {
                    BigDecimal nuevoMontoPagado = deuda.getMontoPagado().add(montoAdicional);
                    deuda.setMontoPagado(nuevoMontoPagado);
                    return deudaRepository.save(deuda);
                })
                .orElseThrow(() -> new RuntimeException("Deuda no encontrada con ID: " + deudaId));
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 59 23 * * *")
    public void procesarDeudasDiarias() {
        procesarDeudasInterno();
    }

    @Override
    @Transactional
    public void generarDeudasManualmente() {
        procesarDeudasInterno();
    }

    private void procesarDeudasInterno() {
        log.info("Iniciando proceso de generación de deudas diarias...");
        
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioDelDia = hoy.atStartOfDay();
        LocalDateTime finDelDia = hoy.atTime(LocalTime.MAX);
        
        log.info("Fecha actual (LocalDate.now()): {}", hoy);
        log.info("Inicio del día: {}", inicioDelDia);
        log.info("Fin del día: {}", finDelDia);

        List<Empleado> empleadosConAuto = empleadoRepository.findAll().stream()
                .filter(Empleado::isActivo)
                .filter(empleado -> {
                    List<Auto> autos = autoRepository.findByEmpleadoPersonaNumeroDocumentoAndActivoTrue(empleado.getPersona().getNumeroDocumento());
                    return !autos.isEmpty();
                })
                .toList();

        for (Empleado empleado : empleadosConAuto) {
            try {
                procesarDeudaEmpleado(empleado, inicioDelDia, finDelDia);
            } catch (Exception e) {
                log.error("Error procesando deuda para empleado {}: {}", empleado.getPersona().getNumeroDocumento(), e.getMessage());
            }
        }

        log.info("Proceso de generación de deudas diarias completado");
    }

    private void procesarDeudaEmpleado(Empleado empleado, LocalDateTime inicioDelDia, LocalDateTime finDelDia) {
        log.info("--- Procesando empleado: {} ---", empleado.getPersona().getNumeroDocumento());
        log.info("Buscando salidas entre: {} y {}", inicioDelDia, finDelDia);
        
        long salidas = marcacionRepository.countSalidasByEmpleadoIdAndFechaBetween(
               empleado.getPersona().getNumeroDocumento(), inicioDelDia, finDelDia);

        log.info("Salidas encontradas para empleado {}: {}",empleado.getPersona().getNumeroDocumento(), salidas);

        if (salidas == 0) {
            log.info("No hay salidas para empleado {} en el rango de fechas",empleado.getPersona().getNumeroDocumento());
            return;
        }

        List<Auto> autos = autoRepository.findByEmpleadoPersonaNumeroDocumentoAndActivoTrue(empleado.getPersona().getNumeroDocumento());
        if (autos.isEmpty()) {
            log.warn("Empleado {} no tiene auto asignado",empleado.getPersona().getNumeroDocumento());
            return;
        }

        Auto auto = autos.getFirst();

        String codigoComision = auto.getEsPropioEmpresa() ?
                "comisionSalidaVehiculoEmpresa" : "comisionSalidaVehiculoEmpleado";

        Optional<ComisionPago> comisionOpt = comisionPagoRepository.findByCodigoAndActivoTrue(codigoComision);
        if (comisionOpt.isEmpty()) {
            log.error("No se encontró comisión con código: {}", codigoComision);
            return;
        }

        ComisionPago comision = comisionOpt.get();
        BigDecimal montoDeudaNuevo = comision.getMonto().multiply(new BigDecimal(salidas));

        Optional<Deuda> deudaExistente = deudaRepository.findByEmpleadoPersonaNumeroDocumentoAndActivoTrueAndFechaCreacionBetween(
               empleado.getPersona().getNumeroDocumento(), inicioDelDia, finDelDia);

        if (deudaExistente.isPresent()) {
            Deuda deuda = deudaExistente.get();
            BigDecimal montoAnterior = deuda.getMontoDeuda();
            deuda.setMontoDeuda(montoDeudaNuevo);
            deudaRepository.save(deuda);
            
            log.info("Deuda actualizada para empleado {}: {} salidas x {} = {} (anterior: {})", 
                   empleado.getPersona().getNumeroDocumento(), salidas, comision.getMonto(), montoDeudaNuevo, montoAnterior);
        } else {
            crearDeuda(empleado.getPersona().getNumeroDocumento(), montoDeudaNuevo);
            
            log.info("Deuda creada para empleado {}: {} salidas x {} = {}", 
                   empleado.getPersona().getNumeroDocumento(), salidas, comision.getMonto(), montoDeudaNuevo);
        }
    }
}