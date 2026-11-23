package cl.duoc.ferreteria_inventario.config;

import cl.duoc.ferreteria_inventario.model.InventarioItem;
import cl.duoc.ferreteria_inventario.repository.InventarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Configuration
@Profile("!prod")
public class DataInitializer {

    @Bean
    CommandLineRunner initInventario(InventarioRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new InventarioItem(null, 1L, "FER-001", "Martillo Carpintero 16 oz", 25, 5,
                        LocalDateTime.now()));
                repo.save(new InventarioItem(null, 2L, "FER-002", "Taladro Percutor 650W", 12, 3, LocalDateTime.now()));
                repo.save(new InventarioItem(null, 3L, "FER-003", "Saco Cemento Especial 25 kg", 100, 20,
                        LocalDateTime.now()));
                repo.save(new InventarioItem(null, 4L, "FER-004", "Esmalte al Agua Blanco 1 Galón", 18, 5,
                        LocalDateTime.now()));
            }
        };
    }
}