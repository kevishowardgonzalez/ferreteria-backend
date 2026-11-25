package cl.duoc.ferreteria_auth.repository;

import cl.duoc.ferreteria_auth.model.RefreshSession;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, String> {
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshSession> findById(String sessionId);
}
