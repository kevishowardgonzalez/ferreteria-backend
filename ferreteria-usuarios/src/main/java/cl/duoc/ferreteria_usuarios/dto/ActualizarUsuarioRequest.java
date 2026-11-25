package cl.duoc.ferreteria_usuarios.dto;

import lombok.Data;

@Data
public class ActualizarUsuarioRequest {

    private String nombre;
    private String apellido;
    private String rol;
    private Boolean activo;
}