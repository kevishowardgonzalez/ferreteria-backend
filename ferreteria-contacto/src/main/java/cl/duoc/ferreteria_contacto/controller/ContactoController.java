package cl.duoc.ferreteria_contacto.controller;

// ¡Estos son los imports correctos!
import cl.duoc.ferreteria_contacto.service.ContactoService;
import cl.duoc.ferreteria_contacto.dto.ContactoRequest;
import cl.duoc.ferreteria_contacto.dto.ContactoResponse;

import jakarta.validation.Valid;


import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/api/contactos")
public class ContactoController {

    private final ContactoService service;

    public ContactoController(ContactoService service) {
        this.service = service;
    }

    // LISTAR CONTACTOS
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<ContactoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // BUSCAR CONTACTO POR ID
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<ContactoResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // CREAR CONTACTO
    @PostMapping
    public ResponseEntity<ContactoResponse> crear(
            @Valid @RequestBody ContactoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.crear(request));
    }

    // ACTUALIZAR CONTACTO
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<ContactoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ContactoRequest request) {

        return ResponseEntity.ok(service.actualizar(id, request));
    }

    // ELIMINAR CONTACTO
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
