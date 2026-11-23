package cl.duoc.ferreteria_usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarUsuarioRequest {
    private String nombre;
    private String apellido;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    private Boolean activo;
}