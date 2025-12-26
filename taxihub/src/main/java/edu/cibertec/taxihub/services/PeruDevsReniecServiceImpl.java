package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.usecase.IReniecApiService;
import edu.cibertec.taxihub.dto.PerudevsReniecDatos;
import edu.cibertec.taxihub.dao.entity.Persona;
import edu.cibertec.taxihub.dto.PerudevsResponse;
import edu.cibertec.taxihub.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class PeruDevsReniecServiceImpl implements IReniecApiService {
    private static final String API_PATH = "/api/v1/dni/complete";
    @Value("${perudevs.token}")
    private String token;

    @Autowired
    @Qualifier(value = "peruDevsReniecRestTemplate")
    private RestTemplate reniecRestTemplate;


    @Override
    public Optional<Persona> getReniecInfo(String dni) {
        String url = UriComponentsBuilder.fromPath(API_PATH)
                .queryParam("document",dni)
                .queryParam("key",token)
                .toUriString();
        ParameterizedTypeReference<PerudevsResponse<PerudevsReniecDatos>> responseType = new ParameterizedTypeReference<PerudevsResponse<PerudevsReniecDatos>>(){};
        ResponseEntity<PerudevsResponse<PerudevsReniecDatos>> responseEntity =  reniecRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
        PerudevsResponse<PerudevsReniecDatos> response = responseEntity.getBody();
        if (!response.estado()){
            return Optional.empty();
        }
        PerudevsReniecDatos resultado = response.resultado();
        Persona persona = new Persona();
        persona.setNumeroDocumento(resultado.id());
        persona.setNombre(resultado.nombres());
        persona.setApePaterno(resultado.apellido_paterno());
        persona.setApeMaterno(resultado.apellido_materno());
        persona.setGenero(resultado.genero());
        persona.setFechaNac(DateHelper.parseDate(resultado.fecha_nacimiento()));

        return Optional.of(persona);
    }
}
