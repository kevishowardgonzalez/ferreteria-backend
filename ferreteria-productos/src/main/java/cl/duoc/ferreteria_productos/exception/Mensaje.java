package cl.duoc.ferreteria_productos.exception;

import org.springframework.http.HttpStatus;

public class Mensaje extends RuntimeException {
    public final HttpStatus status;

    public Mensaje (HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}