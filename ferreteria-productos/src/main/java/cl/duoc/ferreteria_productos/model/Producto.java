package cl.duoc.ferreteria_productos.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
public class Producto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 50)
  private String categoria;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal precio;

  @Column(nullable = false)
  private Integer stock;

  // Las filas previas sin este valor se consideran vigentes.
  private Boolean retirado;

  @Column(length = 1000)
  private String descripcion;
}