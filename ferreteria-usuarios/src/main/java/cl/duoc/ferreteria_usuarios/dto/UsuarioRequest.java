package cl.duoc.ferreteria_usuarios.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

@Data
public class UsuarioRequest {

    @NotBlank(message = "El RUN es obligatorio")
    private String run;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo inválido")
    private String correo;

    @ToString.Exclude
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 10,
            message = "La contraseña debe tener entre 4 y 10 caracteres")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;
}