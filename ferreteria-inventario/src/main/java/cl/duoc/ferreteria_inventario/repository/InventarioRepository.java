package cl.duoc.ferreteria_inventario.repository;

import cl.duoc.ferreteria_inventario.model.InventarioItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<InventarioItem, Long> {
    Optional<InventarioItem> findByProductoId(Long productoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventarioItem> findForUpdateByProductoId(Long productoId);

    boolean existsByProductoId(Long productoId);

    boolean existsByProductoSkuIgnoreCase(String productoSku);
}
