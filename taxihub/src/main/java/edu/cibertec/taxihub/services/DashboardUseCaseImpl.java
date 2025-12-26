package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.constantes.EstadoPagoEnum;
import edu.cibertec.taxihub.dao.repository.AutoRepository;
import edu.cibertec.taxihub.dao.repository.DeudaRepository;
import edu.cibertec.taxihub.dao.repository.EmpleadoRepository;
import edu.cibertec.taxihub.dao.repository.PagoRepository;
import edu.cibertec.taxihub.dto.DashboardDTO;
import edu.cibertec.taxihub.usecase.IDashboardUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardUseCaseImpl implements IDashboardUseCase {

    private final DeudaRepository deudaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final AutoRepository autoRepository;
    private final PagoRepository pagoRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardDTO obtenerKpisPorFecha(LocalDate fecha) {
        
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);
        
        BigDecimal totalDeudasPorCobrar = deudaRepository.sumSaldoPendienteByActivoTrue()
                .orElse(BigDecimal.ZERO);
        
        Long empleadosActivosConAutoEmpresa = empleadoRepository.countEmpleadosActivosConAutoEmpresa();
        
        Long empleadosActivosConAutoPropio = empleadoRepository.countEmpleadosActivosConAutoPropio();
        
        BigDecimal totalPagosDelDia = pagoRepository.sumMontoByEstadoAndFechaCreacionBetween(
                EstadoPagoEnum.APROBADO.getDescripcion(), inicioDia, finDia).orElse(BigDecimal.ZERO);
        
        Long cantidadDeudasPendientes = deudaRepository.countByActivoTrueAndSaldoPendienteGreaterThan(BigDecimal.ZERO);

        Long totalEmpleadosActivos = empleadoRepository.countByActivoTrue();
        Long totalAutosEmpresa = autoRepository.countByEsPropioEmpresaTrue();
        Long totalAutosPersonales = autoRepository.countByEsPropioEmpresaFalse();
        
        return new DashboardDTO(
                fecha,
                totalDeudasPorCobrar,
                empleadosActivosConAutoEmpresa,
                empleadosActivosConAutoPropio,
                totalPagosDelDia,
                cantidadDeudasPendientes,
                totalEmpleadosActivos,
                totalAutosEmpresa,
                totalAutosPersonales
        );
    }
}