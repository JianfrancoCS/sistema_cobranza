package edu.cibertec.taxihub_rest.services;

import edu.cibertec.taxihub_rest.dto.MarcacionRespuesta;

public interface IMarcacionService {

    MarcacionRespuesta marcarIngreso(String numeroDocumento);
    MarcacionRespuesta marcarSalida(String numeroDocumento);


}
