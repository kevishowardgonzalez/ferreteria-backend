package cl.duoc.ferreteria_productos.service;

import cl.duoc.ferreteria_productos.dto.ProductoRequest;
import cl.duoc.ferreteria_productos.exception.Mensaje; // <-- Tu excepción con su nuevo nombre
import cl.duoc.ferreteria_productos.model.Producto;
import cl.duoc.ferreteria_productos.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findAll();
    }

    public List<Producto> buscarPorFiltro(String filtro) {
        return repository.findByNombreContainingIgnoreCaseOrCategoriaContainingIgnoreCase(filtro.trim(), filtro.trim());
    }

    public Producto buscar(Long id) {
        return repository.findById(id)
                // Usando tu excepción "mensaje"
                .orElseThrow(() -> new Mensaje(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id));
    }

    public Producto crear(ProductoRequest req) {
        Producto p = new Producto();
        p.setNombre(req.getNombre().trim());
        p.setCategoria(req.getCategoria().trim());
        p.setPrecio(req.getPrecio());
        p.setStock(req.getStock());
        if (req.getDescripcion() != null) {
            p.setDescripcion(req.getDescripcion().trim());
        }
        return repository.save(p);
    }

    public Producto actualizar(Long id, ProductoRequest req) {
        Producto p = buscar(id); 
        p.setNombre(req.getNombre().trim());
        p.setCategoria(req.getCategoria().trim());
        p.setPrecio(req.getPrecio());
        p.setStock(req.getStock());
        if (req.getDescripcion() != null) {
            p.setDescripcion(req.getDescripcion().trim());
        }
        return repository.save(p);
    }

    public void eliminar(Long id) {
        Producto p = buscar(id);
        repository.delete(p);
    }
}