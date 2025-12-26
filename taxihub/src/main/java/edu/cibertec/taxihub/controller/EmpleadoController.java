package edu.cibertec.taxihub.controller;

import edu.cibertec.taxihub.dto.EmpleadoFormDTO;
import edu.cibertec.taxihub.dao.entity.Cargo;
import edu.cibertec.taxihub.dao.entity.Empleado;
import edu.cibertec.taxihub.dao.entity.Licencia;
import edu.cibertec.taxihub.dao.entity.Persona;
import edu.cibertec.taxihub.exception.GlobalException;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import edu.cibertec.taxihub.usecase.ICargoUseCase;
import edu.cibertec.taxihub.usecase.IEmpleadoUseCase;
import edu.cibertec.taxihub.usecase.ILicenciaUseCase;
import edu.cibertec.taxihub.usecase.IPersonaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final IEmpleadoUseCase empleadoUseCase;
    private final IPersonaUseCase personaUseCase;
    private final ILicenciaUseCase licenciaUseCase;
    private final ICargoUseCase cargoUseCase;

    @GetMapping
    public String listarEmpleados(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String buscarDni,
                                 @RequestParam(required = false) Long cargoId,
                                 @RequestParam(required = false) String nombre,
                                 Model model) {
        try {
            if (size != 10 && size != 25 && size != 50) {
                size = 10;
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Empleado> empleadosPage = empleadoUseCase.listarEmpleadosConFiltros(buscarDni, nombre, cargoId, true, pageable);

            model.addAttribute("empleados", empleadosPage.getContent());
            model.addAttribute("totalPages", empleadosPage.getTotalPages());
            model.addAttribute("totalElements", empleadosPage.getTotalElements());
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("buscarDni", buscarDni);
            model.addAttribute("cargoId", cargoId);
            model.addAttribute("nombre", nombre);
            model.addAttribute("cargos", cargoUseCase.listarCargos());

            Long cargoIdConductor = cargoUseCase.listarCargos().stream()
                    .filter(c -> c.getNombre().toLowerCase().contains("conductor"))
                    .map(c -> c.getId())
                    .findFirst()
                    .orElse(null);
            model.addAttribute("cargoIdConductor", cargoIdConductor);

            model.addAttribute("pageTitle", "Gestión de Empleados - TaxiHub");

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar empleados: " + e.getMessage());
        }
        return "pages/empleados/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(
            @RequestParam(value = "dni", required = false) String dni,
            @RequestParam(value = "buscarLicencia", required = false, defaultValue = "false") Boolean buscarLicencia,
            Model model) {

        Persona persona = null;
        Licencia licencia = null;
        boolean licenciaEncontrada = false;
        model.addAttribute("modo", "crear");
        model.addAttribute("pageTitle", "Nuevo Empleado - TaxiHub");

        if (dni != null && !dni.trim().isEmpty()) {
            Optional<Empleado> empleadoExistente = empleadoUseCase.buscarEmpleadoPorNumeroDocumento(dni.trim());
            if (empleadoExistente.isPresent()) {
                model.addAttribute("errorMessage", "Ya existe un empleado activo con DNI: " + dni.trim() + 
                    ". No se puede crear otro empleado con el mismo documento.");
                return "pages/empleados/crear";
            }
            
            Optional<Persona> personaOpt = personaUseCase.obtenerYCachearPersonaPorDni(dni.trim());
            if (personaOpt.isPresent()) {
                persona = personaOpt.get();
                model.addAttribute("successMessage", "Persona encontrada con el dni "+dni);
                if (persona.getFechaNac() != null) {
                    int edad = Period.between(persona.getFechaNac(), LocalDate.now()).getYears();
                    model.addAttribute("personaEdad", edad + " años");
                } else {
                    model.addAttribute("personaEdad", "N/A");
                }

            } else {
                model.addAttribute("errorMessage", "No se encontró una persona con el DNI proporcionado.");
            }
        }
        if (buscarLicencia && dni != null && !dni.trim().isEmpty() && persona != null) {
            try {
                Optional<Licencia> optLicencia = licenciaUseCase.buscarLincenciaPorDni(dni);
                if (optLicencia.isPresent()) {
                    licencia = optLicencia.get();
                    licenciaEncontrada = true;
                    model.addAttribute("licencia", licencia);
                }
            } catch (Exception e) {
                model.addAttribute("warningMessage", "No se pudo verificar la licencia: " + e.getMessage());
            }
        }

        List<Cargo> cargosDisponibles;
        if (persona != null) {
            if (!buscarLicencia) {
                cargosDisponibles = cargoUseCase.listarCargos().stream()
                    .filter(cargo -> !cargo.getNombre().toLowerCase().contains("conductor"))
                    .collect(java.util.stream.Collectors.toList());
                model.addAttribute("sinLicenciaMessage", "Para crear un empleado conductor, debe marcar la opción 'Buscar licencia de conducir' y validar que tenga licencia vigente.");
            } else if (!licenciaEncontrada) {
                cargosDisponibles = cargoUseCase.listarCargos().stream()
                    .filter(cargo -> !cargo.getNombre().toLowerCase().contains("conductor"))
                    .collect(java.util.stream.Collectors.toList());
                model.addAttribute("sinLicenciaMessage", "No se encontró licencia de conducir para este DNI. Esta persona no puede ser registrada como conductor.");
            } else {
                cargosDisponibles = cargoUseCase.listarCargos();
            }
        } else {
            cargosDisponibles = cargoUseCase.listarCargos();
        }

        model.addAttribute("cargos", cargosDisponibles);
        model.addAttribute("persona", persona);
        
        if (!model.containsAttribute("empleadoData")) {
            EmpleadoFormDTO empleadoForm = new EmpleadoFormDTO(
                dni != null ? dni.trim() : null,
                null,
                null,
                null,
                true
            );
            model.addAttribute("empleadoData", empleadoForm);
        }
        
        return "pages/empleados/crear";
    }

    @PostMapping({"/guardar", "/actualizar/{numeroDocumento}"})
    public String guardarEmpleado(@PathVariable(required = false) String numeroDocumento,
                                 @Valid @ModelAttribute("empleadoData") EmpleadoFormDTO empleadoData, 
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        
        boolean esEdicion = numeroDocumento != null;
        
        if (!esEdicion && empleadoData.numeroDocumento() != null) {
            Optional<Persona> personaOpt = personaUseCase.obtenerYCachearPersonaPorDni(empleadoData.numeroDocumento());
            if (personaOpt.isEmpty()) {
                result.rejectValue("numeroDocumento", "error.persona.notfound", 
                    "No se encontró una persona con DNI: " + empleadoData.numeroDocumento());
            }
        }

        if (empleadoData.cargoId() != null) {
            Optional<Cargo> cargoOpt = cargoUseCase.buscarCargoPorId(empleadoData.cargoId());
            if (cargoOpt.isEmpty()) {
                result.rejectValue("cargoId", "error.cargo.notfound", 
                    "Cargo no encontrado con ID: " + empleadoData.cargoId());
            } else {
                Cargo cargo = cargoOpt.get();
                if (cargo.getNombre().toLowerCase().contains("conductor")) {
                    String dni = esEdicion ? numeroDocumento : empleadoData.numeroDocumento();
                    try {
                        Optional<Licencia> licenciaOpt = licenciaUseCase.buscarLincenciaPorDni(dni);
                        if (licenciaOpt.isEmpty()) {
                            result.rejectValue("cargoId", "error.conductor.sinlicencia",
                                "No se encontró licencia de conducir. No puede ser asignado como conductor.");
                        } else if (!licenciaOpt.get().isLicenciaVigente()) {
                            result.rejectValue("cargoId", "error.conductor.licenciavencida",
                                "La licencia de conducir está vencida. No se puede asignar como conductor.");
                        }
                    } catch (Exception e) {
                        result.rejectValue("cargoId", "error.licencia.verificacion",
                            "Error al verificar la licencia: " + e.getMessage());
                    }
                }
            }
        }

        if (result.hasErrors()) {
            String dni = esEdicion ? numeroDocumento : empleadoData.numeroDocumento();
            cargarDatosFormulario(model, dni);
            model.addAttribute("modo", esEdicion ? "editar" : "crear");
            model.addAttribute("pageTitle", esEdicion ? "Editar Empleado - TaxiHub" : "Nuevo Empleado - TaxiHub");
            
            if (esEdicion) {
                Optional<Empleado> empleadoOpt = empleadoUseCase.buscarEmpleadoPorNumeroDocumento(numeroDocumento);
                if (empleadoOpt.isPresent()) {
                    Empleado empleado = empleadoOpt.get();
                    model.addAttribute("empleado", empleado);
                    model.addAttribute("persona", empleado.getPersona());
                    model.addAttribute("cargo", empleado.getCargo());
                    
                    if (empleado.getPersona().getFechaNac() != null) {
                        int edad = Period.between(empleado.getPersona().getFechaNac(), LocalDate.now()).getYears();
                        model.addAttribute("personaEdad", edad + " años");
                    } else {
                        model.addAttribute("personaEdad", "N/A");
                    }
                }
            }
            
            return "pages/empleados/crear";
        }

        try {
            Optional<Persona> personaOpt = personaUseCase.obtenerYCachearPersonaPorDni(empleadoData.numeroDocumento());
            Optional<Cargo> cargoOpt = cargoUseCase.buscarCargoPorId(empleadoData.cargoId());

            Persona persona = personaOpt.get();
            Cargo cargo = cargoOpt.get();

            Empleado empleado = new Empleado();
            empleado.setPersona(persona);
            empleado.setCargo(cargo);
            empleado.setFechaInicio(empleadoData.fechaInicio());
            empleado.setFechaFin(empleadoData.fechaFin());
            empleado.setActivo(empleadoData.activo() != null ? empleadoData.activo() : true);

            if (esEdicion) {
                empleadoUseCase.actualizarEmpleado(numeroDocumento, empleado);
            } else {
                empleadoUseCase.crearEmpleado(empleado);
            }

            if (cargo.getNombre().equalsIgnoreCase("CONDUCTOR")) {
                Optional<Licencia> licenciaOpt = licenciaUseCase.buscarLincenciaPorDni(empleadoData.numeroDocumento());

                if (!licenciaOpt.isPresent()) {
                    result.rejectValue("cargoId", "error.conductor.sinlicencia",
                        "No se encontró licencia de conducir para el DNI proporcionado. Debe estar registrada en el sistema externo.");
                    cargarDatosFormulario(model, empleadoData.numeroDocumento());
                    model.addAttribute("modo", "crear");
                    model.addAttribute("pageTitle", "Nuevo Empleado - TaxiHub");
                    return "pages/empleados/crear";
                }

                Licencia licencia = licenciaOpt.get();
                if (!licencia.isLicenciaVigente()) {
                    result.rejectValue("cargoId", "error.conductor.licenciavencida",
                        "La licencia de conducir está vencida. No se puede crear el empleado conductor.");
                    cargarDatosFormulario(model, empleadoData.numeroDocumento());
                    model.addAttribute("modo", "crear");
                    model.addAttribute("pageTitle", "Nuevo Empleado - TaxiHub");
                    return "pages/empleados/crear";
                }
            }

            redirectAttributes.addFlashAttribute("successMessage", 
                esEdicion ? "Empleado actualizado exitosamente." : "Empleado creado exitosamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                (esEdicion ? "Error al actualizar empleado: " : "Error al crear empleado: ") + e.getMessage());
        }
        return "redirect:/empleados";
    }
    @GetMapping("/editar/{numeroDocumento}")
    public String mostrarFormularioEditar(@PathVariable String numeroDocumento, Model model, RedirectAttributes redirectAttributes) {
        Optional<Empleado> empleadoOpt = empleadoUseCase.buscarEmpleadoPorNumeroDocumento(numeroDocumento);
        if (empleadoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Empleado no encontrado con número de documento: " + numeroDocumento);
            return "redirect:/empleados";
        }
        Empleado empleado = empleadoOpt.get();
        model.addAttribute("empleado", empleado);
        model.addAttribute("persona", empleado.getPersona());
        model.addAttribute("cargo", empleado.getCargo());
        model.addAttribute("modo", "editar");
        
        Licencia licencia = null;
        boolean licenciaEncontrada = false;
        try {
            Optional<Licencia> optLicencia = licenciaUseCase.buscarLincenciaPorDni(numeroDocumento);
            if (optLicencia.isPresent()) {
                licencia = optLicencia.get();
                licenciaEncontrada = true;
                model.addAttribute("licencia", licencia);
            }
        } catch (Exception e) {
            model.addAttribute("warningMessage", "No se pudo verificar la licencia: " + e.getMessage());
        }

        List<Cargo> cargosDisponibles;
        if (!licenciaEncontrada) {
            cargosDisponibles = cargoUseCase.listarCargos().stream()
                .filter(cargo -> !cargo.getNombre().toLowerCase().contains("conductor"))
                .collect(java.util.stream.Collectors.toList());
            model.addAttribute("sinLicenciaMessage", "No se encontró licencia de conducir para este empleado. No puede ser asignado como conductor.");
        } else {
            cargosDisponibles = cargoUseCase.listarCargos();
        }
        
        model.addAttribute("cargos", cargosDisponibles);
        model.addAttribute("pageTitle", "Editar Empleado - TaxiHub");

        if (empleado.getPersona().getFechaNac() != null) {
            int edad = Period.between(empleado.getPersona().getFechaNac(), LocalDate.now()).getYears();
            model.addAttribute("personaEdad", edad + " años");
        } else {
            model.addAttribute("personaEdad", "N/A");
        }
        
        if (!model.containsAttribute("empleadoData")) {
            EmpleadoFormDTO empleadoForm = new EmpleadoFormDTO(
                empleado.getPersona().getNumeroDocumento(),
                empleado.getCargo().getId(),
                empleado.getFechaInicio(),
                empleado.getFechaFin(),
                empleado.isActivo()
            );
            model.addAttribute("empleadoData", empleadoForm);
        }

        return "pages/empleados/crear";
    }


    @PostMapping("/eliminar/{numeroDocumento}")
    public String eliminarEmpleado(@PathVariable(name="numeroDocumento",required = true) String numeroDocumento, RedirectAttributes redirectAttributes) {
        try {
            empleadoUseCase.eliminarEmpleado(numeroDocumento);
            redirectAttributes.addFlashAttribute("successMessage", "Empleado eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar empleado: " + e.getMessage());
        }
        return "redirect:/empleados";
    }

    private void cargarDatosFormulario(Model model, String dni) {
        Persona persona = null;
        Licencia licencia = null;
        boolean licenciaEncontrada = false;
        
        if (dni != null && !dni.trim().isEmpty()) {
            Optional<Persona> personaOpt = personaUseCase.obtenerYCachearPersonaPorDni(dni.trim());
            if (personaOpt.isPresent()) {
                persona = personaOpt.get();
                model.addAttribute("persona", persona);
                
                if (persona.getFechaNac() != null) {
                    int edad = Period.between(persona.getFechaNac(), LocalDate.now()).getYears();
                    model.addAttribute("personaEdad", edad + " años");
                } else {
                    model.addAttribute("personaEdad", "N/A");
                }
                
                try {
                    Optional<Licencia> optLicencia = licenciaUseCase.buscarLincenciaPorDni(dni);
                    if (optLicencia.isPresent()) {
                        licencia = optLicencia.get();
                        licenciaEncontrada = true;
                        model.addAttribute("licencia", licencia);
                    }
                } catch (Exception e) {
                    model.addAttribute("warningMessage", "No se pudo verificar la licencia: " + e.getMessage());
                }
            }
        }
        
        List<Cargo> cargosDisponibles;
        if (persona != null) {
            if (!licenciaEncontrada) {
                cargosDisponibles = cargoUseCase.listarCargos().stream()
                    .filter(cargo -> !cargo.getNombre().toLowerCase().contains("conductor"))
                    .collect(java.util.stream.Collectors.toList());
                model.addAttribute("sinLicenciaMessage", "No se encontró licencia de conducir para este DNI. Esta persona no puede ser registrada como conductor.");
            } else {
                cargosDisponibles = cargoUseCase.listarCargos();
            }
        } else {
            cargosDisponibles = cargoUseCase.listarCargos();
        }
        
        model.addAttribute("cargos", cargosDisponibles);
    }
}
