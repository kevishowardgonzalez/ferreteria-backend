package cl.duoc.ferreteria_roles.repository;

import cl.duoc.ferreteria_roles.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
