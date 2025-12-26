package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.dao.entity.Empleado;
import edu.cibertec.taxihub.dao.specification.EmpleadoPorCargoSpec;
import edu.cibertec.taxihub.dao.specification.EmpleadoPorDniSpec;
import edu.cibertec.taxihub.dao.specification.EmpleadoPorActivoSpec;
import edu.cibertec.taxihub.dao.specification.EmpleadoPorNombreSpec;
import edu.cibertec.taxihub.usecase.IEmpleadoUseCase;
import edu.cibertec.taxihub.dao.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpleadoUseCaseImpl implements IEmpleadoUseCase {

    private final EmpleadoRepository empleadoRepository;

    @Override
    public List<Empleado> listarEmpleados() {
        return empleadoRepository.findAll(EmpleadoPorActivoSpec.activos());
    }

    @Override
    public Page<Empleado> listarEmpleadosConFiltros(String dni, String nombre, Long cargoId, Boolean activo, Pageable pageable) {
        Specification<Empleado> spec = Specification
                .where(EmpleadoPorDniSpec.conDniContiene(dni))
                .and(EmpleadoPorNombreSpec.conNombreContiene(nombre))
                .and(EmpleadoPorCargoSpec.conCargoId(cargoId))
                .and(EmpleadoPorActivoSpec.conActivo(activo));

        return empleadoRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<Empleado> buscarEmpleadoPorNumeroDocumento(String numeroDocumento) {
        Specification<Empleado> spec = Specification.where(EmpleadoPorDniSpec.conDni(numeroDocumento))
                .and(EmpleadoPorActivoSpec.activos());
        return empleadoRepository.findOne(spec);
    }

    @Override
    public Optional<Empleado> buscarEmpleadoPorDni(String dni) {
        Specification<Empleado> spec = Specification.where(EmpleadoPorDniSpec.conDni(dni))
                .and(EmpleadoPorActivoSpec.activos());
        return empleadoRepository.findOne(spec);
    }

    @Override
    public Empleado crearEmpleado(Empleado empleado) {

        return empleadoRepository.save(empleado);
    }

    @Override
    public Empleado actualizarEmpleado(String numeroDocumento, Empleado empleado) {
        return empleadoRepository.findByPersonaNumeroDocumento(numeroDocumento)
                .map(existingEmpleado -> {
                    existingEmpleado.setCargo(empleado.getCargo());
                    existingEmpleado.setFechaInicio(empleado.getFechaInicio());
                    existingEmpleado.setFechaFin(empleado.getFechaFin());
                    return empleadoRepository.save(existingEmpleado);
                })
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con número de documento: " + numeroDocumento));
    }

    @Override
    public List<Empleado> listarConductoresDisponibles() {
        return empleadoRepository.findConductoresDisponibles();
    }

    @Override
    public boolean esConductor(String numeroDocumento) {
        return empleadoRepository.esConductor(numeroDocumento);
    }

    @Override
    public void eliminarEmpleado(String numeroDocumento) {
        Optional<Empleado> empleadoOpt = empleadoRepository.findByPersonaNumeroDocumento(numeroDocumento);
        if (empleadoOpt.isPresent()) {
            Empleado empleado = empleadoOpt.get();
            empleado.setActivo(false);
            empleadoRepository.save(empleado);
        }
    }
}
