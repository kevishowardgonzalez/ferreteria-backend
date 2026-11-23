package cl.duoc.ferreteria_categorias.config;

import cl.duoc.ferreteria_categorias.model.Categoria;
import cl.duoc.ferreteria_categorias.repository.CategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
public class DataInitializer {

    @Bean
    CommandLineRunner initCategorias(CategoriaRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Categoria(null, "Herramientas Manuales", "Martillos, destornilladores, llaves y alicates",
                        true));
                repo.save(new Categoria(null, "Herramientas Eléctricas", "Taladros, sierras, esmeriles y lijadoras",
                        true));
                repo.save(new Categoria(null, "Construcción", "Cemento, adhesivos, ladrillos y materiales gruesos",
                        true));
                repo.save(new Categoria(null, "Pinturas y Acabados", "Esmaltes, látex, brochas y rodillos", true));
                repo.save(new Categoria(null, "Gasfitería y Electricidad", "Tuberías, grifería, cables e interruptores",
                        true));
            }
        };
    }
}