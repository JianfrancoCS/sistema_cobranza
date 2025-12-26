package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.dao.entity.Autoridad;
import edu.cibertec.taxihub.dao.entity.Empleado;
import edu.cibertec.taxihub.dao.entity.Grupo;
import edu.cibertec.taxihub.dao.entity.Persona;
import edu.cibertec.taxihub.dao.entity.Usuario;
import edu.cibertec.taxihub.exception.GlobalException;
import edu.cibertec.taxihub.dao.repository.AutoridadRepository;
import edu.cibertec.taxihub.dao.repository.EmpleadoRepository;
import edu.cibertec.taxihub.dao.repository.GrupoRepository;
import edu.cibertec.taxihub.dao.repository.PersonaRepository;
import edu.cibertec.taxihub.dao.repository.UsuarioRepository;
import edu.cibertec.taxihub.dao.specification.UsuarioPorActivoSpec;
import edu.cibertec.taxihub.dao.specification.UsuarioPorCorreoSpec;
import edu.cibertec.taxihub.usecase.IUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioUseCaseImpl implements IUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final AutoridadRepository autoridadRepository;
    private final GrupoRepository grupoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder codificadorContrasena;

    public Page<Usuario> listarUsuarios(String correo, Boolean activo, Pageable pageable) {
        Specification<Usuario> spec = Specification
                .where(UsuarioPorCorreoSpec.conCorreo(correo))
                .and(UsuarioPorActivoSpec.conActivo(activo));
        
        return usuarioRepository.findAll(spec, pageable);
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new GlobalException("usuario.not.found", id));
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorIdOptional(Long id) {
        return usuarioRepository.findByIdWithRelations(id);
    }

    public Optional<Usuario> buscarUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreoAndActivoTrue(correo);
    }

    @Transactional
    public Optional<Usuario> crearUsuario(String correo, String contrasena, boolean contrasenaTemporal,
                               List<Long> autoridadIds, Long grupoId, String numeroDocumento,
                               byte[] imagen, String imagenTipo, boolean activo) {

        if (usuarioRepository.existsByCorreoAndActivoTrue(correo)) {
            return Optional.empty();
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContrasena(codificadorContrasena.encode(contrasena));
        usuario.setContrasenaTemporal(contrasenaTemporal);
        usuario.setActivo(activo);

        if (numeroDocumento != null && !numeroDocumento.trim().isEmpty()) {
            Optional<Empleado> empleadoOpt = empleadoRepository.findByPersonaNumeroDocumento(numeroDocumento.trim());
            if (empleadoOpt.isPresent()) {
                usuario.setEmpleado(empleadoOpt.get());
                
                if (imagen != null && imagen.length > 0) {
                    Persona persona = empleadoOpt.get().getPersona();
                    if (persona != null) {
                        persona.setImagen(imagen);
                        persona.setImagenTipo(imagenTipo);
                    }
                }
            }
        }

        if (autoridadIds != null && !autoridadIds.isEmpty()) {
            Set<Autoridad> autoridades = new HashSet<>(autoridadRepository.findAllById(autoridadIds));
            usuario.setAutoridades(autoridades);
        }

        if (grupoId != null) {
            Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
            if (grupoOpt.isPresent()) {
                Set<Grupo> grupos = new HashSet<>();
                grupos.add(grupoOpt.get());
                usuario.setGrupos(grupos);
            }
        }

        return Optional.of(usuarioRepository.save(usuario));
    }

    @Transactional
    public Optional<Usuario> actualizarUsuario(Long id, String correo, List<Long> autoridadIds, Long grupoId) {
        Usuario usuario = buscarUsuarioPorId(id);

        if (!usuario.getCorreo().equals(correo) && usuarioRepository.existsByCorreoAndActivoTrue(correo)) {
            return Optional.empty();
        }

        usuario.setCorreo(correo);

        if (autoridadIds != null) {
            Set<Autoridad> autoridades = autoridadIds.isEmpty() ?
                new HashSet<>() : new HashSet<>(autoridadRepository.findAllById(autoridadIds));
            usuario.setAutoridades(autoridades);
        }

        if (grupoId != null) {
            Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
            if (grupoOpt.isPresent()) {
                Set<Grupo> grupos = new HashSet<>();
                grupos.add(grupoOpt.get());
                usuario.setGrupos(grupos);
            } else {
                usuario.setGrupos(new HashSet<>());
            }
        }

        return Optional.of(usuarioRepository.save(usuario));
    }

    @Transactional
    public Optional<Usuario> actualizarUsuarioConImagen(Long id, String correo, List<Long> autoridadIds, Long grupoId, byte[] imagen, String imagenTipo) {
        Usuario usuario = buscarUsuarioPorId(id);

        if (!usuario.getCorreo().equals(correo) && usuarioRepository.existsByCorreoAndActivoTrue(correo)) {
            return Optional.empty();
        }

        usuario.setCorreo(correo);

        if (autoridadIds != null) {
            Set<Autoridad> autoridades = autoridadIds.isEmpty() ?
                new HashSet<>() : new HashSet<>(autoridadRepository.findAllById(autoridadIds));
            usuario.setAutoridades(autoridades);
        }

        if (grupoId != null) {
            Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
            if (grupoOpt.isPresent()) {
                Set<Grupo> grupos = new HashSet<>();
                grupos.add(grupoOpt.get());
                usuario.setGrupos(grupos);
            } else {
                usuario.setGrupos(new HashSet<>());
            }
        }

        if (imagen != null && imagen.length > 0 && usuario.getEmpleado() != null) {
            Persona persona = usuario.getEmpleado().getPersona();
            if (persona != null) {
                persona.setImagen(imagen);
                persona.setImagenTipo(imagenTipo);
                personaRepository.save(persona);
            }
        }

        return Optional.of(usuarioRepository.save(usuario));
    }

    @Transactional
    public void cambiarContrasena(Long id, String nuevaContrasena) {
        Usuario usuario = buscarUsuarioPorId(id);
        usuario.setContrasena(codificadorContrasena.encode(nuevaContrasena));
        usuario.setContrasenaTemporal(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public boolean validarContrasenaActual(String correo, String contrasenaActual) {
        Optional<Usuario> usuarioOpt = buscarUsuarioPorCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        Usuario usuario = usuarioOpt.get();
        return codificadorContrasena.matches(contrasenaActual, usuario.getContrasena());
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorEmpleado(String numeroDocumento) {
        return usuarioRepository.findByEmpleadoPersonaNumeroDocumentoAndActivoTrue(numeroDocumento);
    }
    
    @Override
    public String obtenerNumeroDocumentoEmpleadoPorEmail(String email) {
        return usuarioRepository.findByCorreoAndActivoTrue(email)
                .map(Usuario::getEmpleado)
                .map(Empleado::getPersona)
                .map(Persona::getNumeroDocumento)
                .orElse(null);
    }
}