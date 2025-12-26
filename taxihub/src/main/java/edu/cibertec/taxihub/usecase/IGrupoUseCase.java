package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Grupo;

import java.util.List;

public interface IGrupoUseCase {
    List<Grupo> listarGrupos();
    List<Grupo> buscarGruposPorNombre(String nombre);
}
