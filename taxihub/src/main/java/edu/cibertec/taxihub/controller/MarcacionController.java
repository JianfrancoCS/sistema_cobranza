package edu.cibertec.taxihub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/marcaciones")
@RequiredArgsConstructor
public class MarcacionController {

    @Value("${dynamosoft.token}")
    private String dynamosoftToken;

    @GetMapping("/entrada")
    public String mostrarEscanerEntrada(Model model) {
        model.addAttribute("pageTitle", "Marcación de Entrada - TaxiHub");
        model.addAttribute("dynamosoftToken", dynamosoftToken);
        model.addAttribute("tipoMarcacion", true);
        model.addAttribute("tipoMarcacionTexto", "Entrada");
        return "pages/marcaciones/entrada";
    }

    @GetMapping("/salida")
    public String mostrarEscanerSalida(Model model) {
        model.addAttribute("pageTitle", "Marcación de Salida - TaxiHub");
        model.addAttribute("dynamosoftToken", dynamosoftToken);
        model.addAttribute("tipoMarcacion", false);
        model.addAttribute("tipoMarcacionTexto", "Salida");
        return "pages/marcaciones/salida";
    }
}