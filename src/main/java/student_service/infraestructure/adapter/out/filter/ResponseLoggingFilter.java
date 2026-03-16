package student_service.infraestructure.adapter.out.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import student_service.domain.util.logger.CanonicalLog;
import student_service.domain.util.logger.ConstantsLogs;
import student_service.domain.util.logger.LoggerFactory;
import student_service.infraestructure.adapter.in.filter.RequestLoggingFilter;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class ResponseLoggingFilter implements WebFilter, LoggerFactory {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                return Flux.from(body)
                        .collectList()
                        .flatMap(dataBuffers -> {
                            DataBuffer joined = originalResponse.bufferFactory()
                                    .join(dataBuffers);
                            byte[] content = new byte[joined.readableByteCount()];
                            joined.read(content);
                            DataBufferUtils.release(joined);

                            String responseBody = new String(content, StandardCharsets.UTF_8);
                            String traceId = exchange.getAttribute(RequestLoggingFilter.TRACE_ID);
                            String path = exchange.getRequest().getPath().value();
                            int statusCode = originalResponse.getStatusCode() != null
                                    ? originalResponse.getStatusCode().value() : 0;

                            logResponse(traceId, path, responseBody, statusCode);

                            DataBuffer buffer = originalResponse.bufferFactory().wrap(content);
                            return super.writeWith(Flux.just(buffer));
                        });
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(Flux::from));
            }

            @Override
            public Mono<Void> setComplete() {
                String traceId = exchange.getAttribute(RequestLoggingFilter.TRACE_ID);
                String path = exchange.getRequest().getPath().value();
                int statusCode = originalResponse.getStatusCode() != null
                        ? originalResponse.getStatusCode().value() : 0;

                logResponse(traceId, path, null, statusCode);

                return super.setComplete();
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private void logResponse(String traceId, String path, String responseBody, int statusCode) {
        try {
            Object bodyNode = (responseBody != null && !responseBody.isBlank())
                    ? objectMapper.readTree(responseBody)
                    : null;
            CanonicalLog logOut;

            if (statusCode >= 400) {
                String errorDescription = responseBody != null ? responseBody : "No response body";
                logOut = createLog(traceId, path, bodyNode, ConstantsLogs.OUT,
                        statusCode, ConstantsLogs.ERROR, errorDescription);
                log.error("{} -> {}", ConstantsLogs.PARAMETERS_OUT,
                        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logOut));
            } else {
                logOut = createLog(traceId, path, bodyNode, ConstantsLogs.OUT);
                logOut = logOut.toBuilder().responseCode(statusCode).build();
                log.info("{} -> {}", ConstantsLogs.PARAMETERS_OUT,
                        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logOut));
            }
        } catch (Exception e) {
            log.error("Error logging response body: {}", e.getMessage());
        }
    }
}
