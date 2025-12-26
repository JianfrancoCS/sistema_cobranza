package edu.cibertec.taxihub.controller;

import edu.cibertec.taxihub.dto.AutoFormDTO;
import edu.cibertec.taxihub.dto.AsignarConductorDTO;
import edu.cibertec.taxihub.dao.entity.Auto;
import edu.cibertec.taxihub.dao.entity.Empleado;
import edu.cibertec.taxihub.usecase.IAutoUseCase;
import edu.cibertec.taxihub.usecase.IEmpleadoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/autos")
@RequiredArgsConstructor
public class AutoController {

    private final IAutoUseCase autoUseCase;
    private final IEmpleadoUseCase empleadoUseCase;

    @GetMapping
    public String listarAutos(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String buscarPlaca,
                              @RequestParam(required = false) String marca,
                              @RequestParam(required = false) Boolean activo,
                              @RequestParam(required = false) Boolean disponibles,
                              Model model) {
        try {
            if (size != 10 && size != 25 && size != 50) {
                size = 10;
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Auto> autosPage = autoUseCase.listarAutosConFiltros(buscarPlaca, marca, activo, disponibles, pageable);

            List<Auto> autosConImagenes = autosPage.getContent().stream()
                    .peek(auto -> {
                        if (auto.getImagen() != null) {
                            String imagenBase64 = Base64.getEncoder().encodeToString(auto.getImagen());
                            auto.setImagenBase64(imagenBase64);
                        }
                    })
                    .toList();

            model.addAttribute("autos", autosConImagenes);
            model.addAttribute("totalPages", autosPage.getTotalPages());
            model.addAttribute("totalElements", autosPage.getTotalElements());
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("buscarPlaca", buscarPlaca);
            model.addAttribute("marca", marca);
            model.addAttribute("activo", activo);
            model.addAttribute("disponibles", disponibles);
            model.addAttribute("pageTitle", "Gestión de Autos - TaxiHub");

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar autos: " + e.getMessage());
        }
        return "pages/autos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(
            @RequestParam(value = "placa", required = false) String placa,
            @RequestParam(value = "numeroDocumento", required = false) String numeroDocumento,
            Model model) {

        AutoFormDTO autoFormDTO = new AutoFormDTO("", "", "", "", "", "", "", null, "", false, true);

        Auto autoEncontrado = null;
        Empleado empleadoEncontrado = null;
        boolean hayBusqueda = (placa != null && !placa.trim().isEmpty()) ||
                (numeroDocumento != null && !numeroDocumento.trim().isEmpty());

        model.addAttribute("modo", "crear");
        model.addAttribute("pageTitle", "Nuevo Auto - TaxiHub");
        model.addAttribute("hayBusqueda", hayBusqueda);

        if (hayBusqueda) {
            try {
                if (placa != null && !placa.trim().isEmpty()) {
                    Optional<Auto> autoExistente = autoUseCase.buscarAutoPorPlaca(placa.trim());
                    if (autoExistente.isPresent()) {
                        model.addAttribute("errorMessage", "El auto con placa " + placa + " ya está registrado en el sistema.");
                        return cargarVistaCrear(autoFormDTO, null, null, placa, numeroDocumento, model);
                    }

                    Optional<Auto> autoExterno = autoUseCase.obtenerInformacionVehiculo(placa.trim());
                    if (autoExterno.isPresent()) {
                        autoEncontrado = autoExterno.get();
                        autoFormDTO = new AutoFormDTO(
                                autoEncontrado.getPlaca(),
                                autoEncontrado.getMarca(),
                                autoEncontrado.getModelo(),
                                autoEncontrado.getSerie(),
                                autoEncontrado.getColor(),
                                autoEncontrado.getMotor(),
                                autoEncontrado.getVin(),
                                null,
                                "",
                                false,
                                true
                        );
                        model.addAttribute("successMessage", "Auto encontrado en sistema externo con placa " + placa);
                    } else {
                        model.addAttribute("errorMessage", "No se encontró información del auto con la placa " + placa);
                        return cargarVistaCrear(autoFormDTO, null, null, placa, numeroDocumento, model);
                    }
                }

                if (numeroDocumento != null && !numeroDocumento.trim().isEmpty()) {
                    Optional<Empleado> empleadoOpt = empleadoUseCase.buscarEmpleadoPorNumeroDocumento(numeroDocumento.trim());
                    if (empleadoOpt.isPresent()) {
                        empleadoEncontrado = empleadoOpt.get();

                        if (!empleadoUseCase.esConductor(numeroDocumento.trim())) {
                            model.addAttribute("errorMessage", "El empleado con DNI " + numeroDocumento + " no es un conductor. Solo los conductores pueden ser asignados a autos.");
                            return cargarVistaCrear(autoFormDTO, autoEncontrado, null, placa, numeroDocumento, model);
                        }

                        if (autoUseCase.empleadoTieneAutoAsignado(numeroDocumento.trim())) {
                            model.addAttribute("errorMessage", "El empleado con DNI " + numeroDocumento + " ya tiene un auto asignado.");
                            return cargarVistaCrear(autoFormDTO, autoEncontrado, null, placa, numeroDocumento, model);
                        }

                        autoFormDTO = new AutoFormDTO(
                                autoFormDTO.placa(),
                                autoFormDTO.marca(),
                                autoFormDTO.modelo(),
                                autoFormDTO.serie(),
                                autoFormDTO.color(),
                                autoFormDTO.motor(),
                                autoFormDTO.vin(),
                                null,
                                empleadoEncontrado.getPersona().getNumeroDocumento(),
                                autoFormDTO.esPropioEmpresa(),
                                autoFormDTO.activo()
                        );
                    } else {
                        model.addAttribute("errorMessage", "No se encontró un empleado con el DNI " + numeroDocumento);
                        return cargarVistaCrear(autoFormDTO, autoEncontrado, null, placa, numeroDocumento, model);
                    }
                }

            } catch (Exception e) {
                model.addAttribute("errorMessage", "Error en la búsqueda: " + e.getMessage());
                return cargarVistaCrear(autoFormDTO, null, null, placa, numeroDocumento, model);
            }
        }

        return cargarVistaCrear(autoFormDTO, autoEncontrado, empleadoEncontrado, placa, numeroDocumento, model);
    }

    private String cargarVistaCrear(AutoFormDTO autoFormDTO, Auto autoEncontrado, Empleado empleadoEncontrado,
                                    String placa, String numeroDocumento, Model model) {

        if (autoEncontrado != null && autoEncontrado.getImagen() != null) {
            String imagenBase64 = Base64.getEncoder().encodeToString(autoEncontrado.getImagen());
            autoEncontrado.setImagenBase64(imagenBase64);
        }

        List<Empleado> conductoresDisponibles = empleadoEncontrado == null ?
            empleadoUseCase.listarConductoresDisponibles() : List.of();
        model.addAttribute("autoFormDTO", autoFormDTO);
        model.addAttribute("auto", autoEncontrado);
        model.addAttribute("empleadoBuscado", empleadoEncontrado);
        model.addAttribute("conductores", conductoresDisponibles);
        model.addAttribute("placa", placa);
        model.addAttribute("numeroDocumento", numeroDocumento);

        return "pages/autos/admin/crear";
    }

    @PostMapping("/guardar")
    public String crearAuto(@Valid @ModelAttribute("autoFormDTO") AutoFormDTO autoFormDTO,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return recargarVistaConErrores(autoFormDTO, bindingResult, model, "crear");
        }

        try {
            Optional<Auto> autoExistente = autoUseCase.buscarAutoPorPlaca(autoFormDTO.placa());
            if (autoExistente.isPresent()) {
                bindingResult.rejectValue("placa", "error.placa", "El auto con placa " + autoFormDTO.placa() + " ya está registrado");
                return recargarVistaConErrores(autoFormDTO, bindingResult, model, "crear");
            }
            Auto nuevoAuto = mapearFormDTOaAuto(autoFormDTO, null);

            if (autoFormDTO.empleadoId() != null && !autoFormDTO.empleadoId().trim().isEmpty()) {
                if (!empleadoUseCase.esConductor(autoFormDTO.empleadoId().trim())) {
                    bindingResult.rejectValue("empleadoId", "error.empleadoId", "El empleado no es un conductor. Solo los conductores pueden ser asignados a autos.");
                    return recargarVistaConErrores(autoFormDTO, bindingResult, model, "crear");
                }
                
                Optional<Empleado> empleadoOpt = empleadoUseCase.buscarEmpleadoPorNumeroDocumento(autoFormDTO.empleadoId());
                if (empleadoOpt.isEmpty()) {
                    bindingResult.rejectValue("empleadoId", "error.empleadoId", "Empleado no encontrado con DNI: " + autoFormDTO.empleadoId());
                    return recargarVistaConErrores(autoFormDTO, bindingResult, model, "crear");
                }
                nuevoAuto.setEmpleado(empleadoOpt.get());
            }

            autoUseCase.crearAuto(nuevoAuto);
            redirectAttributes.addFlashAttribute("successMessage", "Auto creado exitosamente.");
            return "redirect:/autos";

        } catch (Exception e) {
            bindingResult.reject("error.general", "Error al crear auto: " + e.getMessage());
            return recargarVistaConErrores(autoFormDTO, bindingResult, model, "crear");
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Auto> autoOpt = autoUseCase.buscarAutoPorId(id);
            if (autoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Auto no encontrado con ID: " + id);
                return "redirect:/autos";
            }
            Auto auto = autoOpt.get();

            if (auto.getImagen() != null) {
                String imagenBase64 = Base64.getEncoder().encodeToString(auto.getImagen());
                auto.setImagenBase64(imagenBase64);
            }

            AutoFormDTO autoFormDTO = new AutoFormDTO(
                    auto.getPlaca(),
                    auto.getMarca(),
                    auto.getModelo(),
                    auto.getSerie(),
                    auto.getColor(),
                    auto.getMotor(),
                    auto.getVin(),
                    null,
                    auto.getEmpleado() != null ? auto.getEmpleado().getPersona().getNumeroDocumento() : "",
                    auto.getEsPropioEmpresa(),
                    auto.isActivo()
            );

            List<Empleado> conductoresDisponibles = empleadoUseCase.listarConductoresDisponibles();

            model.addAttribute("autoFormDTO", autoFormDTO);
            model.addAttribute("auto", auto);
            model.addAttribute("conductores", conductoresDisponibles);
            model.addAttribute("modo", "editar");
            model.addAttribute("pageTitle", "Editar Auto - TaxiHub");

            return "pages/autos/admin/crear";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar auto: " + e.getMessage());
            return "redirect:/autos";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarAuto(@PathVariable Long id,
                                 @Valid @ModelAttribute("autoFormDTO") AutoFormDTO autoFormDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return recargarVistaConErrores(autoFormDTO, bindingResult, model, "editar", id);
        }

        try {
            Optional<Auto> autoExistenteOpt = autoUseCase.buscarAutoPorId(id);
            if (autoExistenteOpt.isEmpty()) {
                bindingResult.reject("error.general", "Auto no encontrado con ID: " + id);
                return recargarVistaConErrores(autoFormDTO, bindingResult, model, "editar", id);
            }
            Auto autoExistente = autoExistenteOpt.get();


            Auto autoActualizado = mapearFormDTOaAuto(autoFormDTO, autoExistente);

            if (autoFormDTO.empleadoId() != null && !autoFormDTO.empleadoId().trim().isEmpty()) {
                if (!empleadoUseCase.esConductor(autoFormDTO.empleadoId().trim())) {
                    bindingResult.rejectValue("empleadoId", "error.empleadoId", "El empleado no es un conductor. Solo los conductores pueden ser asignados a autos.");
                    return recargarVistaConErrores(autoFormDTO, bindingResult, model, "editar", id);
                }
                
                Optional<Empleado> empleadoOpt = empleadoUseCase.buscarEmpleadoPorNumeroDocumento(autoFormDTO.empleadoId());
                if (empleadoOpt.isEmpty()) {
                    bindingResult.rejectValue("empleadoId", "error.empleadoId", "Empleado no encontrado con DNI: " + autoFormDTO.empleadoId());
                    return recargarVistaConErrores(autoFormDTO, bindingResult, model, "editar", id);
                }
                Empleado empleado = empleadoOpt.get();
                
                if (autoUseCase.empleadoTieneAutoAsignado(autoFormDTO.empleadoId()) &&
                    (autoExistente.getEmpleado() == null || !autoExistente.getEmpleado().getPersona().getNumeroDocumento().equals(autoFormDTO.empleadoId()))) {
                    bindingResult.rejectValue("empleadoId", "error.empleadoId", "El empleado con DNI " + autoFormDTO.empleadoId() + " ya tiene otro auto asignado");
                    return recargarVistaConErrores(autoFormDTO, bindingResult, model, "editar", id);
                }
                
                autoActualizado.setEmpleado(empleado);
            } else {
                autoActualizado.setEmpleado(null);
            }

            autoUseCase.actualizarAuto(id, autoActualizado);
            redirectAttributes.addFlashAttribute("successMessage", "Auto actualizado exitosamente.");
            return "redirect:/autos";

        } catch (Exception e) {
            bindingResult.reject("error.general", "Error al actualizar auto: " + e.getMessage());
            return recargarVistaConErrores(autoFormDTO, bindingResult, model, "editar", id);
        }
    }

    private String recargarVistaConErrores(AutoFormDTO autoFormDTO, BindingResult bindingResult, Model model, String modo) {
        return recargarVistaConErrores(autoFormDTO, bindingResult, model, modo, null);
    }

    private String recargarVistaConErrores(AutoFormDTO autoFormDTO, BindingResult bindingResult, Model model, String modo, Long autoId) {
        try {
            List<Empleado> conductoresDisponibles = empleadoUseCase.listarConductoresDisponibles();
            model.addAttribute("conductores", conductoresDisponibles);
            model.addAttribute("modo", modo);
            model.addAttribute("pageTitle", "crear".equals(modo) ? "Nuevo Auto - TaxiHub" : "Editar Auto - TaxiHub");

            if ("editar".equals(modo) && autoId != null) {
                Auto auto = autoUseCase.buscarAutoPorId(autoId).orElse(null);
                if (auto != null && auto.getImagen() != null) {
                    String imagenBase64 = Base64.getEncoder().encodeToString(auto.getImagen());
                    auto.setImagenBase64(imagenBase64);
                }
                model.addAttribute("auto", auto);
            } else {
                Auto autoEncontrado = null;
                Empleado empleadoEncontrado = null;

                if (autoFormDTO.placa() != null && !autoFormDTO.placa().trim().isEmpty()) {
                    Optional<Auto> autoExterno = autoUseCase.obtenerInformacionVehiculo(autoFormDTO.placa().trim());
                    if (autoExterno.isPresent()) {
                        autoEncontrado = autoExterno.get();
                        if (autoEncontrado.getImagen() != null) {
                            String imagenBase64 = Base64.getEncoder().encodeToString(autoEncontrado.getImagen());
                            autoEncontrado.setImagenBase64(imagenBase64);
                        }
                    }
                }

                if (autoFormDTO.empleadoId() != null && !autoFormDTO.empleadoId().trim().isEmpty()) {
                    empleadoEncontrado = empleadoUseCase.buscarEmpleadoPorNumeroDocumento(autoFormDTO.empleadoId().trim()).orElse(null);
                }

                model.addAttribute("auto", autoEncontrado);
                model.addAttribute("empleadoBuscado", empleadoEncontrado);
                model.addAttribute("placa", autoFormDTO.placa());
                model.addAttribute("numeroDocumento", autoFormDTO.empleadoId());
                model.addAttribute("hayBusqueda", true);
            }

            return "pages/autos/admin/crear";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar la vista: " + e.getMessage());
            return "pages/autos/admin/crear";
        }
    }

    private Auto mapearFormDTOaAuto(AutoFormDTO formDTO, Auto autoExistente) throws Exception {
        Auto auto = autoExistente != null ? autoExistente : new Auto();

        auto.setPlaca(formDTO.placa());
        auto.setMarca(formDTO.marca());
        auto.setModelo(formDTO.modelo());
        auto.setSerie(formDTO.serie());
        auto.setColor(formDTO.color());
        auto.setMotor(formDTO.motor());
        auto.setVin(formDTO.vin());
        auto.setEsPropioEmpresa(formDTO.esPropioEmpresa());
        auto.setActivo(formDTO.activo() != null ? formDTO.activo() : true);

        if (formDTO.imagen() != null && !formDTO.imagen().isEmpty()) {
            auto.setImagen(formDTO.imagen().getBytes());
            auto.setImagenTipo(formDTO.imagen().getContentType());
        }

        return auto;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarAuto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            autoUseCase.eliminarAuto(id);
            redirectAttributes.addFlashAttribute("successMessage", "Auto eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar auto: " + e.getMessage());
        }
        return "redirect:/autos";
    }

