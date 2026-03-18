package student_service.config;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ObjectSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    private static final String APPLICATION_JSON = "application/json";
    private static final String STATUS = "status";
    private static final String UP = "UP";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Scotiabank Reactive Challenge API")
                        .version("1.0")
                .description("Reactive API built with Spring WebFlux"))
            .addTagsItem(new Tag()
                .name("Actuator")
                .description("Actuator Healtcheck (Solo para Demo Pues no es funcional del negocio)"));
    }

    @Bean
    public OpenApiCustomizer actuatorHealthExampleCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }

            PathItem actuatorHealthPath = openApi.getPaths().get("/actuator/health");
            if (actuatorHealthPath == null || actuatorHealthPath.getGet() == null
                    || actuatorHealthPath.getGet().getResponses() == null) {
                return;
            }

            io.swagger.v3.oas.models.responses.ApiResponse okResponse = actuatorHealthPath.getGet().getResponses().get("200");
            if (okResponse == null) {
                return;
            }

            Content content = okResponse.getContent();
            if (content == null) {
                content = new Content();
                okResponse.setContent(content);
            }

            MediaType jsonContent = content.get(APPLICATION_JSON);
            if (jsonContent == null) {
                jsonContent = new MediaType();
                content.addMediaType(APPLICATION_JSON, jsonContent);
            }

            jsonContent.setSchema(new ObjectSchema().additionalProperties(true));
            jsonContent.setExample(Map.of(
                    STATUS, UP,
                    "components", Map.of(
                            "diskSpace", Map.of(STATUS, UP),
                            "ping", Map.of(STATUS, UP),
                            "r2dbc", Map.of(STATUS, UP)
                    )
            ));
        };
    }
}
