package cl.duoc.ferreteria_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String rol;
}