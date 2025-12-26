package edu.cibertec.taxihub_rest.services;

import edu.cibertec.taxihub_rest.constantes.CargoEnum;
import edu.cibertec.taxihub_rest.dao.entity.Empleado;
import edu.cibertec.taxihub_rest.dao.entity.Marcacion;
import edu.cibertec.taxihub_rest.dao.repository.IAutoRepository;
import edu.cibertec.taxihub_rest.dao.repository.IEmpleadoRepository;
import edu.cibertec.taxihub_rest.dao.repository.IMarcacionRepository;
import edu.cibertec.taxihub_rest.dto.MarcacionRespuesta;
import edu.cibertec.taxihub_rest.exception.MarcacionException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarcacionServiceImpl implements IMarcacionService {
    private final IMarcacionRepository marcacionRepository;
    private final IEmpleadoRepository empleadoRepository;
    private final IAutoRepository autoRepository;
    private final MessageSource messageSource;

    @Override
    public MarcacionRespuesta marcarIngreso(String numeroDocumento) {
        Empleado conductor = buscarConductor(numeroDocumento);
        
        if (marcacionRepository.tieneIngresoSinSalida(numeroDocumento)) {
            throw new MarcacionException("error.ingreso.duplicado");
        }
        
        Marcacion ingreso = new Marcacion();
        ingreso.setEmpleado(conductor);
        ingreso.setTipoMarcacion(true);
        ingreso.setFechaCreacion(LocalDateTime.now());
        
        try {
            marcacionRepository.save(ingreso);
        } catch (Exception e) {
            throw new MarcacionException("error.empleado.no.registrado");
        }

        Long totalSalidas = marcacionRepository.contarSalidas(numeroDocumento);
        return new MarcacionRespuesta(numeroDocumento, totalSalidas);
    }

    @Override
    public MarcacionRespuesta marcarSalida(String numeroDocumento) {
        Empleado conductor = buscarConductor(numeroDocumento);
        
        Optional<Marcacion> ultimoIngreso = marcacionRepository.buscarUltimoIngresoPendiente(numeroDocumento);

        if (ultimoIngreso.isEmpty()) {
            throw new MarcacionException("error.salida.sin.ingreso");
        }

        Marcacion salida = new Marcacion();
        salida.setEmpleado(conductor);
        salida.setTipoMarcacion(false);
        salida.setFechaCreacion(LocalDateTime.now());

        try {
            marcacionRepository.save(salida);
        } catch (Exception e) {
            throw new MarcacionException("error.empleado.no.registrado");
        }

        Long totalSalidas = marcacionRepository.contarSalidas(numeroDocumento);
        return new MarcacionRespuesta(numeroDocumento, totalSalidas);
    }
    
    private Empleado buscarConductor(String numeroDocumento) {
        Empleado conductor = empleadoRepository.findByNumeroDocumentoAndCargo(numeroDocumento, CargoEnum.CONDUCTOR.getNombre())
                .orElseThrow(() -> new MarcacionException("error.conductor.no.encontrado"));
        
        if (!autoRepository.existsByEmpleadoId(conductor.getId())) {
            throw new MarcacionException("error.conductor.sin.auto");
        }
        
        return conductor;
    }
}
