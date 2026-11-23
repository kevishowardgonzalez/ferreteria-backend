package cl.duoc.ferreteria_productos.config;

import cl.duoc.ferreteria_productos.model.Producto;
import cl.duoc.ferreteria_productos.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
public class DataInitializer {

    @Bean
    CommandLineRunner initProductos(ProductoRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Producto(null, "FER-001", "Martillo Carpintero 16 oz",
                        "Mango de fibra de vidrio antideslizante y cabeza de acero forjado",
                        "Stanley", 12990, 25, 1L, "", true));
                repo.save(new Producto(null, "FER-002", "Taladro Percutor 650W",
                        "Mandril 13mm velocidad variable reversible",
                        "Bosch", 54990, 12, 2L, "", true));
                repo.save(new Producto(null, "FER-003", "Saco Cemento Especial 25 kg",
                        "Cemento puzolánico de alta resistencia para obra gruesa",
                        "Polpaico", 4890, 100, 3L, "", true));
                repo.save(new Producto(null, "FER-004", "Esmalte al Agua Blanco 1 Galón",
                        "Terminación satinada lavable interior y exterior",
                        "Sherwin Williams", 22990, 18, 4L, "", true));
            }
        };
    }
}