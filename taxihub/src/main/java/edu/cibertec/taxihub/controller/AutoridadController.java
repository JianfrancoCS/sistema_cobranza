package edu.cibertec.taxihub.controller;

import edu.cibertec.taxihub.usecase.IAutoridadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/autoridades")
@RequiredArgsConstructor
public class AutoridadController {

    private final IAutoridadUseCase autoridadUseCase;

    @GetMapping
    public String listarAutoridades(@RequestParam(required = false) String nombre, Model model) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            model.addAttribute("autoridades", autoridadUseCase.buscarAutoridadesPorNombre(nombre.trim()));
        } else {
            model.addAttribute("autoridades", autoridadUseCase.listarAutoridades());
        }
        model.addAttribute("nombreFiltro", nombre);
        return "pages/autoridades/lista";
    }
}