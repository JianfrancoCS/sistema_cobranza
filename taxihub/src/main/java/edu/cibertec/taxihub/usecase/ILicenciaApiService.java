package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Licencia;

import java.util.Optional;

public interface ILicenciaApiService {
   Optional<Licencia> getLicenciaInfo(String dni) ;
}
