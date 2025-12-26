package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Licencia;

import java.util.Optional;

public interface ILicenciaUseCase {
    Optional<Licencia> buscarLincenciaPorDni(String dni);
}