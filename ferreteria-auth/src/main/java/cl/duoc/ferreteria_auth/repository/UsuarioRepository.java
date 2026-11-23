package cl.duoc.ferreteria_auth.repository;

import cl.duoc.ferreteria_auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByRunIgnoreCase(String run);
    long countByRolAndActivoTrue(String rol);
}
