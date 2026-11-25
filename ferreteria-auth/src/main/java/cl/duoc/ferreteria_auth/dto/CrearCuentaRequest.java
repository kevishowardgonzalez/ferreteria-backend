package cl.duoc.ferreteria_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearCuentaRequest(
        @NotBlank String run,
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank @Email String correo,
        @NotBlank @Size(min = 4, max = 10) String password,
        @NotBlank String rol) {
    @Override
    public String toString() {
        return "CrearCuentaRequest";
    }
}
