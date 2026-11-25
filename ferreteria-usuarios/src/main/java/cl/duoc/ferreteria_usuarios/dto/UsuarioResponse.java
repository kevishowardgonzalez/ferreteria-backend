package cl.duoc.ferreteria_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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