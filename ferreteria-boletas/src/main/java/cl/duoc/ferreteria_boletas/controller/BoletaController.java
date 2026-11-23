package cl.duoc.ferreteria_boletas.controller;

import cl.duoc.ferreteria_boletas.dto.BoletaRequest;
import cl.duoc.ferreteria_boletas.model.Boleta;
import cl.duoc.ferreteria_boletas.service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boletas")
@Tag(name = "Boletas", description = "Emisión y consulta de comprobantes de venta")
public class BoletaController {

    private final BoletaService service;

    public BoletaController(BoletaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todas las boletas (ADMIN o VENDEDOR)")
    @GetMapping
    public List<Boleta> listarTodas() {
        return service.listarTodas();
    }

    @Operation(summary = "Historial de boletas del usuario autenticado")
    @GetMapping("/mis-boletas")
    public List<Boleta> misBoletas(Authentication auth) {
        return service.listarPorUsuario(auth.getName());
    }

    @Operation(summary = "Obtener boleta por ID")
    @GetMapping("/{id}")
    public Boleta buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(summary = "Emitir nueva boleta")
    @PostMapping
    public ResponseEntity<Boleta> emitir(@Valid @RequestBody BoletaRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.emitir(auth.getName(), request));
    }

    @Operation(summary = "Anular boleta (ADMIN o VENDEDOR)")
    @PutMapping("/{id}/anular")
    public Boleta anular(@PathVariable Long id) {
        return service.anular(id);
    }
}