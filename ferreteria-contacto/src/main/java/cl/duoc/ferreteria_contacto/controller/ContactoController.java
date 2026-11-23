package cl.duoc.ferreteria_contacto.controller;

import cl.duoc.ferreteria_contacto.dto.ContactoRequest;
import cl.duoc.ferreteria_contacto.model.MensajeContacto;
import cl.duoc.ferreteria_contacto.service.ContactoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contacto")
@Tag(name = "Contacto", description = "Formulario de consultas y soporte al cliente")
public class ContactoController {

    private final ContactoService service;

    public ContactoController(ContactoService service) {
        this.service = service;
    }

    @Operation(summary = "Enviar mensaje de contacto (Público)")
    @PostMapping
    public ResponseEntity<MensajeContacto> enviar(@Valid @RequestBody ContactoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviar(request));
    }

    @Operation(summary = "Listar mensajes recibidos (ADMIN o VENDEDOR)")
    @GetMapping
    public List<MensajeContacto> listar() {
        return service.listar();
    }

    @Operation(summary = "Marcar mensaje como respondido (ADMIN o VENDEDOR)")
    @PutMapping("/{id}/responder")
    public MensajeContacto marcarRespondido(@PathVariable Long id) {
        return service.marcarRespondido(id);
    }

    @Operation(summary = "Eliminar mensaje (ADMIN)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}