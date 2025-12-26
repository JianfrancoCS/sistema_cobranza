package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.usecase.ILicenciaApiService;
import edu.cibertec.taxihub.dao.entity.Licencia;
import edu.cibertec.taxihub.dao.entity.Persona;
import edu.cibertec.taxihub.dao.repository.LicenciaRepository;
import edu.cibertec.taxihub.dao.repository.PersonaRepository;
import edu.cibertec.taxihub.usecase.ILicenciaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LicenciaUseCaseImpl implements ILicenciaUseCase {

    private final ILicenciaApiService licenciaApiService;
    private final LicenciaRepository licenciaRepository;
    private final PersonaRepository personaRepository;

    @Override
    public Optional<Licencia> buscarLincenciaPorDni(String dni) {
        log.debug("Buscando licencia para DNI: {}", dni);
        
        try {
            Optional<Licencia> licenciaBD = licenciaRepository.findByPersonaNumeroDocumento(dni);
            if (licenciaBD.isPresent() && licenciaBD.get().isActivo()) {
                log.debug("Licencia encontrada en BD local para DNI: {}", dni);
                return licenciaBD;
            }
            
            log.debug("Licencia no encontrada en BD local, consultando API externa para DNI: {}", dni);
            
            Optional<Licencia> licenciaAPI = licenciaApiService.getLicenciaInfo(dni);
            if (licenciaAPI.isPresent()) {
                log.debug("Licencia encontrada en API externa para DNI: {}, guardando en BD", dni);
                
                Licencia licencia = licenciaAPI.get();
                Optional<Persona> personaOpt = personaRepository.findByNumeroDocumento(dni);
                
                if (personaOpt.isPresent()) {
                    licencia.setNumeroDocumento(dni);
                    licencia.setPersona(personaOpt.get());
                    licencia.setActivo(true);
                    
                    Licencia licenciaGuardada = licenciaRepository.save(licencia);
                    log.debug("Licencia guardada exitosamente en BD para DNI: {}", dni);
                    
                    return Optional.of(licenciaGuardada);
                } else {
                    log.warn("No se encontró persona con DNI: {} para asociar la licencia", dni);
                    return licenciaAPI;
                }
            }
            
            log.debug("No se encontró licencia ni en BD ni en API externa para DNI: {}", dni);
            return Optional.empty();
            
        } catch (Exception e) {
            log.error("Error al buscar licencia para DNI {}: {}", dni, e.getMessage());
            return Optional.empty();
        }
    }
}