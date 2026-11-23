package cl.duoc.ferreteria_productos.repository;

import cl.duoc.ferreteria_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsBySkuIgnoreCase(String sku);

    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);

    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}