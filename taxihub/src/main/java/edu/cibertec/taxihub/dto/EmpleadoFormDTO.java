package edu.cibertec.taxihub.dto;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record EmpleadoFormDTO(
         @NotBlank(message = "El DNI es obligatorio")
         @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos")
         String numeroDocumento,
         
         @NotNull(message = "Debe seleccionar un cargo")
         Long cargoId,
         
         @NotNull(message = "La fecha de inicio es obligatoria")
         @DateTimeFormat(pattern = "yyyy-MM-dd")
         LocalDate fechaInicio,
         
         @DateTimeFormat(pattern = "yyyy-MM-dd")
         LocalDate fechaFin,
         
         Boolean activo
) {
}
