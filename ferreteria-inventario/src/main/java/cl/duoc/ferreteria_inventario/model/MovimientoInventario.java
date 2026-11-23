package cl.duoc.ferreteria_inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false, length = 20)
    private String tipo; // ENTRADA, SALIDA, AJUSTE

    @Column(nullable = false)
    private Integer cantidad;

    @Column(length = 255)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}