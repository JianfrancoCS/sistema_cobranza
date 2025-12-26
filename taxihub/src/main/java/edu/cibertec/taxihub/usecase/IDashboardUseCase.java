package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dto.DashboardDTO;

import java.time.LocalDate;

public interface IDashboardUseCase {
    
    DashboardDTO obtenerKpisPorFecha(LocalDate fecha);
    
}