package student_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
		// Verifica que el contexto de Spring Boot arranca correctamente con toda
		// la configuracion de WebFlux, R2DBC y los beans registrados.
		// No requiere aserciones adicionales: si el contexto falla al cargar,
		// el test fallara automaticamente con un error de inicializacion.
	}

}
