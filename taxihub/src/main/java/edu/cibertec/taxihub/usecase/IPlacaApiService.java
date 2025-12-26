package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Auto;

import java.util.Optional;

public interface IPlacaApiService {
   Optional<Auto> getPlacaInfo(String placa) ;
}
