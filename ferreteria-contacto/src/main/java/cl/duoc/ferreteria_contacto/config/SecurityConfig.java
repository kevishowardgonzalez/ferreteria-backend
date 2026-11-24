package cl.duoc.ferreteria_contacto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.ferreteria_contacto.security.JwtAuthenticationFilter;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(
                    corsConfigurationSource()))

            .sessionManagement(session -> session
                    .sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS))

            .exceptionHandling(exception -> exception
        .authenticationEntryPoint(
                new HttpStatusEntryPoint(
                        HttpStatus.UNAUTHORIZED))
        .accessDeniedHandler((request, response, ex) ->
                response.setStatus(
                        HttpStatus.FORBIDDEN.value())))
                        

            .authorizeHttpRequests(auth -> auth

                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()

                    .requestMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**")
                    .permitAll()

                    .requestMatchers(
                            HttpMethod.POST, "/api/contactos")
                    .permitAll()

                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/contactos",
                            "/api/contactos/**")
                    .hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.PUT, "/api/contactos/**")
                    .hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.DELETE, "/api/contactos/**")
                    .hasRole("ADMIN")

                    .anyRequest().denyAll())

            .addFilterBefore(
                    jwtFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(
                List.of("http://localhost:5173"));

        config.setAllowedMethods(
                List.of("GET", "POST", "PUT",
                        "DELETE", "OPTIONS"));

        config.setAllowedHeaders(
                List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", config);

        return source;
    }
}