package edu.cibertec.taxihub.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record PagoFormDTO(
        @NotNull(message = "Debe seleccionar una deuda")
        Long deudaId,
        
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        @DecimalMax(value = "100.00", message = "El monto no puede exceder S/ 100.00")
        BigDecimal monto,
        
        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        String descripcion,
        
        MultipartFile voucher
) {
}