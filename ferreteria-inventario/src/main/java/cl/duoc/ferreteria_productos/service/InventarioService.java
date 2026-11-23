package cl.duoc.ferreteria_inventario.service;

import cl.duoc.ferreteria_inventario.dto.InventarioRequest;
import cl.duoc.ferreteria_inventario.dto.MovimientoRequest;
import cl.duoc.ferreteria_inventario.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_inventario.exception.ReglaNegocioException;
import cl.duoc.ferreteria_inventario.model.InventarioItem;
import cl.duoc.ferreteria_inventario.model.MovimientoInventario;
import cl.duoc.ferreteria_inventario.repository.InventarioRepository;
import cl.duoc.ferreteria_inventario.repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepo;
    private final MovimientoInventarioRepository movimientoRepo;

    public InventarioService(InventarioRepository inventarioRepo, MovimientoInventarioRepository movimientoRepo) {
        this.inventarioRepo = inventarioRepo;
        this.movimientoRepo = movimientoRepo;
    }

    public List<InventarioItem> listar() {
        return inventarioRepo.findAll();
    }

    public InventarioItem buscarPorProducto(Long productoId) {
        return inventarioRepo.findByProductoId(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inventario no encontrado para producto: " + productoId));
    }

    public List<MovimientoInventario> listarMovimientos(Long productoId) {
        return movimientoRepo.findByProductoIdOrderByFechaDesc(productoId);
    }

    public InventarioItem registrarProducto(InventarioRequest req) {
        if (inventarioRepo.existsByProductoId(req.getProductoId())) {
            throw new ReglaNegocioException("El producto ya está registrado en inventario");
        }
        InventarioItem item = new InventarioItem(
                null,
                req.getProductoId(),
                req.getProductoSku().trim().toUpperCase(),
                req.getProductoNombre().trim(),
                req.getStockActual(),
                req.getStockMinimo() != null ? req.getStockMinimo() : 5,
                LocalDateTime.now());
        return inventarioRepo.save(item);
    }

    @Transactional
    public InventarioItem registrarMovimiento(MovimientoRequest req) {
        InventarioItem item = buscarPorProducto(req.getProductoId());
        String tipo = req.getTipo().trim().toUpperCase();

        switch (tipo) {
            case "ENTRADA" -> item.setStockActual(item.getStockActual() + req.getCantidad());
            case "SALIDA" -> {
                if (item.getStockActual() < req.getCantidad()) {
                    throw new ReglaNegocioException("Stock insuficiente. Disponible: " + item.getStockActual());
                }
                item.setStockActual(item.getStockActual() - req.getCantidad());
            }
            case "AJUSTE" -> item.setStockActual(req.getCantidad());
            default -> throw new ReglaNegocioException("Tipo inválido: use ENTRADA, SALIDA o AJUSTE");
        }

        item.setUltimaActualizacion(LocalDateTime.now());
        movimientoRepo.save(new MovimientoInventario(
                null,
                req.getProductoId(),
                tipo,
                req.getCantidad(),
                req.getMotivo() != null ? req.getMotivo().trim() : "",
                LocalDateTime.now()));
        return inventarioRepo.save(item);
    }
}