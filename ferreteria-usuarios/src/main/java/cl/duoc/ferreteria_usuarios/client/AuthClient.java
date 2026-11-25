package cl.duoc.ferreteria_usuarios.client;

import cl.duoc.ferreteria_usuarios.dto.*;
import cl.duoc.ferreteria_usuarios.exception.RespuestaAuthInvalidaException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AuthClient {

    private static final String CUENTAS = "/internal/v1/cuentas";

    private final RestClient restClient;

    public AuthClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<UsuarioResponse> listar(String authorization) {

        ResponseEntity<List<UsuarioResponse>> respuesta = restClient.get()
                .uri(CUENTAS)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<UsuarioResponse>>() {});

        validarEstado(respuesta, HttpStatus.OK);

        List<UsuarioResponse> usuarios = respuesta.getBody();

        if (usuarios == null) {
            throw new RespuestaAuthInvalidaException(
                    "Auth devolvió una lista de usuarios inválida");
        }

        usuarios.forEach(this::validarUsuario);

        return usuarios;
    }

    public UsuarioResponse buscar(Long id, String authorization) {

        ResponseEntity<UsuarioResponse> respuesta = restClient.get()
                .uri(CUENTAS + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve()
                .toEntity(UsuarioResponse.class);

        UsuarioResponse usuario = obtenerUsuario(respuesta, HttpStatus.OK);

        validarId(usuario, id);

        return usuario;
    }

    public UsuarioResponse crear(
            UsuarioRequest request,
            String authorization) {

        ResponseEntity<UsuarioResponse> respuesta = restClient.post()
                .uri(CUENTAS)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(UsuarioResponse.class);

        return obtenerUsuario(respuesta, HttpStatus.CREATED);
    }

    public UsuarioResponse actualizar(
            Long id,
            ActualizarUsuarioRequest request,
            String authorization) {

        ResponseEntity<UsuarioResponse> respuesta = restClient.put()
                .uri(CUENTAS + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(UsuarioResponse.class);

        UsuarioResponse usuario = obtenerUsuario(respuesta, HttpStatus.OK);

        validarId(usuario, id);

        return usuario;
    }

    public void desactivar(Long id, String authorization) {

        ResponseEntity<Void> respuesta = restClient.delete()
                .uri(CUENTAS + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve()
                .toBodilessEntity();

        validarEstado(respuesta, HttpStatus.NO_CONTENT);
    }

    public UsuarioResponse obtenerPerfil(String authorization) {

        ResponseEntity<UsuarioResponse> respuesta = restClient.get()
                .uri(CUENTAS + "/me")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve()
                .toEntity(UsuarioResponse.class);

        return obtenerUsuario(respuesta, HttpStatus.OK);
    }

    public UsuarioResponse actualizarPerfil(
            ActualizarPerfilRequest request,
            String authorization) {

        ResponseEntity<UsuarioResponse> respuesta = restClient.put()
                .uri(CUENTAS + "/me")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(UsuarioResponse.class);

        return obtenerUsuario(respuesta, HttpStatus.OK);
    }

    private void validarEstado(
            ResponseEntity<?> respuesta,
            HttpStatus estadoEsperado) {

        if (!respuesta.getStatusCode().equals(estadoEsperado)) {
            throw new RespuestaAuthInvalidaException(
                    "Auth devolvió un estado HTTP inesperado");
        }
    }

    private UsuarioResponse obtenerUsuario(
            ResponseEntity<UsuarioResponse> respuesta,
            HttpStatus estadoEsperado) {

        validarEstado(respuesta, estadoEsperado);

        UsuarioResponse usuario = respuesta.getBody();

        validarUsuario(usuario);

        return usuario;
    }

    private void validarUsuario(UsuarioResponse usuario) {

        if (usuario == null
                || usuario.getId() == null
                || usuario.getId() <= 0
                || textoInvalido(usuario.getRun())
                || textoInvalido(usuario.getNombre())
                || textoInvalido(usuario.getApellido())
                || textoInvalido(usuario.getCorreo())
                || usuario.getActivo() == null
                || !rolValido(usuario.getRol())) {

            throw new RespuestaAuthInvalidaException(
                    "Auth devolvió datos de usuario incompletos o inválidos");
        }
    }

    private void validarId(UsuarioResponse usuario, Long id) {

        if (!usuario.getId().equals(id)) {
            throw new RespuestaAuthInvalidaException(
                    "Auth devolvió un usuario diferente al solicitado");
        }
    }

    private boolean textoInvalido(String valor) {
        return valor == null || valor.isBlank();
    }

    private boolean rolValido(String rol) {

        return "ADMIN".equals(rol)
                || "VENDEDOR".equals(rol)
                || "CLIENTE".equals(rol);
    }
}