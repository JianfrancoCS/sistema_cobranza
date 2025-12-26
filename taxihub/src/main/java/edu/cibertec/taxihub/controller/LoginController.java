package edu.cibertec.taxihub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping({"","/"})
    public String index() {
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               Model model) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "Correo o contraseña incorrectos");
        }
        
        if (logout != null) {
            model.addAttribute("logoutMessage", "Ha cerrado sesión exitosamente");
        }
        
        model.addAttribute("pageTitle", "Iniciar Sesión - TaxiHub");
        return "pages/auth/login";
    }
}