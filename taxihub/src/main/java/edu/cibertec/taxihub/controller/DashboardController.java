package edu.cibertec.taxihub.controller;

import edu.cibertec.taxihub.dto.DashboardDTO;
import edu.cibertec.taxihub.usecase.IDashboardUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardUseCase dashboardUseCase;

    @GetMapping("")
    @PreAuthorize("hasAuthority('VER_DASHBOARD')")
    public String dashboard(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
                           Model model) {
        
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        
        try {
            DashboardDTO kpis = dashboardUseCase.obtenerKpisPorFecha(fecha);
            
            model.addAttribute("kpis", kpis);
            model.addAttribute("fechaSeleccionada", fecha);
            model.addAttribute("pageTitle", "Dashboard - TaxiHub");
            
            return "pages/dashboard/index";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar los KPIs: " + e.getMessage());
            model.addAttribute("fechaSeleccionada", fecha);
            model.addAttribute("pageTitle", "Dashboard - TaxiHub");
            return "pages/dashboard/index";
        }
    }
}