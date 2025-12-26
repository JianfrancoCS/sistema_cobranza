package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IEmpleadoUseCase {
    List<Empleado> listarEmpleados();
    Page<Empleado> listarEmpleadosConFiltros(String dni, String nombre, Long cargoId, Boolean activo, Pageable pageable);
    Optional<Empleado> buscarEmpleadoPorNumeroDocumento(String numeroDocumento);
    Optional<Empleado> buscarEmpleadoPorDni(String dni);
    List<Empleado> listarConductoresDisponibles();
    boolean esConductor(String numeroDocumento);
    Empleado crearEmpleado(Empleado empleado);
    Empleado actualizarEmpleado(String numeroDocumento, Empleado empleado);
    void eliminarEmpleado(String numeroDocumento);
}
