package edu.cibertec.taxihub.controller;

import edu.cibertec.taxihub.dao.entity.Autoridad;
import edu.cibertec.taxihub.dao.entity.Persona;
import edu.cibertec.taxihub.dao.entity.Usuario;
import edu.cibertec.taxihub.services.GrupoUseCaseImpl;
import edu.cibertec.taxihub.usecase.IAutoridadUseCase;
import edu.cibertec.taxihub.usecase.IUsuarioUseCase;
import org.springframework.data.domain.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import edu.cibertec.taxihub.dto.UsuarioFormDTO;
import edu.cibertec.taxihub.dto.CambiarContrasenaDTO;
import edu.cibertec.taxihub.dao.entity.Empleado;
import edu.cibertec.taxihub.dao.entity.Licencia;
import edu.cibertec.taxihub.usecase.IEmpleadoUseCase;
import edu.cibertec.taxihub.usecase.ILicenciaUseCase;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final IUsuarioUseCase usuarioUseCase;
    private final IAutoridadUseCase autoridadUseCase;
    private final GrupoUseCaseImpl grupoUseCase;
    private final IEmpleadoUseCase empleadoUseCase;
    private final ILicenciaUseCase licenciaUseCase;

    @GetMapping
    public String listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String correo,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        Page<Usuario> usuariosPage = usuarioUseCase.listarUsuarios(correo, true, pageable);

        if (size != 10 && size != 25 && size != 50) {
            size = 10;
        }

        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("correoFiltro", correo);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", usuariosPage.getTotalPages());
        model.addAttribute("totalElements", usuariosPage.getTotalElements());

        return "pages/usuarios/lista";
    }

    @GetMapping("/crear")
    @PreAuthorize("hasAuthority('CREAR_USUARIOS')")
    public String mostrarFormularioCrear(
            @RequestParam(required = false) String dni,
            Model model) {
        
        if (dni == null || dni.trim().isEmpty()) {
            return "pages/usuarios/crear";
        }
        
        validarYCargarDatosEmpleado(model, dni.trim());
        
        UsuarioFormDTO usuarioForm = new UsuarioFormDTO();
        model.addAttribute("usuarioForm", usuarioForm);
        return "pages/usuarios/crear";
    }

    @PostMapping("/crear")
    public String crearUsuario(
            @RequestParam() String dni,
            @Valid @ModelAttribute("usuarioForm") UsuarioFormDTO usuarioForm,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!result.hasErrors()) {
            Optional<Usuario> usuarioExistente = usuarioUseCase.buscarUsuarioPorEmpleado(dni.trim());
            usuarioExistente.ifPresent(usuario -> result.rejectValue("correo", "error.empleado.duplicado",
                    "Ya existe un usuario para este empleado con correo: " + usuario.getCorreo()));
        }

        if (result.hasErrors()) {
            cargarDatosCompletos(model, dni);
            return "pages/usuarios/crear";
        }

        try {
            log.warn("SECURITY_AUDIT - Creando usuario | DNI empleado: {} | Correo: {} | Autoridades: {} | Grupo: {}", 
                    dni.trim(), usuarioForm.getCorreo(), usuarioForm.getAutoridadIds(), usuarioForm.getGrupoId());
            
            byte[] imagenBytes = null;
            String imagenTipo = null;
            
            if (usuarioForm.getImagen() != null && !usuarioForm.getImagen().isEmpty()) {
                try {
                    imagenBytes = procesarImagenBytes(usuarioForm.getImagen());
                    imagenTipo = usuarioForm.getImagen().getContentType();
                } catch (java.io.IOException e) {
                    cargarDatosCompletos(model, dni);
                    model.addAttribute("errorMessage", "Error al procesar la imagen: " + e.getMessage());
                    return "pages/usuarios/crear";
                }
            }
            
            Optional<Usuario> usuarioCreado = usuarioUseCase.crearUsuario(
                usuarioForm.getCorreo(), 
                usuarioForm.getContrasena(), 
                usuarioForm.isContrasenaTemporal(),
                usuarioForm.getAutoridadIds(), 
                usuarioForm.getGrupoId(),
                dni.trim(),
                imagenBytes,
                imagenTipo,
                usuarioForm.isActivo()
            );
            
            if (usuarioCreado.isPresent()) {
                redirectAttributes.addFlashAttribute("successMessage", "Usuario creado correctamente");
                return "redirect:/usuarios";
            } else {
                cargarDatosCompletos(model, dni);
                model.addAttribute("errorMessage", "Error al crear usuario: El correo ya existe o datos inválidos");
                return "pages/usuarios/crear";
            }
        } catch (Exception e) {
            cargarDatosCompletos(model, dni);
            model.addAttribute("errorMessage", "Error al crear usuario: " + e.getMessage());
            return "pages/usuarios/crear";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioUseCase.buscarUsuarioPorIdOptional(id);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado");
            return "redirect:/usuarios";
        }
        Usuario usuario = usuarioOpt.get();
        
        
        List<Long> autoridadIds = usuario.getAutoridades() != null ?
            usuario.getAutoridades().stream().map(Autoridad::getId).toList() : null;
        Long grupoId = usuario.getGrupos() != null && !usuario.getGrupos().isEmpty() ? 
            usuario.getGrupos().iterator().next().getId() : null;
            
        UsuarioFormDTO usuarioForm = new UsuarioFormDTO(
            usuario.getCorreo(),
            "",
            usuario.getContrasenaTemporal(),
            autoridadIds,
            grupoId,
            null,
            usuario.isActivo()
        );
        
        model.addAttribute("usuarioForm", usuarioForm);
        model.addAttribute("usuario", usuario);
        model.addAttribute("autoridades", autoridadUseCase.listarAutoridades());
        
        String numeroDocumento = usuario.getEmpleado() != null ? usuario.getEmpleado().getPersona().getNumeroDocumento() : null;
        cargarGruposFiltrados(model, numeroDocumento);
        
        return "pages/usuarios/editar";
    }

    @PostMapping("/editar/{id}")
    public String editarUsuario(
            @PathVariable Long id,
            @Valid @ModelAttribute("usuarioForm") UsuarioFormDTO usuarioForm,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            Optional<Usuario> usuarioOpt = usuarioUseCase.buscarUsuarioPorIdOptional(id);
            if (usuarioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado");
                return "redirect:/usuarios";
            }
            Usuario usuario = usuarioOpt.get();
            model.addAttribute("usuario", usuario);
            model.addAttribute("autoridades", autoridadUseCase.listarAutoridades());
            
            String numeroDocumento = usuario.getEmpleado() != null ? usuario.getEmpleado().getPersona().getNumeroDocumento() : null;
            cargarGruposFiltrados(model, numeroDocumento);
            
            return "pages/usuarios/editar";
        }

        Optional<Usuario> usuarioActualizado;
        if (usuarioForm.getImagen() != null && !usuarioForm.getImagen().isEmpty()) {
            try {
                usuarioActualizado = usuarioUseCase.actualizarUsuarioConImagen(
                        id,
                        usuarioForm.getCorreo(),
                        usuarioForm.getAutoridadIds(),
                        usuarioForm.getGrupoId(),
                        procesarImagenBytes(usuarioForm.getImagen()),
                        usuarioForm.getImagen().getContentType()
                );
            } catch (java.io.IOException e) {
                Optional<Usuario> usuarioOpt = usuarioUseCase.buscarUsuarioPorIdOptional(id);
                if (usuarioOpt.isEmpty()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado");
                    return "redirect:/usuarios";
                }
                model.addAttribute("usuario", usuarioOpt.get());
                model.addAttribute("autoridades", autoridadUseCase.listarAutoridades());
                model.addAttribute("grupos", grupoUseCase.listarGrupos());
                model.addAttribute("errorMessage", "Error al procesar la imagen: " + e.getMessage());
                return "pages/usuarios/editar";
            }
        } else {
            usuarioActualizado = usuarioUseCase.actualizarUsuario(id, usuarioForm.getCorreo(), usuarioForm.getAutoridadIds(), usuarioForm.getGrupoId());
        }
        
        if (usuarioActualizado.isPresent()) {
            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar usuario: El correo ya existe o datos inválidos");
        }

        return "redirect:/usuarios";
    }

    @PostMapping("/cambiar-contrasena/{id}")
    public String cambiarContrasena(
            @PathVariable Long id,
            @RequestParam String nuevaContrasena,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioUseCase.buscarUsuarioPorIdOptional(id);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado");
            return "redirect:/usuarios";
        }
        
        Usuario usuario = usuarioOpt.get();
        log.error("CRITICAL_AUDIT - Admin: {} | RESET CONTRASEÑA | Usuario afectado: {} | ID: {}", 
                 authentication.getName(), usuario.getCorreo(), id);

        usuarioUseCase.cambiarContrasena(id, nuevaContrasena);
        redirectAttributes.addFlashAttribute("successMessage", "Contraseña cambiada correctamente");

        return "redirect:/usuarios";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, 
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioUseCase.buscarUsuarioPorIdOptional(id);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado");
            return "redirect:/usuarios";
        }
        
        Usuario usuario = usuarioOpt.get();
        log.error("CRITICAL_AUDIT - Admin: {} | ELIMINANDO USUARIO | Usuario: {} | DNI: {} | ID: {}", 
                 authentication.getName(), usuario.getCorreo(), 
                 usuario.getEmpleado() != null ? usuario.getEmpleado().getPersona().getNumeroDocumento() : "Sin empleado", id);
        
        usuarioUseCase.eliminarUsuario(id);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado correctamente");
        return "redirect:/usuarios";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Authentication authentication, Model model) {
        String correoUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioUseCase.buscarUsuarioPorCorreo(correoUsuario);
        
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        Usuario usuario = usuarioOpt.get();
        model.addAttribute("usuario", usuario);
        
        if (usuario.getEmpleado() != null) {
            model.addAttribute("empleado", usuario.getEmpleado());
        }
        
        return "pages/usuarios/perfil";
    }

    @GetMapping("/perfil/cambiar-contrasena")
    public String mostrarCambiarContrasena(Model model) {
        model.addAttribute("cambiarContrasenaForm", new CambiarContrasenaDTO("", "", ""));
        return "pages/usuarios/cambiar-contrasena";
    }

    @PostMapping("/perfil/cambiar-contrasena")
    public String cambiarContrasenaPropia(
            @Valid @ModelAttribute("cambiarContrasenaForm") CambiarContrasenaDTO cambiarContrasenaForm,
            BindingResult result,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        String correoUsuario = authentication.getName();

        if (!result.hasErrors()) {
            if (!usuarioUseCase.validarContrasenaActual(correoUsuario, cambiarContrasenaForm.contrasenaActual())) {
                result.rejectValue("contrasenaActual", "error.contrasenaActual", "La contraseña actual es incorrecta");
            }

            if (!cambiarContrasenaForm.nuevaContrasena().equals(cambiarContrasenaForm.confirmarContrasena())) {
                result.rejectValue("confirmarContrasena", "error.confirmarContrasena", "Las contraseñas no coinciden");
            }
        }

        if (result.hasErrors()) {
            log.debug("Errores de validación encontrados: {}", result.getAllErrors());
            model.addAttribute("cambiarContrasenaForm", cambiarContrasenaForm);
            return "pages/usuarios/cambiar-contrasena";
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioUseCase.buscarUsuarioPorCorreo(correoUsuario);
            
            if (usuarioOpt.isEmpty()) {
                result.rejectValue("contrasenaActual", "error.general", "Usuario no encontrado");
                model.addAttribute("cambiarContrasenaForm", cambiarContrasenaForm);
                return "pages/usuarios/cambiar-contrasena";
            }
            
            Usuario usuario = usuarioOpt.get();
            
            usuarioUseCase.cambiarContrasena(usuario.getId(), cambiarContrasenaForm.nuevaContrasena());
            redirectAttributes.addFlashAttribute("successMessage", "Contraseña cambiada correctamente");
            return "redirect:/usuarios/perfil";
        } catch (Exception e) {
            result.rejectValue("nuevaContrasena", "error.general", "Error al cambiar la contraseña: " + e.getMessage());
            model.addAttribute("cambiarContrasenaForm", cambiarContrasenaForm);
            return "pages/usuarios/cambiar-contrasena";
        }
    }

    @PostMapping("/perfil/cambiar-foto")
    public String cambiarFotoPerfil(
            @RequestParam("imagen") MultipartFile imagen,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (imagen.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debe seleccionar una imagen");
            return "redirect:/usuarios/perfil";
        }

        try {
            String correoUsuario = authentication.getName();
            Optional<Usuario> usuarioOpt = usuarioUseCase.buscarUsuarioPorCorreo(correoUsuario);
            
            if (usuarioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado");
                return "redirect:/usuarios/perfil";
            }
            
            Usuario usuario = usuarioOpt.get();
            
            if (usuario.getEmpleado() == null || usuario.getEmpleado().getPersona() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "No se puede cambiar la foto. Usuario sin empleado asociado");
                return "redirect:/usuarios/perfil";
            }

            Persona persona = usuario.getEmpleado().getPersona();
            persona.setImagen(procesarImagenBytes(imagen));
            persona.setImagenTipo(imagen.getContentType());
            
            Optional<Usuario> usuarioActualizado = usuarioUseCase.actualizarUsuario(usuario.getId(), usuario.getCorreo(),
                usuario.getAutoridades().stream().map(Autoridad::getId).toList(),
                usuario.getGrupos().isEmpty() ? null : usuario.getGrupos().iterator().next().getId());
            
            if (usuarioActualizado.isPresent()) {
                redirectAttributes.addFlashAttribute("successMessage", "Foto actualizada correctamente");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la foto del usuario");
            }
            return "redirect:/usuarios/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cambiar la foto: " + e.getMessage());
            return "redirect:/usuarios/perfil";
        }
    }

    private void validarYCargarDatosEmpleado(Model model, String numeroDocumento) {
        Optional<Usuario> usuarioExistente = usuarioUseCase.buscarUsuarioPorEmpleado(numeroDocumento);
        if (usuarioExistente.isPresent()) {
            model.addAttribute("errorMessage", 
                "Ya existe un usuario para este empleado con correo: " + usuarioExistente.get().getCorreo());
            return;
        }
        
        Optional<Empleado> empleadoOpt = empleadoUseCase.buscarEmpleadoPorNumeroDocumento(numeroDocumento);
        if (empleadoOpt.isEmpty()) {
            model.addAttribute("errorMessage", "No se encontró empleado con el DNI proporcionado");
            return;
        }

        model.addAttribute("autoridades", autoridadUseCase.listarAutoridades());
        cargarGruposFiltrados(model, numeroDocumento);
        model.addAttribute("empleado", empleadoOpt.get());
    }
    
    private void cargarDatosCompletos(Model model, String numeroDocumento) {
        model.addAttribute("autoridades", autoridadUseCase.listarAutoridades());
        cargarGruposFiltrados(model, numeroDocumento);

        if (numeroDocumento != null && !numeroDocumento.trim().isEmpty()) {
            Optional<Empleado> empleadoOpt = empleadoUseCase.buscarEmpleadoPorNumeroDocumento(numeroDocumento.trim());
            empleadoOpt.ifPresent(empleado -> model.addAttribute("empleado", empleado));
        }
    }
    private byte[] procesarImagenBytes(MultipartFile imagen) throws IOException {
        return imagen.getBytes();
    }
    
    private void cargarGruposFiltrados(Model model, String numeroDocumento) {
        if (numeroDocumento != null && !numeroDocumento.trim().isEmpty()) {
            try {
                Optional<Licencia> licenciaOpt = licenciaUseCase.buscarLincenciaPorDni(numeroDocumento.trim());
                if (licenciaOpt.isPresent() && licenciaOpt.get().isLicenciaVigente()) {
                    model.addAttribute("grupos", grupoUseCase.listarGrupos());
                } else {
                    var gruposFiltrados = grupoUseCase.listarGrupos().stream()
                        .filter(grupo -> !grupo.getNombreGrupo().toLowerCase().contains("conductor"))
                        .collect(java.util.stream.Collectors.toList());
                    model.addAttribute("grupos", gruposFiltrados);
                    model.addAttribute("sinLicenciaMessage", "Este empleado no tiene licencia vigente. No puede ser asignado al grupo CONDUCTOR.");
                }
            } catch (Exception e) {
                var gruposFiltrados = grupoUseCase.listarGrupos().stream()
                    .filter(grupo -> !grupo.getNombreGrupo().toLowerCase().contains("conductor"))
                    .collect(java.util.stream.Collectors.toList());
                model.addAttribute("grupos", gruposFiltrados);
                model.addAttribute("warningMessage", "No se pudo verificar la licencia del empleado.");
            }
        } else {
            model.addAttribute("grupos", grupoUseCase.listarGrupos());
        }
    }
}