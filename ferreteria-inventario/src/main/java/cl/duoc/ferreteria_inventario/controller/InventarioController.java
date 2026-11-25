package cl.duoc.ferreteria_inventario.controller;

import cl.duoc.ferreteria_inventario.dto.InventarioRequest;
import cl.duoc.ferreteria_inventario.dto.MovimientoRequest;
import cl.duoc.ferreteria_inventario.model.InventarioItem;
import cl.duoc.ferreteria_inventario.model.MovimientoInventario;
import cl.duoc.ferreteria_inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@Tag(name = "Inventario", description = "Control de stock y movimientos de bodega")
public class InventarioController {

    private final InventarioService service;

    public InventarioController(InventarioService service) {
        this.service = service;
    }

    @Operation(summary = "Listar inventario completo (ADMIN o VENDEDOR)")
    @GetMapping
    public List<InventarioItem> listar() {
        return service.listar();
    }

    @Operation(summary = "Consultar stock de un producto")
    @GetMapping("/producto/{productoId}")
    public InventarioItem buscarPorProducto(@PathVariable Long productoId) {
        return service.buscarPorProducto(productoId);
    }

    @Operation(summary = "Historial de movimientos de un producto (ADMIN)")
    @GetMapping("/producto/{productoId}/movimientos")
    public List<MovimientoInventario> listarMovimientos(@PathVariable Long productoId) {
        return service.listarMovimientos(productoId);
    }

    @Operation(summary = "Registrar ficha de inventario (ADMIN)")
    @PostMapping
    public ResponseEntity<InventarioItem> registrarProducto(@Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarProducto(request));
    }

    @Operation(summary = "Registrar entrada, salida o ajuste de stock (ADMIN)",
            description = "ENTRADA y SALIDA requieren cantidad positiva. AJUSTE fija un saldo absoluto y admite cero.")
    @PostMapping("/movimientos")
    public InventarioItem registrarMovimiento(@Valid @RequestBody MovimientoRequest request) {
        return service.registrarMovimiento(request);
    }
}