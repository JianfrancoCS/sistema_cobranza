package edu.cibertec.taxihub.dto;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;

public record AutoFormDTO(
         @NotBlank(message = "La placa es obligatoria")
         @Size(max = 10, message = "La placa no puede tener más de 10 caracteres")
         String placa,
         
         @NotBlank(message = "La marca es obligatoria")
         @Size(max = 50, message = "La marca no puede tener más de 50 caracteres")
         String marca,
         
         @NotBlank(message = "El modelo es obligatorio")
         @Size(max = 100, message = "El modelo no puede tener más de 50 caracteres")
         String modelo,
         
         @Size(max = 50, message = "La serie no puede tener más de 50 caracteres")
         String serie,
         
         @NotBlank(message = "El color es obligatorio")
         @Size(max = 30, message = "El color no puede tener más de 30 caracteres")
         String color,
         
         @Size(max = 50, message = "El motor no puede tener más de 50 caracteres")
         String motor,
         
         String vin,
         
         MultipartFile imagen,
         
         @Size(max = 8, message = "El número de documento no puede tener más de 8 caracteres")
         String empleadoId,
         
         boolean esPropioEmpresa,
         Boolean activo
) {
}