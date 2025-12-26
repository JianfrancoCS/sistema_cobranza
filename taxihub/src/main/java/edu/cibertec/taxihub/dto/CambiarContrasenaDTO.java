package edu.cibertec.taxihub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CambiarContrasenaDTO(
        @NotBlank(message = "La contraseña actual es obligatoria")
        String contrasenaActual,
        
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 6, max = 15, message = "La nueva contraseña debe tener entre 6 y 15 caracteres")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "La contraseña debe contener al menos: una mayúscula, una minúscula, un número y un símbolo (@$!%*?&)"
        )
        String nuevaContrasena,
        
        @NotBlank(message = "La confirmación de contraseña es obligatoria")
        String confirmarContrasena
) {
}