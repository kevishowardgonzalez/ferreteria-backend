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
        return repository.findAll().stream().filter(p -> !Boolean.TRUE.equals(p.getRetirado())).toList();
    }

    public List<Producto> buscarPorFiltro(String filtro) {
        return repository.findByNombreContainingIgnoreCaseOrCategoriaContainingIgnoreCase(filtro.trim(), filtro.trim())
                .stream().filter(p -> !Boolean.TRUE.equals(p.getRetirado())).toList();
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
        p.setStock(0);
        p.setRetirado(false);
        if (req.getDescripcion() != null) {
            p.setDescripcion(req.getDescripcion().trim());
        }
        return repository.save(p);
    }

    public Producto actualizar(Long id, ProductoRequest req) {
        Producto p = buscar(id);
        if (Boolean.TRUE.equals(p.getRetirado())) {
            throw new Mensaje(HttpStatus.CONFLICT, "El producto está retirado del catálogo");
        }
        p.setNombre(req.getNombre().trim());
        p.setCategoria(req.getCategoria().trim());
        p.setPrecio(req.getPrecio());
        if (req.getDescripcion() != null) {
            p.setDescripcion(req.getDescripcion().trim());
        }
        return repository.save(p);
    }

    public void eliminar(Long id) {
        Producto p = buscar(id);
        p.setRetirado(true);
        repository.save(p);
    }
}