package cl.duoc.ferreteria_contacto.repository;
import cl.duoc.ferreteria_contacto.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Long> {

}