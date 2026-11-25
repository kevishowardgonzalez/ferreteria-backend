package cl.duoc.ferreteria_inventario.service;

import cl.duoc.ferreteria_inventario.client.ProductosClient;
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
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepo;
    private final MovimientoInventarioRepository movimientoRepo;
    private final ProductosClient productosClient;

    public InventarioService(InventarioRepository inventarioRepo, MovimientoInventarioRepository movimientoRepo,
                             ProductosClient productosClient) {
        this.inventarioRepo = inventarioRepo;
        this.movimientoRepo = movimientoRepo;
        this.productosClient = productosClient;
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
        String sku = req.getProductoSku().trim().toUpperCase(Locale.ROOT);
        if (inventarioRepo.existsByProductoSkuIgnoreCase(sku)) {
            throw new ReglaNegocioException("El SKU ya está registrado en inventario");
        }
        productosClient.validarVigencia(req.getProductoId());
        InventarioItem item = new InventarioItem(
                null,
                req.getProductoId(),
                sku,
                req.getProductoNombre().trim(),
                req.getStockActual(),
                req.getStockMinimo() != null ? req.getStockMinimo() : 5,
                LocalDateTime.now());
        try {
            return inventarioRepo.saveAndFlush(item);
        } catch (DataIntegrityViolationException error) {
            if (inventarioRepo.existsByProductoId(req.getProductoId())) {
                throw new ReglaNegocioException("El producto ya está registrado en inventario");
            }
            if (inventarioRepo.existsByProductoSkuIgnoreCase(sku)) {
                throw new ReglaNegocioException("El SKU ya está registrado en inventario");
            }
            throw error;
        }
    }

    @Transactional
    public InventarioItem registrarMovimiento(MovimientoRequest req) {
        InventarioItem item = inventarioRepo.findForUpdateByProductoId(req.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inventario no encontrado para producto: " + req.getProductoId()));
        String tipo = req.getTipo().trim().toUpperCase(Locale.ROOT);
        int cantidad = req.getCantidad();
        if (cantidad == 0 && ("ENTRADA".equals(tipo) || "SALIDA".equals(tipo))) {
            throw new ReglaNegocioException("Las entradas y salidas requieren una cantidad positiva");
        }

        switch (tipo) {
            case "ENTRADA" -> {
                if (cantidad > Integer.MAX_VALUE - item.getStockActual()) {
                    throw new ReglaNegocioException("El saldo supera el máximo permitido");
                }
                item.setStockActual(item.getStockActual() + cantidad);
            }
            case "SALIDA" -> {
                if (item.getStockActual() < cantidad) {
                    throw new ReglaNegocioException("Stock insuficiente. Disponible: " + item.getStockActual());
                }
                item.setStockActual(item.getStockActual() - cantidad);
            }
            case "AJUSTE" -> item.setStockActual(cantidad);
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
