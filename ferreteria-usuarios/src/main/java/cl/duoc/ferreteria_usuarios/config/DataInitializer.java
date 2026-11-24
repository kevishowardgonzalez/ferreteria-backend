package cl.duoc.ferreteria_usuarios.config;

import cl.duoc.ferreteria_usuarios.model.Usuario;
import cl.duoc.ferreteria_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.password:}")
    private String seedPassword;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0 && seedPassword != null && !seedPassword.isBlank()) {
            usuarioRepository.save(new Usuario(null, "12345678-5", "Kevis", "Admin", "admin@duoc.cl", passwordEncoder.encode(seedPassword), "ADMIN", true));
            usuarioRepository.save(new Usuario(null, "15987456-3", "Javier", "Cruz", "vendedor@duoc.cl", passwordEncoder.encode(seedPassword), "VENDEDOR", true));
            usuarioRepository.save(new Usuario(null, "19876543-2", "Yerickson", "Rodriguez", "carlos@gmail.com", passwordEncoder.encode(seedPassword), "CLIENTE", true));
        }
    }
}