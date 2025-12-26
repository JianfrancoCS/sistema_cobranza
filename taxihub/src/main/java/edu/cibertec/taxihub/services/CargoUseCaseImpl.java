package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.dao.entity.Cargo;
import edu.cibertec.taxihub.dao.repository.CargoRepository;
import edu.cibertec.taxihub.usecase.ICargoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CargoUseCaseImpl implements ICargoUseCase {

    private final CargoRepository cargoRepository;

    @Override
    public List<Cargo> listarCargos() {
        return cargoRepository.findAllActive();
    }

    @Override
    public Optional<Cargo> buscarCargoPorId(Long id) {
        return cargoRepository.findById(id)
                .filter(cargo -> cargo.isActivo());
    }

    @Override
    public List<Cargo> buscarCargosPorNombre(String nombre) {
        return cargoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }
}