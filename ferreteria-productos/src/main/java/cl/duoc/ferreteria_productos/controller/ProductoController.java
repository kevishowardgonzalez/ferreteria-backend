package cl.duoc.ferreteria_productos.controller;

import cl.duoc.ferreteria_productos.dto.ProductoRequest;
import cl.duoc.ferreteria_productos.model.Producto;
import cl.duoc.ferreteria_productos.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // LISTAR TODOS O BUSCAR POR FILTRO (GET /api/productos o GET /api/productos?filtro=martillo)
    @GetMapping
    public ResponseEntity<List<Producto>> listar(@RequestParam(required = false) String filtro) {
        if (filtro != null && !filtro.isBlank()) {
            return ResponseEntity.ok(service.buscarPorFiltro(filtro));
        }
        return ResponseEntity.ok(service.listar());
    }

    // BUSCAR UN PRODUCTO POR ID (GET /api/productos/1)
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    // CREAR UN PRODUCTO NUEVO (POST /api/productos) - Protegido por JWT
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRequest req) {
        Producto nuevoProducto = service.crear(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    // ACTUALIZAR UN PRODUCTO EXISTENTE (PUT /api/productos/1) - Protegido por JWT
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest req) {
        return ResponseEntity.ok(service.actualizar(id, req));
    }

    // ELIMINAR UN PRODUCTO (DELETE /api/productos/1) - Protegido por JWT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }
}