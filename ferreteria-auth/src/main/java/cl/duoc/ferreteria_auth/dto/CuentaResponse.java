package cl.duoc.ferreteria_auth.dto;

public record CuentaResponse(Long id, String run, String nombre, String apellido,
                             String correo, String rol, Boolean activo) {
}
