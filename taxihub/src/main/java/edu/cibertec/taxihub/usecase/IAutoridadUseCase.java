package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Autoridad;

import java.util.List;

public interface IAutoridadUseCase {
    List<Autoridad> listarAutoridades();
    public List<Autoridad> buscarAutoridadesPorNombre(String nombre);
}
