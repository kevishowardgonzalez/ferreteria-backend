package cl.duoc.ferreteria_auth.repository;

import cl.duoc.ferreteria_auth.model.Usuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByRunIgnoreCase(String run);
    long countByRolAndActivoTrue(String rol);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Usuario u where u.rol = 'ADMIN' and u.activo = true order by u.id")
    List<Usuario> bloquearAdminsActivos();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Usuario> findForUpdateById(Long id);
}
