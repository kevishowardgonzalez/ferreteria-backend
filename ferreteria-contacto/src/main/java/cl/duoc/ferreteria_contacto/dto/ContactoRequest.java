package cl.duoc.ferreteria_contacto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactoRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100)
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Ingresa un correo válido")
        @Size(max = 254)
        String correo,

        @NotBlank(message = "El asunto es obligatorio")
        @Size(min = 4, max = 150)
        String asunto,

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(min = 10, max = 2000)
        String mensaje

) {}