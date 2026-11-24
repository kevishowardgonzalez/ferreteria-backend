package cl.duoc.ferreteria_productos.config;

import cl.duoc.ferreteria_productos.model.Producto;
import cl.duoc.ferreteria_productos.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.math.BigDecimal;

@Configuration
@Profile("!prod")
public class DataInitializer {

    @Bean
    CommandLineRunner initProductos(ProductoRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                Producto p1 = new Producto();
                p1.setNombre("Martillo Carpintero 16 oz");
                p1.setCategoria("Herramientas Manuales");
                p1.setPrecio(new BigDecimal("12990"));
                p1.setStock(25);
                p1.setDescripcion("Mango de fibra de vidrio antideslizante y cabeza de acero forjado");
                repo.save(p1);

                Producto p2 = new Producto();
                p2.setNombre("Taladro Percutor 650W");
                p2.setCategoria("Herramientas Eléctricas");
                p2.setPrecio(new BigDecimal("54990"));
                p2.setStock(12);
                p2.setDescripcion("Mandril 13mm velocidad variable reversible");
                repo.save(p2);

                Producto p3 = new Producto();
                p3.setNombre("Saco Cemento Especial 25 kg");
                p3.setCategoria("Construcción");
                p3.setPrecio(new BigDecimal("4890"));
                p3.setStock(100);
                p3.setDescripcion("Cemento puzolánico de alta resistencia para obra gruesa");
                repo.save(p3);
            }
        };
    }
}