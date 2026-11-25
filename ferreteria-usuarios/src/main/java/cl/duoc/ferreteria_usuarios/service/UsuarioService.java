package cl.duoc.ferreteria_usuarios.service;

import cl.duoc.ferreteria_usuarios.client.AuthClient;
import cl.duoc.ferreteria_usuarios.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final AuthClient authClient;

    public UsuarioService(AuthClient authClient) {
        this.authClient = authClient;
    }

    public List<UsuarioResponse> listar(String authorization) {
        return authClient.listar(authorization);
    }

    public UsuarioResponse buscar(
            Long id, String authorization) {

        return authClient.buscar(id, authorization);
    }

    public UsuarioResponse crear(
            UsuarioRequest request, String authorization) {

        return authClient.crear(request, authorization);
    }

    public UsuarioResponse actualizar(
            Long id,
            ActualizarUsuarioRequest request,
            String authorization) {

        return authClient.actualizar(id, request, authorization);
    }

    public void desactivar(Long id, String authorization) {
        authClient.desactivar(id, authorization);
    }

    public UsuarioResponse obtenerPerfil(String authorization) {
        return authClient.obtenerPerfil(authorization);
    }

    public UsuarioResponse actualizarPerfil(
            ActualizarPerfilRequest request,
            String authorization) {

        return authClient.actualizarPerfil(request, authorization);
    }
}