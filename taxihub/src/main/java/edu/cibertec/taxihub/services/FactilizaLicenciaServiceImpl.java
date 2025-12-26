package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.dto.FactilizaLicenciaDatos;
import edu.cibertec.taxihub.dto.FactilizaReponse;
import edu.cibertec.taxihub.usecase.ILicenciaApiService;
import edu.cibertec.taxihub.dao.entity.Licencia;
import edu.cibertec.taxihub.exception.GlobalException;
import edu.cibertec.taxihub.utils.DateHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;


@Service
public class FactilizaLicenciaServiceImpl implements ILicenciaApiService {

    private final RestTemplate licenciaRestTemplate;

    public FactilizaLicenciaServiceImpl(@Qualifier("licenciaRestTemplate") RestTemplate licenciaRestTemplate) {
        this.licenciaRestTemplate = licenciaRestTemplate;
    }

    @Override
    public Optional<Licencia> getLicenciaInfo(String dni)  {
        try {
            ParameterizedTypeReference<FactilizaReponse<FactilizaLicenciaDatos>> responseType =
                new ParameterizedTypeReference<FactilizaReponse<FactilizaLicenciaDatos>>() {};

            String url = "/v1/licencia/info/" + dni;

            ResponseEntity<FactilizaReponse<FactilizaLicenciaDatos>> responseEntity =
                licenciaRestTemplate.exchange(url, HttpMethod.GET, null, responseType);

        FactilizaReponse<FactilizaLicenciaDatos> response = responseEntity.getBody();

        if (response == null || response.status() != 200 || response.data() == null) {
            return Optional.empty();
        }

        FactilizaLicenciaDatos licenciaDatos = response.data();

        Licencia licencia = new Licencia();

        FactilizaLicenciaDatos.LicenciaDetalle detalleLicencia = licenciaDatos.licencia();

        if (detalleLicencia != null) {
            licencia.setNumeroLicencia(detalleLicencia.numero());
            licencia.setCategoriaLicencia(detalleLicencia.categoria());

            if (detalleLicencia.fecha_vencimiento() != null && !detalleLicencia.fecha_vencimiento().isEmpty()) {
                licencia.setFechaVencimientoLicencia(DateHelper.parseDate(detalleLicencia.fecha_vencimiento()));
            }
        }

        return Optional.of(licencia);

        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
