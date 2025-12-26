package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Auto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IAutoUseCase {
    Page<Auto> listarAutosConFiltros(String placa, String marca, Boolean activo, Boolean disponibles, Pageable pageable);
    Auto crearAuto(Auto auto);
    Auto actualizarAuto(Long id, Auto auto);
    void eliminarAuto(Long id);
    Optional<Auto> buscarAutoPorId(Long id);
    Optional<Auto> buscarAutoPorPlaca(String placa);
    Optional<Auto> obtenerInformacionVehiculo(String placa);
    boolean empleadoTieneAutoAsignado(String numeroDocumento);
}
