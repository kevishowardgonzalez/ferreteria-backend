package cl.duoc.ferreteria_inventario.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProductosClient {
    private final RestClient cliente;

    public ProductosClient(@Value("${app.productos.api-url}") String url) {
        SimpleClientHttpRequestFactory solicitudes = new SimpleClientHttpRequestFactory();
        solicitudes.setConnectTimeout(Duration.ofSeconds(2));
        solicitudes.setReadTimeout(Duration.ofSeconds(3));
        this.cliente = RestClient.builder().baseUrl(url).requestFactory(solicitudes).build();
    }

    public void validarVigencia(Long productoId) {
        ProductoCatalogo producto;
        try {
            producto = cliente.get().uri("/api/productos/{id}", productoId)
                    .retrieve().body(ProductoCatalogo.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe en el catálogo");
            }
            if (e.getStatusCode().is5xxServerError()) {
                throw productosNoDisponible();
            }
            throw respuestaInvalida();
        } catch (ResourceAccessException e) {
            throw productosNoDisponible();
        } catch (RestClientException e) {
            throw respuestaInvalida();
        }

        if (producto == null || !productoId.equals(producto.id())) {
            throw respuestaInvalida();
        }
        if (Boolean.TRUE.equals(producto.retirado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El producto está retirado del catálogo");
        }
    }

    private ResponseStatusException productosNoDisponible() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo verificar el producto");
    }

    private ResponseStatusException respuestaInvalida() {
        return new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Productos devolvió una respuesta inválida");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ProductoCatalogo(Long id, @JsonProperty(required = true) Boolean retirado) {}
}
