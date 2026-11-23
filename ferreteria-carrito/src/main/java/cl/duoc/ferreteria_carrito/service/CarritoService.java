package cl.duoc.ferreteria_carrito.service;

import cl.duoc.ferreteria_carrito.dto.CarritoItemRequest;
import cl.duoc.ferreteria_carrito.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_carrito.model.CarritoItem;
import cl.duoc.ferreteria_carrito.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository repository;

    public CarritoService(CarritoRepository repository) {
        this.repository = repository;
    }

    public List<CarritoItem> obtenerCarrito(String correo) {
        return repository.findByUsuarioCorreoIgnoreCase(correo);
    }

    public CarritoItem agregarOActualizar(String correo, CarritoItemRequest req) {
        return repository.findByUsuarioCorreoIgnoreCaseAndProductoId(correo, req.getProductoId())
                .map(existente -> {
                    existente.setCantidad(existente.getCantidad() + req.getCantidad());
                    existente.setPrecioUnitario(req.getPrecioUnitario());
                    return repository.save(existente);
                })
                .orElseGet(() -> repository.save(new CarritoItem(
                        null,
                        correo,
                        req.getProductoId(),
                        req.getProductoNombre().trim(),
                        req.getPrecioUnitario(),
                        req.getCantidad(),
                        req.getImagenUrl() != null ? req.getImagenUrl().trim() : "")));
    }

    public CarritoItem actualizarCantidad(String correo, Long productoId, Integer cantidad) {
        CarritoItem item = repository.findByUsuarioCorreoIgnoreCaseAndProductoId(correo, productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no está en el carrito"));
        item.setCantidad(cantidad);
        return repository.save(item);
    }

    public void eliminarItem(String correo, Long productoId) {
        CarritoItem item = repository.findByUsuarioCorreoIgnoreCaseAndProductoId(correo, productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no está en el carrito"));
        repository.delete(item);
    }

    @Transactional
    public void vaciarCarrito(String correo) {
        repository.deleteByUsuarioCorreoIgnoreCase(correo);
    }
}