package cl.duoc.ferreteria_productos.repository;

import cl.duoc.ferreteria_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Método necesario para el input de búsqueda en tu AdminProductos.jsx
    List<Producto> findByNombreContainingIgnoreCaseOrCategoriaContainingIgnoreCase(String nombre, String categoria);
}