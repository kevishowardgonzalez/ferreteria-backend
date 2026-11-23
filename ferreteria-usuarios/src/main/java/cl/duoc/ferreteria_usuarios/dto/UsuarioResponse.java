package cl.duoc.ferreteria_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String run;
    private String nombre;
    private String apellido;
    private String correo;
    private String rol;
    private Boolean activo;
}