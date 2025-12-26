package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.dao.entity.Autoridad;
import edu.cibertec.taxihub.dao.repository.AutoridadRepository;
import edu.cibertec.taxihub.usecase.IAutoridadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoridadUseCaseImpl implements IAutoridadUseCase {

    private final AutoridadRepository autoridadRepository;

    public List<Autoridad> listarAutoridades() {
        return autoridadRepository.findAllActive();
    }

    public List<Autoridad> buscarAutoridadesPorNombre(String nombre) {
        return autoridadRepository.findByNombreAutoridadContainingIgnoreCaseAndActivoTrue(nombre);
    }
}