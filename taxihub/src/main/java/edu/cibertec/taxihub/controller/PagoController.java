package edu.cibertec.taxihub.controller;

import edu.cibertec.taxihub.constantes.CargoEnum;
import edu.cibertec.taxihub.constantes.EstadoPagoEnum;
import edu.cibertec.taxihub.constantes.ModalidadPagoEnum;
import edu.cibertec.taxihub.dao.entity.Deuda;
import edu.cibertec.taxihub.dao.entity.Empleado;
import edu.cibertec.taxihub.dao.entity.Pago;
import edu.cibertec.taxihub.dto.PagoFormDTO;
import edu.cibertec.taxihub.usecase.IDeudaUseCase;
import edu.cibertec.taxihub.usecase.IEmpleadoUseCase;
import edu.cibertec.taxihub.usecase.IPagoUseCase;
import edu.cibertec.taxihub.usecase.IUsuarioUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pagos")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PagoController {

    private final String CONDUCTOR_PATH= "/conductor";

    private final IPagoUseCase pagoUseCase;
    private final IDeudaUseCase deudaUseCase;
    private final IEmpleadoUseCase empleadoUseCase;
    private final IUsuarioUseCase usuarioUseCase;

    @GetMapping
    public String listarTodosLosPagos(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "id") String sort,
                                     @RequestParam(defaultValue = "desc") String direction,
                                     @RequestParam(required = false) String numeroDocumento,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
                                     @RequestParam(required = false) String modalidad,
                                     @RequestParam(required = false) String estado,
                                     @RequestParam(required = false) Boolean activo,
                                     Model model) {

        Sort.Direction sortDirection = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Pago> pagos = pagoUseCase.listarPagos(numeroDocumento, fecha, modalidad, estado, activo, pageable);

        model.addAttribute("pagos", pagos);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentSize", size);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);
        model.addAttribute("numeroDocumento", numeroDocumento);
        model.addAttribute("fecha", fecha);
        model.addAttribute("modalidad", modalidad);
        model.addAttribute("estado", estado);
        model.addAttribute("activo", activo);
        model.addAttribute("pageTitle", "Todos los Pagos - Administrador - TaxiHub");

        return "pages/pagos/lista";
    }

    @GetMapping("/supervisor/nuevo")
    public String mostrarFormularioPagoSupervisor(@RequestParam Long deudaId,
                                                  Model model,
                                                  RedirectAttributes redirectAttributes) {
        
        Optional<Deuda> deudaOpt = deudaUseCase.buscarDeudaPorId(deudaId);
        if (deudaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Deuda no encontrada.");
            return "redirect:/deudas";
        }
        
        Deuda deuda = deudaOpt.get();
        if (deuda.estaSaldada()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Esta deuda ya está saldada.");
            return "redirect:/deudas";
        }
        
        PagoFormDTO pagoForm = new PagoFormDTO(deudaId, null, "", null);
        model.addAttribute("pagoForm", pagoForm);
        model.addAttribute("deudaSeleccionada", deuda);
        model.addAttribute("pageTitle", "Crear Pago Físico - TaxiHub");
        
        return "pages/pagos/supervisor/crear";
    }
    
    @PostMapping("/supervisor/nuevo")
    public String guardarPagoSupervisor(@Valid @ModelAttribute("pagoForm") PagoFormDTO pagoForm,
                                       BindingResult result,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            Optional<Deuda> deudaOpt = deudaUseCase.buscarDeudaPorId(pagoForm.deudaId());
            if (deudaOpt.isPresent()) {
                model.addAttribute("deudaSeleccionada", deudaOpt.get());
            }
            model.addAttribute("pagoForm", pagoForm);
            model.addAttribute("pageTitle", "Crear Pago Físico - TaxiHub");
            return "pages/pagos/supervisor/crear";
        }

            Optional<Deuda> deudaOpt = deudaUseCase.buscarDeudaPorId(pagoForm.deudaId());
            if (deudaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Deuda no encontrada.");
                return "redirect:/deudas";
            }

            Deuda deuda = deudaOpt.get();

            if (pagoForm.monto().compareTo(deuda.getSaldoPendiente()) > 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "El monto no puede ser mayor al saldo pendiente: S/. " + deuda.getSaldoPendiente());
                return "redirect:/pagos/supervisor/nuevo?deudaId=" + pagoForm.deudaId();
            }

            Optional<Pago> pagoOpt = pagoUseCase.crearPagoFisico(pagoForm.deudaId(), pagoForm.monto(), pagoForm.descripcion());

            if (pagoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "No se pudo crear el pago. Verifique que la deuda existe y el monto sea válido.");
                return "redirect:/pagos/supervisor/nuevo?deudaId=" + pagoForm.deudaId();
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Pago físico de S/. " + pagoForm.monto() + " registrado exitosamente para " +
                            deuda.getEmpleado().getPersona().getNombre() + ".");
            return "redirect:/deudas";
    }

    @PostMapping("/buscar")
    public String buscarConductor(
            @RequestParam 
            @NotBlank(message = "El DNI es obligatorio")
            @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 dígitos")
            String dni, 
            RedirectAttributes redirectAttributes) {
        try {
            Optional<Empleado> empleadoOpt = empleadoUseCase.buscarEmpleadoPorDni(dni);
            
            if (empleadoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No se encontró un empleado con el DNI: " + dni);
                return "redirect:/pagos/nuevo";
            }
            
            Empleado empleado = empleadoOpt.get();
            
            if (!empleado.getCargo().getNombre().equalsIgnoreCase(CargoEnum.CONDUCTOR.getNombre())) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "El empleado con DNI " + dni + " no es un conductor. Cargo actual: " + empleado.getCargo().getNombre());
                return "redirect:/pagos/nuevo";
            }
            
            if (!empleado.isActivo()) {
                redirectAttributes.addFlashAttribute("errorMessage", "El conductor está inactivo.");
                return "redirect:/pagos/nuevo";
            }
            
            return "redirect:/pagos/conductor/" + dni;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al buscar el conductor: " + e.getMessage());
            return "redirect:/pagos/nuevo";
        }
    }

    @GetMapping("/conductor/{dni}")
    public String mostrarDeudasConductor(@PathVariable String dni, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Empleado> empleadoOpt = empleadoUseCase.buscarEmpleadoPorDni(dni);
            
            if (empleadoOpt.isEmpty() || 
                !empleadoOpt.get().getCargo().getNombre().equalsIgnoreCase(CargoEnum.CONDUCTOR.getNombre())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Conductor no encontrado.");
                return "redirect:/pagos/nuevo";
            }
            
            Empleado conductor = empleadoOpt.get();
            
            Page<Deuda> deudasPage = deudaUseCase.listarDeudasPendientesPorEmpleado(dni, PageRequest.of(0, 100));
            List<Deuda> deudas = deudasPage.getContent();
            
            BigDecimal totalDeudaPendiente = deudaUseCase.obtenerTotalDeudaPendientePorEmpleado(dni);
            
            model.addAttribute("conductor", conductor);
            model.addAttribute("deudas", deudas);
            model.addAttribute("totalDeudaPendiente", totalDeudaPendiente);
            model.addAttribute("pageTitle", "Deudas del Conductor - TaxiHub");
            
            return "pages/deudas/conductor/lista";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar las deudas: " + e.getMessage());
            return "redirect:/pagos/nuevo";
        }
    }

    @GetMapping("/crear/{deudaId}")
    public String mostrarFormularioCrearPago(@PathVariable Long deudaId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Deuda> deudaOpt = deudaUseCase.buscarDeudaPorId(deudaId);
            
            if (deudaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Deuda no encontrada.");
                return "redirect:/pagos/nuevo";
            }
            
            Deuda deuda = deudaOpt.get();
            
            if (deuda.getSaldoPendiente().compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Esta deuda ya está completamente saldada.");
                return "redirect:/pagos/conductor/" + deuda.getEmpleado().getPersona().getNumeroDocumento();
            }
            
            Optional<Empleado> conductorOpt = empleadoUseCase.buscarEmpleadoPorDni(deuda.getEmpleado().getPersona().getNumeroDocumento());
            
            if (conductorOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Conductor no encontrado.");
                return "redirect:/pagos/nuevo";
            }
            
            model.addAttribute("deuda", deuda);
            model.addAttribute("conductor", conductorOpt.get());
            model.addAttribute("pageTitle", "Crear Pago - TaxiHub");
            
            return "pages/pagos/supervisor/crear";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/pagos/nuevo";
        }
    }
    @PostMapping("/guardar")
    public String guardarPago(@RequestParam Long deudaId,
                            @RequestParam BigDecimal monto,
                            @RequestParam(required = false) String descripcion,
                            @RequestParam String tipoPago,
                            RedirectAttributes redirectAttributes) {
        try {
            Optional<Deuda> deudaOpt = deudaUseCase.buscarDeudaPorId(deudaId);
            
            if (deudaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Deuda no encontrada.");
                return "redirect:/pagos/nuevo";
            }
            
            Deuda deuda = deudaOpt.get();
            
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "El monto debe ser mayor a 0.");
                return "redirect:/pagos/crear/" + deudaId;
            }
            
            if (monto.compareTo(deuda.getSaldoPendiente()) > 0) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "El monto no puede ser mayor al saldo pendiente: S/. " + deuda.getSaldoPendiente());
                return "redirect:/pagos/crear/" + deudaId;
            }
            
            Optional<Pago> pagoOpt = pagoUseCase.crearPagoFisico(deudaId, monto, descripcion != null ? descripcion : "");
            
            if (pagoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "No se pudo crear el pago. Verifique que la deuda existe y el monto sea válido.");
                return "redirect:/pagos/crear/" + deudaId;
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Pago físico de S/. " + monto + " registrado exitosamente.");
                
            return "redirect:/pagos";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar el pago: " + e.getMessage());
            return "redirect:/pagos/nuevo";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPago(@PathVariable Long id, 
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            Optional<Pago> pagoOpt = pagoUseCase.buscarPagoPorId(id);
            if (pagoOpt.isPresent()) {
                Pago pago = pagoOpt.get();
                log.error("CRITICAL_AUDIT - Usuario: {} | ELIMINANDO PAGO | ID: {} | Monto: {} | Conductor: {}", 
                         authentication.getName(), id, pago.getMontoPago(), pago.getDeuda().getEmpleado().getPersona().getNumeroDocumento());
            }
            
            pagoUseCase.eliminarPago(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pago eliminado exitosamente.");
        } catch (Exception e) {
            log.error("DELETE_ERROR - Usuario: {} | Error eliminando pago ID: {} | Error: {}", 
                     authentication.getName(), id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el pago: " + e.getMessage());
        }
        return "redirect:/pagos";
    }



    @GetMapping(value = CONDUCTOR_PATH)
    public String listarMisPagos(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "id") String sort,
                                 @RequestParam(defaultValue = "desc") String direction,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
                                 Authentication authentication,
                                 Model model) {

        String email = authentication.getName();
        String numeroDocumentoConductor = usuarioUseCase.obtenerNumeroDocumentoEmpleadoPorEmail(email);

        if (numeroDocumentoConductor == null) {
            model.addAttribute("pagos", Page.empty());
            model.addAttribute("pageTitle", "Mis Pagos - TaxiHub");
            return "pages/pagos/conductor/lista";
        }

        if (fecha == null) {
            fecha = LocalDate.now();
        }

        Sort.Direction sortDirection = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Pago> pagos = pagoUseCase.listarPagos(numeroDocumentoConductor, fecha, null, null, null, pageable);

        model.addAttribute("pagos", pagos);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentSize", size);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);
        model.addAttribute("fecha", fecha);
        model.addAttribute("pageTitle", "Mis Pagos - TaxiHub");

        return "pages/pagos/conductor/lista";
    }

    @GetMapping(CONDUCTOR_PATH+"/nuevo")
    public String mostrarFormularioNuevoPago(@RequestParam(required = false) Long deudaId,
                                             Authentication authentication,
                                             Model model,
                                             RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        String numeroDocumentoConductor = usuarioUseCase.obtenerNumeroDocumentoEmpleadoPorEmail(email);

        if (numeroDocumentoConductor == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes acceder a esta función porque no tienes un empleado asociado.");
            return "redirect:/dashboard";
        }

        PagoFormDTO pagoForm = new PagoFormDTO(deudaId, null, "", null);

        if (deudaId != null) {
            Optional<Deuda> deudaOpt = deudaUseCase.buscarDeudaPorId(deudaId);
            if (deudaOpt.isPresent()) {
                Deuda deuda = deudaOpt.get();
                if (!deuda.getEmpleado().getPersona().getNumeroDocumento().equals(numeroDocumentoConductor)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "No puedes pagar una deuda que no es tuya.");
                    return "redirect:/deudas/conductor";
                }
                model.addAttribute("deudaSeleccionada", deuda);
            }
        }

        Page<Deuda> deudasPendientes = deudaUseCase.listarDeudasPendientesPorEmpleado(
                numeroDocumentoConductor, PageRequest.of(0, 100)
        );

        model.addAttribute("pagoForm", pagoForm);
        model.addAttribute("deudasPendientes", deudasPendientes.getContent());
        model.addAttribute("pageTitle", "Nuevo Pago - TaxiHub");

        return "pages/pagos/conductor/crear";
    }

    @PostMapping(CONDUCTOR_PATH+"/guardar")
    public String guardarPago(@Valid @ModelAttribute("pagoForm") PagoFormDTO pagoForm,
                              BindingResult result,
                              Authentication authentication,

                              Model model,
                              RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        String numeroDocumentoConductor = usuarioUseCase.obtenerNumeroDocumentoEmpleadoPorEmail(email);

        if (numeroDocumentoConductor == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes acceder a esta función porque no tienes un empleado asociado.");
            return "redirect:/dashboard";
        }

        if (pagoForm.voucher() == null || pagoForm.voucher().isEmpty()) {
            result.rejectValue("voucher", "error.voucher", "La imagen del voucher es obligatoria");
        }

        if (result.hasErrors()) {
            Page<Deuda> deudasPendientes = deudaUseCase.listarDeudasPendientesPorEmpleado(
                    numeroDocumentoConductor, PageRequest.of(0, 100)
            );

            if (pagoForm.deudaId() != null) {
                Optional<Deuda> deudaOpt = deudaUseCase.buscarDeudaPorId(pagoForm.deudaId());
                if (deudaOpt.isPresent()) {
                    model.addAttribute("deudaSeleccionada", deudaOpt.get());
                }
            }

            model.addAttribute("deudasPendientes", deudasPendientes.getContent());
            model.addAttribute("pagoForm", pagoForm);
            model.addAttribute("pageTitle", "Nuevo Pago - TaxiHub");
            return "pages/pagos/conductor/crear";
        }

            Optional<Deuda> deudaOpt = deudaUseCase.buscarDeudaPorId(pagoForm.deudaId());
            if (deudaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Deuda no encontrada.");
                return "redirect:/pagos/conductor/nuevo";
            }

            Deuda deuda = deudaOpt.get();
            if (!deuda.getEmpleado().getPersona().getNumeroDocumento().equals(numeroDocumentoConductor)) {
                redirectAttributes.addFlashAttribute("errorMessage", "No puedes pagar una deuda que no es tuya.");
                return "redirect:/deudas/conductor";
            }

            if (pagoForm.monto().compareTo(deuda.getSaldoPendiente()) > 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "El monto no puede ser mayor al saldo pendiente: S/. " + deuda.getSaldoPendiente());
                return "redirect:/pagos/conductor/nuevo?deudaId=" + pagoForm.deudaId();
            }

            byte[] imagenBytes = null;
            String imagenTipo = null;
            if (pagoForm.voucher() != null && !pagoForm.voucher().isEmpty()) {
                try {
                    imagenBytes = pagoForm.voucher().getBytes();
                    imagenTipo = pagoForm.voucher().getContentType();
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar la imagen del voucher.");
                    return "redirect:/pagos/conductor/nuevo?deudaId=" + pagoForm.deudaId();
                }
            }
            Optional<Pago> pagoOpt = pagoUseCase.crearPago(
                pagoForm.deudaId(), 
                ModalidadPagoEnum.BILLETERA_VIRTUAL, 
                pagoForm.monto(), 
                pagoForm.descripcion(),
                imagenBytes,
                imagenTipo
            );

            if (pagoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "No se pudo crear el pago. Verifique que la deuda existe, el monto sea válido y haya subido la imagen del voucher.");
                return "redirect:/pagos/conductor/nuevo?deudaId=" + pagoForm.deudaId();
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Pago de S/. " + pagoForm.monto() + " registrado exitosamente.");
            return "redirect:/pagos/conductor";
    }

    @GetMapping("/ver/{id}")
    public String verDetallePago(@PathVariable Long id, 
                                Authentication authentication,
                                Model model, 
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<Pago> pagoOpt = pagoUseCase.buscarPagoPorId(id);
            
            if (pagoOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Pago no encontrado.");
                return "error/404";
            }
            
            Pago pago = pagoOpt.get();
            
            boolean puedeVerTodos = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("VER_PAGOS_TODOS"));
            
            boolean puedeVerPropios = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("VER_PAGOS_PROPIOS"));
            
            if (!puedeVerTodos && !puedeVerPropios) {
                model.addAttribute("errorMessage", "No tienes permisos para ver pagos.");
                return "error/403";
            }
            
            if (!puedeVerTodos && puedeVerPropios) {
                String email = authentication.getName();
                String numeroDocumentoConductor = usuarioUseCase.obtenerNumeroDocumentoEmpleadoPorEmail(email);
                
                if (numeroDocumentoConductor != null) {
                    boolean esMiPago = pagoUseCase.verificarPropietarioPago(id, numeroDocumentoConductor);
                    if (!esMiPago) {
                        model.addAttribute("errorMessage", "No puedes ver un pago que no es tuyo.");
                        return "error/403";
                    }
                }
            }
            
            Optional<Deuda> deudaOpt = deudaUseCase.buscarDeudaPorId(pago.getDeudaId());
            if (deudaOpt.isPresent()) {
                model.addAttribute("deuda", deudaOpt.get());
            }
            
            model.addAttribute("pago", pago);
            model.addAttribute("pageTitle", "Detalle del Pago #" + pago.getId() + " - TaxiHub");
            
            return "pages/pagos/ver";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el detalle del pago: " + e.getMessage());
            return "redirect:/pagos";
        }
    }

    @GetMapping("/gestionar/{id}")
    public String mostrarGestionPago(@PathVariable Long id, 
                                    Authentication authentication,
                                    Model model) {
        boolean puedeGestionar = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("GESTIONAR_PAGOS"));
        
        if (!puedeGestionar) {
            model.addAttribute("errorMessage", "No tienes permisos para gestionar pagos.");
            return "error/403";
        }
        
        try {
            Optional<Pago> pagoOpt = pagoUseCase.buscarPagoPorId(id);
            
            if (pagoOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Pago no encontrado.");
                return "error/404";
            }
            
            Pago pago = pagoOpt.get();
            
            Optional<Deuda> deudaOpt = deudaUseCase.buscarDeudaPorId(pago.getDeudaId());
            if (deudaOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Deuda asociada no encontrada.");
                return "error/404";
            }
            
            Deuda deuda = deudaOpt.get();
            
            BigDecimal saldoPendiente = deuda.getSaldoPendiente();
            BigDecimal montoPago = pago.getMontoPago();
            BigDecimal sobrepago = montoPago.subtract(saldoPendiente);
            boolean haySobrepago = sobrepago.compareTo(BigDecimal.ZERO) > 0;
            
            model.addAttribute("pago", pago);
            model.addAttribute("deuda", deuda);
            model.addAttribute("sobrepago", haySobrepago ? sobrepago : BigDecimal.ZERO);
            model.addAttribute("haySobrepago", haySobrepago);
            model.addAttribute("pageTitle", "Gestionar Pago #" + pago.getId() + " - TaxiHub");
            
            return "pages/pagos/gestionar";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar el pago: " + e.getMessage());
            return "error/500";
        }
    }

    @PostMapping("/gestionar/{id}/cambiar-estado")
    public String cambiarEstadoPago(@PathVariable Long id,
                                   @RequestParam String nuevoEstado,
                                   @RequestParam(required = false) String observacion,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        
        boolean puedeGestionar = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("GESTIONAR_PAGOS"));
        
        if (!puedeGestionar) {
            redirectAttributes.addFlashAttribute("errorMessage", "No tienes permisos para gestionar pagos.");
            return "redirect:/pagos";
        }
        
        try {
            Optional<Pago> pagoOpt = pagoUseCase.buscarPagoPorId(id);
            if (pagoOpt.isPresent()) {
                Pago pago = pagoOpt.get();
                log.warn("CRITICAL_AUDIT - Usuario: {} | CAMBIO ESTADO PAGO | ID: {} | Estado anterior: {} | Estado nuevo: {} | Monto: {} | Observación: {}", 
                        authentication.getName(), id, pago.getEstado(), nuevoEstado, pago.getMontoPago(), observacion != null ? observacion : "Sin observación");
            }
            
            EstadoPagoEnum estadoEnum;
            switch (nuevoEstado) {
                case "POR REVISAR" -> estadoEnum = EstadoPagoEnum.POR_REVISAR;
                case "EN REVISION" -> estadoEnum = EstadoPagoEnum.EN_REVISION;
                case "APROBADO" -> estadoEnum = EstadoPagoEnum.APROBADO;
                case "RECHAZADO" -> estadoEnum = EstadoPagoEnum.RECHAZADO;
                default -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Estado inválido seleccionado.");
                    return "redirect:/pagos/gestionar/" + id;
                }
            }
            
            Pago pago = pagoUseCase.cambiarEstadoPago(id, estadoEnum, observacion);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Estado del pago #" + pago.getId() + " cambiado a: " + nuevoEstado);
            return "redirect:/pagos";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al cambiar el estado del pago: " + e.getMessage());
            return "redirect:/pagos/gestionar/" + id;
        }
    }

}