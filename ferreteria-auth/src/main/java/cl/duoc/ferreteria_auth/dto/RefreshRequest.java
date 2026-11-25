package cl.duoc.ferreteria_auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {

    @NotBlank(message = "El token es obligatorio")
    private String refreshToken;
}