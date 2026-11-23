package cl.duoc.ferreteria_foro.repository;

import cl.duoc.ferreteria_foro.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findAllByOrderByFechaDesc();
}