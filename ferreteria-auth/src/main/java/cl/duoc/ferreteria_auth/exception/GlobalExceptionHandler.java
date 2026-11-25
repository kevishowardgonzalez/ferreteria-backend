package cl.duoc.ferreteria_auth.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RecursoNoEncontradoException.class)
  ResponseEntity<Map<String, String>> noEncontrado(RecursoNoEncontradoException e) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(Map.of("error", e.getMessage()));
  }

  @ExceptionHandler(ReglaNegocioException.class)
  ResponseEntity<Map<String, String>> reglaIncumplida(ReglaNegocioException e) {
    return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  ResponseEntity<Map<String, String>> credencialesInvalidas() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
      .body(Map.of("error", "Credenciales inválidas"));
  }

  @ExceptionHandler(RenovacionDuplicadaException.class)
  ResponseEntity<Map<String, String>> renovacionDuplicada() {
    return ResponseEntity.status(HttpStatus.CONFLICT)
      .body(Map.of("error", "La sesión ya se renovó con esta credencial."));
  }

  // Token alterado, vencido o de otro emisor al intentar renovarlo
  @ExceptionHandler(JwtException.class)
  ResponseEntity<Map<String, String>> tokenInvalido() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
      .body(Map.of("error", "Token inválido o expirado"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, String>> datosInvalidos(MethodArgumentNotValidException e) {
    String mensaje = e.getBindingResult().getFieldErrors().stream()
      .findFirst().map(error -> error.getField() + ": " + error.getDefaultMessage())
      .orElse("Datos inválidos");
    return ResponseEntity.badRequest().body(Map.of("error", mensaje));
  }
}
