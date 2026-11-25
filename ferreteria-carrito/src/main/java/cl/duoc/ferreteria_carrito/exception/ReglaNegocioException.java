package cl.duoc.ferreteria_carrito.exception;

/** Se lanza cuando una operación viola una regla de negocio (correo duplicado, último admin, etc.). */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
