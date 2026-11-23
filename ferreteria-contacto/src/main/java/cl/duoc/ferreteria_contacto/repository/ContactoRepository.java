package cl.duoc.ferreteria_contacto.repository;

import cl.duoc.ferreteria_contacto.model.MensajeContacto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactoRepository extends JpaRepository<MensajeContacto, Long> {
    List<MensajeContacto> findAllByOrderByFechaDesc();
}