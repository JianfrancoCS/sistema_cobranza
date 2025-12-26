package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Persona;

import java.util.Optional;

public interface IReniecApiService {
   Optional<Persona> getReniecInfo(String dni);
}
