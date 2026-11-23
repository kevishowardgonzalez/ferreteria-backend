package cl.duoc.ferreteria_productos.service;

import cl.duoc.ferreteria_productos.dto.ProductoRequest;
import cl.duoc.ferreteria_productos.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_productos.exception.ReglaNegocioException;
import cl.duoc.ferreteria_productos.model.Producto;
import cl.duoc.ferreteria_productos.repository.ProductoRepository;
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

    public List<Producto> listarActivos() {
        return repository.findByActivoTrue();
    }

    public List<Producto> listarPorCategoria(Long categoriaId) {
        return repository.findByCategoriaIdAndActivoTrue(categoriaId);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre.trim());
    }

    public Producto buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }

    public Producto crear(ProductoRequest req) {
        String sku = req.getSku().trim().toUpperCase();
        if (repository.existsBySkuIgnoreCase(sku)) {
            throw new ReglaNegocioException("Ya existe un producto con el SKU: " + sku);
        }

        Producto p = new Producto(
                null,
                sku,
                req.getNombre().trim(),
                req.getDescripcion() != null ? req.getDescripcion().trim() : "",
                req.getMarca() != null ? req.getMarca().trim() : "Genérica",
                req.getPrecio(),
                req.getStock(),
                req.getCategoriaId(),
                req.getImagenUrl() != null ? req.getImagenUrl().trim() : "",
                req.getActivo() != null ? req.getActivo() : true);
        return repository.save(p);
    }

    public Producto actualizar(Long id, ProductoRequest req) {
        Producto p = buscar(id);
        p.setSku(req.getSku().trim().toUpperCase());
        p.setNombre(req.getNombre().trim());
        if (req.getDescripcion() != null)
            p.setDescripcion(req.getDescripcion().trim());
        if (req.getMarca() != null)
            p.setMarca(req.getMarca().trim());
        p.setPrecio(req.getPrecio());
        p.setStock(req.getStock());
        p.setCategoriaId(req.getCategoriaId());
        if (req.getImagenUrl() != null)
            p.setImagenUrl(req.getImagenUrl().trim());
        if (req.getActivo() != null)
            p.setActivo(req.getActivo());
        return repository.save(p);
    }

    public void desactivar(Long id) {
        Producto p = buscar(id);
        p.setActivo(false);
        repository.save(p);
    }
}