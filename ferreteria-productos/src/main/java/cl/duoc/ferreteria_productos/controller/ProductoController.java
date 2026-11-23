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
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Catálogo de productos de Ferretería Los Maestros")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los productos")
    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    @Operation(summary = "Listar productos activos (catálogo público)")
    @GetMapping("/activos")
    public List<Producto> listarActivos() {
        return service.listarActivos();
    }

    @Operation(summary = "Filtrar productos activos por categoría")
    @GetMapping("/categoria/{categoriaId}")
    public List<Producto> listarPorCategoria(@PathVariable Long categoriaId) {
        return service.listarPorCategoria(categoriaId);
    }

    @Operation(summary = "Buscar productos por nombre")
    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre);
    }

    @Operation(summary = "Obtener producto por ID")
    @GetMapping("/{id}")
    public Producto buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(summary = "Crear producto (ADMIN o VENDEDOR)")
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @Operation(summary = "Actualizar producto (ADMIN o VENDEDOR)")
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Desactivar producto (borrado lógico)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        service.desactivar(id);
    }
}