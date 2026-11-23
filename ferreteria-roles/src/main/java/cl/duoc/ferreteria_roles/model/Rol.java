package cl.duoc.ferreteria_roles.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String nombre; // ADMIN, VENDEDOR, CLIENTE

    @Column(nullable = false)
    private String descripcion;

    private Boolean activo = true;
}