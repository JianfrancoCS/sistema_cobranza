package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.usecase.IReniecApiService;
import edu.cibertec.taxihub.constantes.TipoDocuementoEnum;
import edu.cibertec.taxihub.dao.entity.Persona;
import edu.cibertec.taxihub.dao.entity.TipoDocumento;
import edu.cibertec.taxihub.exception.GlobalException;
import edu.cibertec.taxihub.exception.GlobalException;
import edu.cibertec.taxihub.dao.repository.PersonaRepository;
import edu.cibertec.taxihub.dao.repository.TipoDocumentoRepository;
import edu.cibertec.taxihub.usecase.IPersonaUseCase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class PersonaUseCaseImpl implements IPersonaUseCase {
    private final PersonaRepository personaRepository;
    private final IReniecApiService reniecApiService;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public PersonaUseCaseImpl(PersonaRepository personaRepository, @Qualifier("peruDevsReniecServiceImpl") IReniecApiService reniecApiService, TipoDocumentoRepository tipoDocumentoRepository) {
        this.personaRepository = personaRepository;
        this.reniecApiService = reniecApiService;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @Override
    @Transactional
    public Persona crearPersona(Persona persona) {
        return personaRepository.save(persona);
    }



    @Override
    @Transactional
    public Optional<Persona> obtenerYCachearPersonaPorDni(String dni) {
        Optional<Persona> personaExistente = personaRepository.findByNumeroDocumentoAndActivoTrueWithRelations(dni);
        if (personaExistente.isPresent()) {
            return personaExistente;
        }

        Optional<Persona> reniecDataOpt = reniecApiService.getReniecInfo(dni);
        if (reniecDataOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Persona reniecData = reniecDataOpt.get();
        Persona nuevaPersona = new Persona();
        nuevaPersona.setNumeroDocumento(reniecData.getNumeroDocumento());
        nuevaPersona.setNombre(reniecData.getNombre());
        nuevaPersona.setApePaterno(reniecData.getApePaterno());
        if (reniecData.getApeMaterno() != null) {
            nuevaPersona.setApeMaterno(reniecData.getApeMaterno());
        }
        nuevaPersona.setGenero(reniecData.getGenero());
        if (reniecData.getFechaNac() != null) {
            nuevaPersona.setFechaNac(reniecData.getFechaNac());
        }
        TipoDocumento tipoDocumentoDni = tipoDocumentoRepository.findByCodigo(TipoDocuementoEnum.DNI.getCodigo())
                .orElseThrow(() -> new GlobalException("Tipo de Documento 'DNI' no encontrado. Asegúrate de que exista en la base de datos."));
        nuevaPersona.setTipoDocumento(tipoDocumentoDni);
        Persona personaGuardada = personaRepository.save(nuevaPersona);
        return Optional.of(personaGuardada);
    }
}