    @GetMapping("/asignar/{id}")
    public String mostrarFormularioAsignar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Auto> autoOpt = autoUseCase.buscarAutoPorId(id);
            if (autoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Auto no encontrado con ID: " + id);
                return "redirect:/autos";
            }
            Auto auto = autoOpt.get();

            if (auto.getImagen() != null) {
                String imagenBase64 = Base64.getEncoder().encodeToString(auto.getImagen());
                auto.setImagenBase64(imagenBase64);
            }

            List<Empleado> conductoresDisponibles = empleadoUseCase.listarConductoresDisponibles();

            AsignarConductorDTO asignarConductorDTO = new AsignarConductorDTO(
                auto.getId(),
                ""
            );

            model.addAttribute("auto", auto);
            model.addAttribute("conductoresDisponibles", conductoresDisponibles);
            model.addAttribute("asignarConductorDTO", asignarConductorDTO);
            model.addAttribute("pageTitle", "Asignar Conductor - TaxiHub");

            return "pages/autos/admin/asignar";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar formulario de asignación: " + e.getMessage());
            return "redirect:/autos";
        }
    }

    @PostMapping("/asignar")
    public String asignarConductor(@Valid @ModelAttribute AsignarConductorDTO asignarConductorDTO,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return recargarVistaAsignar(asignarConductorDTO, bindingResult, model);
        }

        try {
            Optional<Auto> autoOpt = autoUseCase.buscarAutoPorId(asignarConductorDTO.autoId());
            if (autoOpt.isEmpty()) {
                bindingResult.reject("error.general", "Auto no encontrado con ID: " + asignarConductorDTO.autoId());
                return recargarVistaAsignar(asignarConductorDTO, bindingResult, model);
            }
            Auto auto = autoOpt.get();

            if (asignarConductorDTO.empleadoId().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("successMessage", "Asignación mantenida sin cambios.");
            } else if ("0".equals(asignarConductorDTO.empleadoId().trim())) {
                auto.setEmpleado(null);
                autoUseCase.actualizarAuto(auto.getId(), auto);
                redirectAttributes.addFlashAttribute("successMessage", "Conductor desasignado exitosamente.");
            } else {
                String empleadoId = asignarConductorDTO.empleadoId().trim();
                
                if (!empleadoUseCase.esConductor(empleadoId)) {
                    bindingResult.rejectValue("empleadoId", "error.empleadoId", "El empleado no es un conductor.");
                    return recargarVistaAsignar(asignarConductorDTO, bindingResult, model);
                }

                Optional<Empleado> empleadoOpt = empleadoUseCase.buscarEmpleadoPorNumeroDocumento(empleadoId);
                if (empleadoOpt.isEmpty()) {
                    bindingResult.rejectValue("empleadoId", "error.empleadoId", "Empleado no encontrado con DNI: " + empleadoId);
                    return recargarVistaAsignar(asignarConductorDTO, bindingResult, model);
                }

                if (autoUseCase.empleadoTieneAutoAsignado(empleadoId) &&
                    (auto.getEmpleado() == null || !auto.getEmpleado().getPersona().getNumeroDocumento().equals(empleadoId))) {
                    bindingResult.rejectValue("empleadoId", "error.empleadoId", "El empleado ya tiene otro auto asignado.");
                    return recargarVistaAsignar(asignarConductorDTO, bindingResult, model);
                }

                auto.setEmpleado(empleadoOpt.get());
                autoUseCase.actualizarAuto(auto.getId(), auto);
                redirectAttributes.addFlashAttribute("successMessage", "Conductor asignado exitosamente.");
            }

            return "redirect:/autos";

        } catch (Exception e) {
            bindingResult.reject("error.general", "Error al procesar asignación: " + e.getMessage());
            return recargarVistaAsignar(asignarConductorDTO, bindingResult, model);
        }
    }

    private String recargarVistaAsignar(AsignarConductorDTO asignarConductorDTO, BindingResult bindingResult, Model model) {
        try {
            Optional<Auto> autoOpt = autoUseCase.buscarAutoPorId(asignarConductorDTO.autoId());
            if (autoOpt.isPresent()) {
                Auto auto = autoOpt.get();
                
                if (auto.getImagen() != null) {
                    String imagenBase64 = Base64.getEncoder().encodeToString(auto.getImagen());
                    auto.setImagenBase64(imagenBase64);
                }

                List<Empleado> conductoresDisponibles = empleadoUseCase.listarConductoresDisponibles();

                model.addAttribute("auto", auto);
                model.addAttribute("conductoresDisponibles", conductoresDisponibles);
                model.addAttribute("pageTitle", "Asignar Conductor - TaxiHub");
            }

            return "pages/autos/admin/asignar";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar la vista: " + e.getMessage());
            return "pages/autos/admin/asignar";
        }
    }
}