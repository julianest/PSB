package student_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class OpenApiConfig {

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
}
