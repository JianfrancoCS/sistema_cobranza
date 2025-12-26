package edu.cibertec.taxihub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioFormDTO {
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String correo;
    
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;
    
    private boolean contrasenaTemporal = false;
    
    private List<Long> autoridadIds;
    
    @NotNull(message = "Debe seleccionar un grupo")
    private Long grupoId;
    
    private MultipartFile imagen;
    
    private boolean activo = true;

    public void setContrasenaTemporal(Boolean contrasenaTemporal) {
        this.contrasenaTemporal = contrasenaTemporal != null ? contrasenaTemporal : false;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo != null ? activo : true;
    }
}