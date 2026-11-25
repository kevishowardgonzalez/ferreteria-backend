package cl.duoc.ferreteria_inventario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MovimientoRequest {
    @NotNull(message = "El ID de producto es obligatorio")
    @Positive
    private Long productoId;

    @NotBlank(message = "El tipo de movimiento es obligatorio (ENTRADA, SALIDA, AJUSTE)")
    private String tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    @Size(max = 255)
    private String motivo;
}
