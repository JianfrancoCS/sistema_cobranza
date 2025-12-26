package edu.cibertec.taxihub.controller;

import edu.cibertec.taxihub.dao.entity.ComisionPago;
import edu.cibertec.taxihub.usecase.IComisionPagoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/comisiones")
@RequiredArgsConstructor
public class ComisionController {

    private final IComisionPagoUseCase comisionPagoUseCase;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sort,
                        @RequestParam(defaultValue = "asc") String direction,
                        Model model) {

        Sort.Direction sortDirection = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<ComisionPago> comisiones = comisionPagoUseCase.listarComisionesActivas(pageable);

        model.addAttribute("comisiones", comisiones);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentSize", size);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);

        return "pages/comisiones/lista";
    }



    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ComisionPago> comisionOpt = Optional.empty();
        try {
            Page<ComisionPago> comisiones = comisionPagoUseCase.listarComisionesActivas(PageRequest.of(0, 100));
            comisionOpt = comisiones.getContent().stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al buscar la comisión.");
            return "redirect:/comisiones";
        }

        if (comisionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Comisión no encontrada.");
            return "redirect:/comisiones";
        }

        model.addAttribute("comisionPago", comisionOpt.get());
        model.addAttribute("isEditing", true);
        model.addAttribute("pageTitle", "Editar Comisión - TaxiHub");
        return "pages/comisiones/editar";
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                        @Valid @ModelAttribute("comisionPago") ComisionPago comision,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        

        if (result.hasErrors()) {
            try {
                Optional<ComisionPago> comisionOriginal = comisionPagoUseCase.listarComisionesActivas(PageRequest.of(0, 100))
                    .getContent().stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst();
                
                if (comisionOriginal.isPresent()) {

                    comision.setId(id);
                    comision.setCodigo(comisionOriginal.get().getCodigo());
                    comision.setFechaCreacion(comisionOriginal.get().getFechaCreacion());
                }
            } catch (Exception e) {
                comision.setId(id);
            }
            
            model.addAttribute("comisionPago", comision);
            model.addAttribute("isEditing", true);
            model.addAttribute("pageTitle", "Editar Comisión - TaxiHub");
            return "pages/comisiones/editar";
        }
        
        try {
            comisionPagoUseCase.actualizarComision(id, comision);
            redirectAttributes.addFlashAttribute("successMessage", "Comisión actualizada exitosamente.");
            return "redirect:/comisiones";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la comisión: " + e.getMessage());
            return "redirect:/comisiones/editar/" + id;
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            comisionPagoUseCase.eliminarComision(id);
            redirectAttributes.addFlashAttribute("successMessage", "Comisión eliminada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la comisión: " + e.getMessage());
        }
        return "redirect:/comisiones";
    }
}