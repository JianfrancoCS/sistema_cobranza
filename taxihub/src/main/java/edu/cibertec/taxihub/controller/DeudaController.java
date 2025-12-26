package edu.cibertec.taxihub.controller;

import edu.cibertec.taxihub.dao.entity.Deuda;
import edu.cibertec.taxihub.usecase.IDeudaUseCase;
import edu.cibertec.taxihub.usecase.IUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/deudas")
@RequiredArgsConstructor
public class DeudaController {

    private final IDeudaUseCase deudaUseCase;
    private final IUsuarioUseCase usuarioUseCase;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sort,
                        @RequestParam(defaultValue = "asc") String direction,
                        @RequestParam(required = false) String numeroDocumento,
                        @RequestParam(required = false) Boolean pendiente,
                        @RequestParam(required = false) Boolean activo,
                        @RequestParam(required = false) String fecha,
                        Model model) {

        Sort.Direction sortDirection = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        LocalDate fechaFilter = null;
        if (fecha != null && !fecha.trim().isEmpty()) {
            try {
                fechaFilter = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                model.addAttribute("errorMessage", "Formato de fecha inválido. Use el formato yyyy-MM-dd");
            }
        }

        Page<Deuda> deudas = deudaUseCase.listarDeudasConFiltros(numeroDocumento, pendiente, activo, fechaFilter, pageable);

        model.addAttribute("deudas", deudas);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentSize", size);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);
        model.addAttribute("numeroDocumento", numeroDocumento);
        model.addAttribute("pendiente", pendiente);
        model.addAttribute("activo", activo);
        model.addAttribute("fecha", fecha);
        model.addAttribute("pageTitle", "Gestión de Deudas - TaxiHub");

        return "pages/deudas/lista";
    }

    @GetMapping(value = "/conductor")
    public String listarMisDeudas(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "id") String sort,
                                  @RequestParam(defaultValue = "desc") String direction,
                                  @RequestParam(required = false) Boolean pendiente,
                                  @RequestParam(required = false) Boolean activo,
                                  @RequestParam(required = false) String fecha,
                                  Authentication authentication,
                                  Model model) {

        String email = authentication.getName();
        String numeroDocumentoConductor = usuarioUseCase.obtenerNumeroDocumentoEmpleadoPorEmail(email);

        Sort.Direction sortDirection = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        LocalDate fechaFilter = null;
        if (fecha != null && !fecha.trim().isEmpty()) {
            try {
                fechaFilter = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                model.addAttribute("errorMessage", "Formato de fecha inválido. Use el formato yyyy-MM-dd");
            }
        }

        Page<Deuda> deudas;
        if (numeroDocumentoConductor == null) {
            deudas = Page.empty(pageable);
        } else {
            deudas = deudaUseCase.listarDeudasConFiltros(numeroDocumentoConductor, pendiente, activo, fechaFilter, pageable);
        }

        model.addAttribute("deudas", deudas);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentSize", size);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);
        model.addAttribute("pendiente", pendiente);
        model.addAttribute("activo", activo);
        model.addAttribute("fecha", fecha);
        model.addAttribute("pageTitle", "Mis Deudas - TaxiHub");

        return "pages/deudas/conductor/lista";
    }

    @PostMapping("/generar")
    @PreAuthorize("hasAuthority('CREAR_DEUDAS')")
    public String generarDeudas(RedirectAttributes redirectAttributes) {
        try {
            deudaUseCase.generarDeudasManualmente();
            redirectAttributes.addFlashAttribute("successMessage", "Deudas generadas/actualizadas correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al generar deudas: " + e.getMessage());
        }
        return "redirect:/deudas";
    }

}