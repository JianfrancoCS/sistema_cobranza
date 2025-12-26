package edu.cibertec.taxihub.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UsuarioDetalles extends User {
    
    private final String nombreCompleto;
    private final String imagenBase64;
    private final String imagenTipo;
    private final Long usuarioId;
    
    public UsuarioDetalles(String username, String password, Collection<? extends GrantedAuthority> authorities,
                          String nombreCompleto, String imagenBase64, String imagenTipo, Long usuarioId) {
        super(username, password, authorities);
        this.nombreCompleto = nombreCompleto;
        this.imagenBase64 = imagenBase64;
        this.imagenTipo = imagenTipo;
        this.usuarioId = usuarioId;
    }
    
    public UsuarioDetalles(String username, String password, boolean enabled, boolean accountNonExpired,
                          boolean credentialsNonExpired, boolean accountNonLocked,
                          Collection<? extends GrantedAuthority> authorities,
                          String nombreCompleto, String imagenBase64, String imagenTipo, Long usuarioId) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.nombreCompleto = nombreCompleto;
        this.imagenBase64 = imagenBase64;
        this.imagenTipo = imagenTipo;
        this.usuarioId = usuarioId;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public String getImagenBase64() {
        return imagenBase64;
    }
    
    public String getImagenTipo() {
        return imagenTipo;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public boolean tieneImagen() {
        return imagenBase64 != null && !imagenBase64.isEmpty();
    }
}