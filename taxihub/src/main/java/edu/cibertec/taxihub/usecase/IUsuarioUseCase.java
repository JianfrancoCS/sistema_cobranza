package edu.cibertec.taxihub.usecase;

import edu.cibertec.taxihub.dao.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUsuarioUseCase {
    Page<Usuario> listarUsuarios(String correo, Boolean activo, Pageable pageable);
    Usuario buscarUsuarioPorId(Long id);
    Optional<Usuario> buscarUsuarioPorIdOptional(Long id);
    Optional<Usuario> buscarUsuarioPorCorreo(String correo);
    Optional<Usuario> crearUsuario(String correo, String contrasena, boolean contrasenaTemporal,
                         List<Long> autoridadIds, Long grupoId, String numeroDocumento,
                         byte[] imagen, String imagenTipo, boolean activo);
    Optional<Usuario> actualizarUsuario(Long id, String correo, List<Long> autoridadIds, Long grupoId);
    Optional<Usuario> actualizarUsuarioConImagen(Long id, String correo, List<Long> autoridadIds, Long grupoId, byte[] imagen, String imagenTipo);
    void cambiarContrasena(Long id, String nuevaContrasena);
    boolean validarContrasenaActual(String correo, String contrasenaActual);
    void eliminarUsuario(Long id);
    Optional<Usuario> buscarUsuarioPorEmpleado(String numeroDocumento);
    
    String obtenerNumeroDocumentoEmpleadoPorEmail(String email);
}
