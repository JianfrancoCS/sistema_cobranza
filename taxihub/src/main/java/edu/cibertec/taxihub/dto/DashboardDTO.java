package edu.cibertec.taxihub.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardDTO(
        LocalDate fechaConsulta,
        BigDecimal totalDeudasPorCobrar,
        Long empleadosActivosConAutoEmpresa,
        Long empleadosActivosConAutoPropio,
        BigDecimal totalPagosDelDia,
        Long cantidadDeudasPendientes,
        Long totalEmpleadosActivos,
        Long totalAutosEmpresa,
        Long totalAutosPersonales
) {
}