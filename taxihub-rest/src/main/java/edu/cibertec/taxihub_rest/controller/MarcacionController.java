package edu.cibertec.taxihub_rest.controller;

import edu.cibertec.taxihub_rest.dto.MarcacionRespuesta;
import edu.cibertec.taxihub_rest.services.IMarcacionService;
import edu.cibertec.taxihub_rest.utils.ResultadoAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = MarcacionController.BASE_PATH)
@RequiredArgsConstructor
@Slf4j
public class MarcacionController {
    public static final String BASE_PATH = "/marcacion";

    private final IMarcacionService marcacionService;

    @PostMapping(value = "/{numero_documento}")
    ResponseEntity<ResultadoAPI<MarcacionRespuesta>> marcacion(@PathVariable(name = "numero_documento") String numero_documento,
                                     @RequestParam(name = "tipo_marcacion") boolean tipoMarcacion) {
        log.info("Petición recibida: POST /marcacion/{} - Tipo: {}", 
                numero_documento, tipoMarcacion ? "INGRESO" : "SALIDA");
        
        MarcacionRespuesta respuesta;
        String mensaje;

        if (tipoMarcacion) {
            respuesta = marcacionService.marcarIngreso(numero_documento);
            mensaje = "Ingreso registrado correctamente";
        } else {
            respuesta = marcacionService.marcarSalida(numero_documento);
            mensaje = "Salida registrada correctamente";
        }

        return ResponseEntity.ok(ResultadoAPI.exito(respuesta, mensaje));
    }
}
