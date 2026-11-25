package cl.duoc.ferreteria_boletas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final SecretKey key;
    private final String issuer;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.issuer}") String issuer) {
        try {
            this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Configure JWT_SECRET en Base64 con al menos 256 bits.");
        }
        if (issuer.isBlank()) {
            throw new IllegalArgumentException("Configure un emisor JWT no vacio.");
        }
        this.issuer = issuer;
    }

    public Claims validar(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (claims.getSubject() == null || claims.getSubject().isBlank() || claims.getExpiration() == null) {
            throw new JwtException("JWT sin identidad o vencimiento.");
        }
        String rol = claims.get("rol", String.class);
        if (!"ADMIN".equals(rol) && !"VENDEDOR".equals(rol) && !"CLIENTE".equals(rol)) {
            throw new JwtException("Rol JWT invalido.");
        }
        return claims;
    }

    public boolean esValido(String token) {
        try {
            validar(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}