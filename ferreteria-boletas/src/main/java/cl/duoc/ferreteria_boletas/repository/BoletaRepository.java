package cl.duoc.ferreteria_boletas.repository;

import cl.duoc.ferreteria_boletas.model.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {
    List<Boleta> findByUsuarioCorreoIgnoreCaseOrderByFechaDesc(String usuarioCorreo);
}