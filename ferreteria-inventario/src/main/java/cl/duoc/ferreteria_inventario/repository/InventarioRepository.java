package cl.duoc.ferreteria_inventario.repository;

import cl.duoc.ferreteria_inventario.model.InventarioItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<InventarioItem, Long> {
    Optional<InventarioItem> findByProductoId(Long productoId);

    boolean existsByProductoId(Long productoId);

    boolean existsByProductoSkuIgnoreCase(String productoSku);
}