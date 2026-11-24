package cl.duoc.ferreteria_auth.security;

import cl.duoc.ferreteria_auth.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    private SecretKey clave() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(usuario.getCorreo())
                .issuer(issuer)
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(clave())
                .compact();
    }

    public Claims validar(String token) {
        return Jwts.parser()
                .verifyWith(clave())
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
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