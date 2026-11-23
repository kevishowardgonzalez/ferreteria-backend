package cl.duoc.ferreteria_foro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicacion_id", nullable = false)
    @JsonBackReference
    private Publicacion publicacion;

    @Column(nullable = false, length = 1000)
    private String contenido;

    @Column(name = "autor_correo", nullable = false)
    private String autorCorreo;

    @Column(name = "autor_nombre", nullable = false)
    private String autorNombre;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}