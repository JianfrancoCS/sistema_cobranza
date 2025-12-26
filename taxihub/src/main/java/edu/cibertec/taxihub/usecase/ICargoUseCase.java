package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Cargo;

import java.util.List;
import java.util.Optional;

public interface ICargoUseCase {
    List<Cargo> listarCargos();
    Optional<Cargo> buscarCargoPorId(Long id);
    List<Cargo> buscarCargosPorNombre(String nombre);
}