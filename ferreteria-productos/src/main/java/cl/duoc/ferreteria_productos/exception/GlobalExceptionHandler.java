package cl.duoc.ferreteria_productos.exception;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  
  @ExceptionHandler(Mensaje.class)
  public ResponseEntity<?> api(Mensaje e) {
    return ResponseEntity.status(e.status).body(Map.of("mensaje", e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> validation(MethodArgumentNotValidException e) {
    var errors = new java.util.LinkedHashMap<String, String>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(f -> errors.put(f.getField(), f.getDefaultMessage()));
    return ResponseEntity.badRequest()
        .body(Map.of("mensaje", "Revisa los datos ingresados.", "errores", errors));
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<?> format(Exception e) {
    return ResponseEntity.badRequest().body(Map.of("mensaje", "Formato de datos inválido."));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> conflict(Exception e) {
    return ResponseEntity.status(409)
        .body(Map.of("mensaje", "El registro ya existe o está relacionado con otros datos."));
  }
}