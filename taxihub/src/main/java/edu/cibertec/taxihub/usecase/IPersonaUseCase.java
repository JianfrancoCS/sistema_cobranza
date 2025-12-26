package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Persona;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IPersonaUseCase {
    Persona crearPersona(Persona persona);
    Optional<Persona> obtenerYCachearPersonaPorDni(String dni);
}
