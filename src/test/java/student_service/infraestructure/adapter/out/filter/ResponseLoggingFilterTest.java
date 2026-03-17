package student_service.infraestructure.adapter.out.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import java.nio.charset.StandardCharsets;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResponseLoggingFilterTest {

    @Test
    @DisplayName("Debe ejecutar writeAndFlushWith para cubrir streaming response")
    void debeCubrirWriteAndFlushWith() {
        WebTestClient client = WebTestClient
                .bindToWebHandler(exchange -> {
                    byte[] bytes = "stream-data".getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = exchange.getResponse()
                            .bufferFactory()
                            .wrap(bytes);

                    return exchange.getResponse()
                            .writeAndFlushWith(Flux.just(Flux.just(buffer)));
                })
                .webFilter(new ResponseLoggingFilter(new ObjectMapper()))
                .build();

        client.get()
                .uri("/test-stream")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Debe ejecutar setComplete cuando la respuesta no tiene body")
    void debeCubrirSetComplete() {
        WebTestClient client = WebTestClient
                .bindToWebHandler(exchange -> exchange.getResponse().setComplete())
                .webFilter(new ResponseLoggingFilter(new ObjectMapper()))
                .build();

        client.get()
                .uri("/test-empty")
                .exchange()
                .expectStatus().isOk();
    }
}
