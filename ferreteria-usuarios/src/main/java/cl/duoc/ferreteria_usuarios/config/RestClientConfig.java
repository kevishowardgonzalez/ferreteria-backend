package cl.duoc.ferreteria_usuarios.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient authRestClient(
            @Value("${app.auth.base-url:http://localhost:8081}") String baseUrl,
            @Value("${app.auth.connect-timeout-ms:3000}") int connectTimeout,
            @Value("${app.auth.read-timeout-ms:5000}") int readTimeout) {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}