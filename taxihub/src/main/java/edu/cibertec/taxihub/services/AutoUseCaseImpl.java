package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.dao.specification.AutoDisponibleSpecification;
import edu.cibertec.taxihub.dao.specification.AutoPorEstadoSpecification;
import edu.cibertec.taxihub.dao.specification.AutoPorMarcaSpecification;
import edu.cibertec.taxihub.dao.specification.AutoPorPlacaSpecification;
import edu.cibertec.taxihub.usecase.IPlacaApiService;
import edu.cibertec.taxihub.dao.entity.Auto;
import edu.cibertec.taxihub.dao.repository.AutoRepository;
import edu.cibertec.taxihub.usecase.IAutoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutoUseCaseImpl implements IAutoUseCase {

    private final AutoRepository autoRepository;
    private final IPlacaApiService placaApiService;

    @Override
    public Page<Auto> listarAutosConFiltros(String placa, String marca, Boolean activo, Boolean disponibles, Pageable pageable) {
        Specification<Auto> spec = Specification.where(null);

        spec = spec.and(AutoPorPlacaSpecification.porPlaca(placa));
        spec = spec.and(AutoPorMarcaSpecification.porMarca(marca));
        spec = spec.and(AutoDisponibleSpecification.disponibles(disponibles));
        
        if (activo == null) {
            spec = spec.and(AutoPorEstadoSpecification.soloActivos());
        } else {
            spec = spec.and(AutoPorEstadoSpecification.porEstadoActivo(activo));
        }

        return autoRepository.findAll(spec, pageable);
    }

    @Override
    public Auto crearAuto(Auto auto) {
        return autoRepository.save(auto);
    }

    @Override
    public Auto actualizarAuto(Long id, Auto auto) {
        auto.setId(id);
        return autoRepository.save(auto);
    }

    @Override
    public void eliminarAuto(Long id) {
        Optional<Auto> autoOpt = autoRepository.findById(id);
        if (autoOpt.isPresent()) {
            Auto auto = autoOpt.get();
            auto.setActivo(false);
            autoRepository.save(auto);
        }
    }

    @Override
    public Optional<Auto> buscarAutoPorId(Long id) {
        return autoRepository.findById(id);
    }

    @Override
    public Optional<Auto> buscarAutoPorPlaca(String placa) {
        return autoRepository.findByPlacaAndActivoTrue(placa);
    }

    @Override
    public Optional<Auto> obtenerInformacionVehiculo(String placa) {
        try {
            Optional<Auto> autoFromApiOpt = placaApiService.getPlacaInfo(placa);
            return autoFromApiOpt;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean empleadoTieneAutoAsignado(String numeroDocumento) {
        return autoRepository.existsByEmpleadoPersonaNumeroDocumentoAndActivoTrue(numeroDocumento);
    }
}