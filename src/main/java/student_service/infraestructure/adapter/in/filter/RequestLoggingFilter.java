package student_service.infraestructure.adapter.in.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import student_service.domain.util.logger.CanonicalLog;
import student_service.domain.util.logger.ConstantsLogs;
import student_service.domain.util.logger.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class RequestLoggingFilter implements WebFilter, LoggerFactory {

    private final ObjectMapper objectMapper;
    public static final String TRACE_ID = "Idempotency-Key";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String traceId = request.getHeaders().getFirst(TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        String uuid = traceId;
        exchange.getAttributes().put(TRACE_ID, uuid);

        // Read body
        return request.getBody()
                .reduce(new StringBuilder(), (sb, dataBuffer) -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    return sb.append(new String(bytes, StandardCharsets.UTF_8));
                })
                .defaultIfEmpty(new StringBuilder("{}"))
                .flatMap(body -> {
                    try {
                        CanonicalLog logIn = createLog(
                                uuid, request.getPath().value(), objectMapper.readTree(body.toString()), ConstantsLogs.IN);

                        log.info("{} -> {}", ConstantsLogs.PARAMETERS_IN,
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logIn));

                    } catch (Exception e) {
                        log.error("Error logging request body: {}", e.getMessage());
                    }

                    // Decorator with new request but same body to be passed down the chain
                    byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
                    ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(request) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                            return Flux.just(buffer);
                        }
                    };

                    return chain.filter(exchange.mutate().request(decoratedRequest).build())
                            .contextWrite(ctx -> ctx.put(TRACE_ID, uuid));
                });
    }
}

