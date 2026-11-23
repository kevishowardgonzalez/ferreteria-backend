package cl.duoc.ferreteria_roles.repository;

import cl.duoc.ferreteria_roles.model.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<RolEntity, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}