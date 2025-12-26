package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.dto.FactilizaPlacaDatos;
import edu.cibertec.taxihub.dto.FactilizaReponse;
import edu.cibertec.taxihub.usecase.IPlacaApiService;
import edu.cibertec.taxihub.dao.entity.Auto;
import edu.cibertec.taxihub.exception.GlobalException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class FactilizaPlacaServiceImpl implements IPlacaApiService {

    private final RestTemplate placaRestTemplate;

    public FactilizaPlacaServiceImpl(@Qualifier("placaRestTemplate") RestTemplate placaRestTemplate) {
        this.placaRestTemplate = placaRestTemplate;
    }

    @Override
    public Optional<Auto> getPlacaInfo(String placa)  {
        ParameterizedTypeReference<FactilizaReponse<FactilizaPlacaDatos>> responseType =
            new ParameterizedTypeReference<FactilizaReponse<FactilizaPlacaDatos>>() {};

        ResponseEntity<FactilizaReponse<FactilizaPlacaDatos>> responseEntity =
            placaRestTemplate.exchange("/v1/placa/info/{placa}", HttpMethod.GET, null, responseType, placa);

        FactilizaReponse<FactilizaPlacaDatos> response = responseEntity.getBody();

        if (response == null || !response.success() || response.data() == null) {
            return Optional.empty();
        }

        FactilizaPlacaDatos placaDatos = response.data();

        Auto auto = new Auto();
        auto.setPlaca(placaDatos.placa());
        auto.setMarca(placaDatos.marca());
        auto.setModelo(placaDatos.modelo());
        auto.setSerie(placaDatos.serie());
        auto.setColor(placaDatos.color());
        auto.setMotor(placaDatos.motor());
        auto.setVin(placaDatos.vin());

        return Optional.of(auto);
    }
}