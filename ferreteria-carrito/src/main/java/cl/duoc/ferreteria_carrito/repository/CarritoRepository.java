package cl.duoc.ferreteria_carrito.repository;

import cl.duoc.ferreteria_carrito.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByUsuarioCorreoIgnoreCase(String usuarioCorreo);

    Optional<CarritoItem> findByUsuarioCorreoIgnoreCaseAndProductoId(String usuarioCorreo, Long productoId);

    void deleteByUsuarioCorreoIgnoreCase(String usuarioCorreo);
}