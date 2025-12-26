package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.dao.entity.Grupo;
import edu.cibertec.taxihub.dao.repository.GrupoRepository;
import edu.cibertec.taxihub.usecase.IGrupoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrupoUseCaseImpl implements IGrupoUseCase {

    private final GrupoRepository grupoRepository;

    public List<Grupo> listarGrupos() {
        return grupoRepository.findAllActive();
    }

    public List<Grupo> buscarGruposPorNombre(String nombre) {
        return grupoRepository.findByNombreGrupoContainingIgnoreCaseAndActivoTrue(nombre);
    }
}