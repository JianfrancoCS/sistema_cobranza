package edu.cibertec.taxihub.controller;

import edu.cibertec.taxihub.services.GrupoUseCaseImpl;
import edu.cibertec.taxihub.usecase.IGrupoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final IGrupoUseCase grupoUseCase;

    @GetMapping
    public String listarGrupos(@RequestParam(required = false) String nombre, Model model) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            model.addAttribute("grupos", grupoUseCase.buscarGruposPorNombre(nombre.trim()));
        } else {
            model.addAttribute("grupos", grupoUseCase.listarGrupos());
        }
        model.addAttribute("nombreFiltro", nombre);
        return "pages/grupos/lista";
    }
}