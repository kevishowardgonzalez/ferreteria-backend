package cl.duoc.ferreteria_auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth_refresh_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshSession {
    @Id
    @Column(name = "token_hash", length = 64)
    private String sessionId;

    @Column(length = 64)
    private String currentTokenHash;

    @ElementCollection
    @CollectionTable(name = "auth_refresh_consumed", joinColumns = @JoinColumn(name = "session_id"))
    @MapKeyColumn(name = "token_hash", length = 64)
    @Column(name = "consumed_at", nullable = false)
    private Map<String, Instant> consumedTokens = new HashMap<>();

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Instant expiresAt;

    public RefreshSession(String sessionId, String currentTokenHash, Long usuarioId, Instant expiresAt) {
        this.sessionId = sessionId;
        this.currentTokenHash = currentTokenHash;
        this.usuarioId = usuarioId;
        this.expiresAt = expiresAt;
    }

    public String hashVigente() {
        // Las sesiones anteriores usaban el hash del primer token como identificador.
        return currentTokenHash == null ? sessionId : currentTokenHash;
    }

    public void rotar(String nuevoHash, Instant ahora) {
        consumedTokens.put(hashVigente(), ahora);
        currentTokenHash = nuevoHash;
    }
}
