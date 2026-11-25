package cl.duoc.ferreteria_auth.service;

import cl.duoc.ferreteria_auth.exception.RenovacionDuplicadaException;
import cl.duoc.ferreteria_auth.model.RefreshSession;
import cl.duoc.ferreteria_auth.repository.RefreshSessionRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class RefreshSessionService {
    private static final long DUPLICATE_GRACE_SECONDS = 5;
    private final SecureRandom random = new SecureRandom();
    private final RefreshSessionRepository repository;
    private final long expirationSeconds;

    public RefreshSessionService(RefreshSessionRepository repository,
                                 @Value("${app.jwt.refresh-expiration-seconds:604800}") long expirationSeconds) {
        if (expirationSeconds <= 0) {
            throw new IllegalArgumentException("JWT_REFRESH_TTL_SECONDS debe ser positivo.");
        }
        this.repository = repository;
        this.expirationSeconds = expirationSeconds;
    }

    String emitir(Long usuarioId) {
        String sessionId = hash(credencialAleatoria());
        String token = nuevoToken(sessionId);
        repository.save(new RefreshSession(sessionId, hash(token), usuarioId,
                Instant.now().plusSeconds(expirationSeconds)));
        return token;
    }

    RefreshSession obtenerVigente(String token) {
        return buscarAutenticada(token)
                .filter(s -> s.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new BadCredentialsException("Renovacion invalida"));
    }

    String rotar(RefreshSession session, String token) {
        Instant ahora = Instant.now();
        String tokenHash = hash(token);
        if (!session.hashVigente().equals(tokenHash)) {
            Instant consumido = session.getConsumedTokens().get(tokenHash);
            if (consumido.plusSeconds(DUPLICATE_GRACE_SECONDS).isAfter(ahora)) {
                throw new RenovacionDuplicadaException();
            }
            revocar(session);
            throw new BadCredentialsException("Credencial reutilizada");
        }
        String siguiente = nuevoToken(session.getSessionId());
        session.rotar(hash(siguiente), ahora);
        return siguiente;
    }

    void revocar(String token) {
        buscarAutenticada(token).ifPresent(this::revocar);
    }

    void revocar(RefreshSession session) {
        repository.delete(session);
    }

    private Optional<RefreshSession> buscarAutenticada(String token) {
        String tokenHash = hash(token);
        String sessionId;
        if (token.matches("[a-f0-9]{64}\\.[A-Za-z0-9_-]{43}")) {
            sessionId = token.substring(0, 64);
        } else if (token.matches("[A-Za-z0-9_-]{43}")) {
            sessionId = tokenHash;
        } else {
            return Optional.empty();
        }
        // Tanto la rotación como el cierre bloquean la fila estable antes de leer sus hashes.
        return repository.findById(sessionId).filter(session ->
                session.hashVigente().equals(tokenHash) || session.getConsumedTokens().containsKey(tokenHash));
    }

    private String nuevoToken(String sessionId) {
        return sessionId + "." + credencialAleatoria();
    }

    private String credencialAleatoria() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible.", e);
        }
    }
}
