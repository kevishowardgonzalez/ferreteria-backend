package cl.duoc.ferreteria_boletas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DetalleBoletaRequest {
    @NotNull(message = "El ID de producto es obligatorio")
    private Long productoId;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String productoNombre;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Integer precioUnitario;
}