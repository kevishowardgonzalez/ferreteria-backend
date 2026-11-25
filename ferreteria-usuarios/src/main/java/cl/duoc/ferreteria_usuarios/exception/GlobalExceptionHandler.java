package cl.duoc.ferreteria_usuarios.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(
            RecursoNoEncontradoException e) {

        return respuesta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<Map<String, String>> reglaIncumplida(
            DatosInvalidosException e) {

        return respuesta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos(
            MethodArgumentNotValidException e) {

        String mensaje = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": "
                        + error.getDefaultMessage())
                .orElse("Datos inválidos");

        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> solicitudInvalida(
            HttpMessageNotReadableException e) {

        return respuesta(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida o contiene campos no permitidos");
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<Map<String, String>> errorAuth(
            RestClientResponseException e) {

        return switch (e.getStatusCode().value()) {

            case 400 -> respuesta(
                    HttpStatus.BAD_REQUEST,
                    "Datos inválidos o regla de negocio incumplida");

            case 401 -> respuesta(
                    HttpStatus.UNAUTHORIZED,
                    "Sesión inválida o cuenta desactivada");

            case 403 -> respuesta(
                    HttpStatus.FORBIDDEN,
                    "No tienes permisos para realizar esta operación");

            case 404 -> respuesta(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado");

            case 503 -> respuesta(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio de autenticación no está disponible");

            default -> respuesta(
                    HttpStatus.BAD_GATEWAY,
                    "Respuesta inesperada del servicio de autenticación");
        };
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, String>> authNoDisponible(
            ResourceAccessException e) {

        return respuesta(
                HttpStatus.SERVICE_UNAVAILABLE,
                "No fue posible establecer comunicación con Auth");
    }

    @ExceptionHandler(RespuestaAuthInvalidaException.class)
    public ResponseEntity<Map<String, String>> respuestaAuthInvalida(
            RespuestaAuthInvalidaException e) {

        return respuesta(HttpStatus.BAD_GATEWAY, e.getMessage());
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Map<String, String>> errorComunicacion(
            RestClientException e) {

        return respuesta(
                HttpStatus.BAD_GATEWAY,
                "Error al procesar la respuesta de Auth");
    }

    private ResponseEntity<Map<String, String>> respuesta(
            HttpStatus estado, String mensaje) {

        return ResponseEntity.status(estado)
                .body(Map.of("error", mensaje));
    }
}