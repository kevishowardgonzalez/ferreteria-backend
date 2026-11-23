package cl.duoc.ferreteria_roles.config;

import cl.duoc.ferreteria_roles.model.RolEntity;
import cl.duoc.ferreteria_roles.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RolRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new RolEntity(null, "ADMIN", "Administrador total del sistema", true));
                repo.save(new RolEntity(null, "VENDEDOR", "Gestión de ventas, inventario y despacho", true));
                repo.save(new RolEntity(null, "CLIENTE", "Cliente comprador de la tienda", true));
            }
        };
    }
}