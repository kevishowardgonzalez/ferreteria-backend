package cl.duoc.ferreteria_productos.controller;

import cl.duoc.ferreteria_productos.dto.ProductoRequest;
import cl.duoc.ferreteria_productos.model.Producto;
import cl.duoc.ferreteria_productos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catálogo de productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar productos", description = "Filtro opcional por nombre o categoría.")
    @GetMapping
    public ResponseEntity<List<Producto>> listar(@RequestParam(required = false) String filtro) {
        if (filtro != null && !filtro.isBlank()) {
            return ResponseEntity.ok(service.buscarPorFiltro(filtro));
        }
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Consultar un producto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @Operation(summary = "Crear producto (ADMIN)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRequest req) {
        Producto nuevoProducto = service.crear(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @Operation(summary = "Actualizar producto (ADMIN)")
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest req) {
        return ResponseEntity.ok(service.actualizar(id, req));
    }

    @Operation(summary = "Eliminar producto (ADMIN)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }
}
