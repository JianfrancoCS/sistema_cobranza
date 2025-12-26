package edu.cibertec.taxihub;

import edu.cibertec.taxihub.constantes.CargoEnum;
import edu.cibertec.taxihub.dao.entity.Grupo;
import edu.cibertec.taxihub.dao.entity.Usuario;
import edu.cibertec.taxihub.dao.repository.GrupoRepository;
import edu.cibertec.taxihub.dao.repository.UsuarioRepository;
import edu.cibertec.taxihub.usecase.IUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@SpringBootApplication
@EnableJpaAuditing
@RequiredArgsConstructor
public class TaxihubApplication implements CommandLineRunner {

    private final IUsuarioUseCase usuarioUseCase;
    private final UsuarioRepository usuarioRepository;
    private final GrupoRepository grupoRepository;
    private final PasswordEncoder passwordEncoder;


	public static void main(String[] args) {
		SpringApplication.run(TaxihubApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        crearUsuarioAdminBasico();
    }

    private void crearUsuarioAdminBasico() {
        String correoAdmin = "sanpablosegundo@gmail.com";
        String passwordAdmin = "admin123";
        if (usuarioRepository.existsByCorreoAndActivoTrue(correoAdmin)) {
            Usuario byCorreoAndActivoTrue = usuarioRepository.findByCorreoAndActivoTrue(correoAdmin).get();
            byCorreoAndActivoTrue.setContrasena(passwordEncoder.encode(passwordAdmin));
            usuarioRepository.save(byCorreoAndActivoTrue);

            System.out.println("Usuario admin ya existe: " + correoAdmin);
            System.out.println("Usuario admin con contraseña: " + correoAdmin);
            System.out.println("===========================================");
            System.out.println("Hora local de la app: "+ LocalDateTime.now());
            System.out.println("===========================================");

            return;
        }

        try {
            Optional<Grupo> grupoAdminOpt = grupoRepository.findByNombreGrupo(CargoEnum.ADMINISTRADOR.getNombre());
            if (grupoAdminOpt.isEmpty()) {
                System.err.println("Error: No se encontró el grupo ADMINISTRADOR. Ejecute las migraciones primero.");
                return;
            }

            Long grupoAdminId = grupoAdminOpt.get().getId();

            usuarioUseCase.crearUsuario(
                correoAdmin,
                    passwordAdmin,
                false,
                null,
                grupoAdminId,
                null,
                null,
                null,
                true
            );

            System.out.println("===========================================");
            System.out.println("Usuario administrador creado exitosamente:");
            System.out.println("Correo: " + correoAdmin);
            System.out.println("Contraseña: "+ passwordAdmin);
            System.out.println("===========================================");
            System.out.println("Hora local de la app: "+ LocalDateTime.now());
            System.out.println("===========================================");


        } catch (Exception e) {
            System.err.println("Error al crear usuario admin: " + e.getMessage());
        }
    }
}
