package cl.duoc.ferreteria_carrito.controller;

import cl.duoc.ferreteria_carrito.dto.CarritoItemRequest;
import cl.duoc.ferreteria_carrito.model.CarritoItem;
import cl.duoc.ferreteria_carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carrito")
@Tag(name = "Carrito", description = "Carrito de compras persistente por usuario")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    @Operation(summary = "Ver carrito del usuario autenticado")
    @GetMapping
    public List<CarritoItem> verCarrito(Authentication auth) {
        return service.obtenerCarrito(auth.getName());
    }

    @Operation(summary = "Agregar producto al carrito")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarritoItem agregar(@Valid @RequestBody CarritoItemRequest request, Authentication auth) {
        return service.agregarOActualizar(auth.getName(), request);
    }

    @Operation(summary = "Actualizar cantidad de un producto en el carrito")
    @PutMapping("/{productoId}")
    public CarritoItem actualizarCantidad(@PathVariable Long productoId, @RequestParam Integer cantidad,
            Authentication auth) {
        return service.actualizarCantidad(auth.getName(), productoId, cantidad);
    }

    @Operation(summary = "Quitar producto del carrito")
    @DeleteMapping("/{productoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarItem(@PathVariable Long productoId, Authentication auth) {
        service.eliminarItem(auth.getName(), productoId);
    }

    @Operation(summary = "Vaciar carrito completo")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void vaciar(Authentication auth) {
        service.vaciarCarrito(auth.getName());
    }
}