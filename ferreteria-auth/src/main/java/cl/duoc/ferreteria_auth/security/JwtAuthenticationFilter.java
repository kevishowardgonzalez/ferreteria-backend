package cl.duoc.ferreteria_auth.security;

import cl.duoc.ferreteria_auth.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.filter.OncePerRequestFilter;

/** Guía 17, sección 12: lee el Bearer, valida el JWT y registra la identidad con su rol. */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UsuarioRepository usuarios;
    private final HandlerExceptionResolver errores;

    public JwtAuthenticationFilter(JwtService jwt, UsuarioRepository usuarios,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver errores) {
        this.jwt = jwt;
        this.usuarios = usuarios;
        this.errores = errores;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
    HttpServletResponse response, FilterChain chain)
    throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwt.validar(header.substring(7));
                JwtService.Identidad identidad = jwt.identidad(claims);
                String rol = claims.get("rol", String.class);
                var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
                if (request.getRequestURI().startsWith(request.getContextPath() + "/internal/")) {
                    var cuenta = usuarios.findById(identidad.id())
                            .filter(u -> identidad.correo().equals(u.getCorreo()) && Boolean.TRUE.equals(u.getActivo()))
                            .orElseThrow(() -> new JwtException("Cuenta JWT inválida"));
                    if ("ADMIN".equals(rol) && !"ADMIN".equals(cuenta.getRol())) {
                        autoridades = List.of();
                    }
                }
                var auth = new UsernamePasswordAuthenticationToken(identidad, null, autoridades);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            } catch (TransientDataAccessException | CannotGetJdbcConnectionException e) {
                errores.resolveException(request, response, null, e);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
