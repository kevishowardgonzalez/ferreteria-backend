package cl.duoc.ferreteria_boletas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BoletaRequest {
    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String usuarioNombre;

    @NotEmpty(message = "La boleta debe tener al menos un producto")
    @Valid
    private List<DetalleBoletaRequest> detalles;
}