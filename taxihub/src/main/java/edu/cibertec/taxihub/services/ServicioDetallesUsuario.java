package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.dao.entity.Usuario;
import edu.cibertec.taxihub.dao.entity.Grupo;
import edu.cibertec.taxihub.dao.entity.Autoridad;
import edu.cibertec.taxihub.security.UsuarioDetalles;
import edu.cibertec.taxihub.usecase.IUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ServicioDetallesUsuario implements UserDetailsService {

    private final IUsuarioUseCase usuarioUseCase;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioUseCase.buscarUsuarioPorCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + correo);
        }

        String nombreCompleto = obtenerNombreCompleto(usuario);
        String imagenBase64 = usuario.getImagenBase64();
        String imagenTipo = usuario.getImagenTipo();
        
        return new UsuarioDetalles(
                usuario.getCorreo(),
                usuario.getContrasena(),
                usuario.isActivo(),
                true,
                !usuario.tieneContrasenaTemporalActiva(),
                true,
                obtenerAutoridades(usuario),
                nombreCompleto,
                imagenBase64,
                imagenTipo,
                usuario.getId()
        );
    }

    private Collection<? extends GrantedAuthority> obtenerAutoridades(Usuario usuario) {
        Set<GrantedAuthority> autoridades = new HashSet<>();

        for (Grupo grupo : usuario.getGrupos()) {
            if (grupo.isActivo()) {
                autoridades.add(new SimpleGrantedAuthority("ROLE_" + grupo.getNombreGrupo()));
                for (Autoridad autoridad : grupo.getAutoridades()) {
                    if (autoridad.isActivo()) {
                        autoridades.add(new SimpleGrantedAuthority(autoridad.getNombreAutoridad()));
                    }
                }
            }
        }

        for (Autoridad autoridad : usuario.getAutoridades()) {
            if (autoridad.isActivo()) {
                autoridades.add(new SimpleGrantedAuthority(autoridad.getNombreAutoridad()));
            }
        }

         return autoridades;
    }
    
    private String obtenerNombreCompleto(Usuario usuario) {
        if (usuario.getEmpleado() != null && usuario.getEmpleado().getPersona() != null) {
            var persona = usuario.getEmpleado().getPersona();
            return String.join(" ",
                    persona.getNombre(),
                    persona.getApePaterno(),
                    persona.getApeMaterno()
            ).trim();
        }
        return usuario.getCorreo();
    }
}