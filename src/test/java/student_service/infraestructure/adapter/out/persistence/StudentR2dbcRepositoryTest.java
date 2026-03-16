package student_service.infraestructure.adapter.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.test.StepVerifier;

@DataR2dbcTest
class StudentR2dbcRepositoryTest {

    @Autowired
    private StudentR2dbcRepository repository;

    @Autowired
    private R2dbcEntityTemplate template;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1");
        registry.add("spring.r2dbc.username", () -> "sa");
        registry.add("spring.r2dbc.password", () -> "");
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.sql.init.schema-locations", () -> "classpath:schema.sql");
    }

    private StudentEntity buildEntity(Long id, String state) {
        return StudentEntity.builder()
                .id(id)
                .name("Test")
                .lastName("User")
                .state(state)
                .age(25)
                .build();
    }

    @Test
    @DisplayName("Debe persistir y recuperar un alumno por id")
    void debePersistirYRecuperarAlumno() {
        StudentEntity entity = buildEntity(100L, "ACTIVE");

        StepVerifier.create(template.insert(entity)
                        .then(repository.findById(100L)))
                .expectNextMatches(saved -> saved.getName().equals("Test")
                        && saved.getState().equals("ACTIVE"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar true si el id ya existe")
    void debeRetornarTrueSiIdExiste() {
        StudentEntity entity = buildEntity(200L, "ACTIVE");

        StepVerifier.create(template.insert(entity)
                        .then(repository.existsById(200L)))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe filtrar alumnos por estado ACTIVE")
    void debeFiltrarPorEstadoActivo() {
        StudentEntity activo = buildEntity(300L, "ACTIVE");
        StudentEntity inactivo = buildEntity(301L, "INACTIVE");

        StepVerifier.create(
                        template.delete(StudentEntity.class).all()
                                .then(template.insert(activo))
                                .then(template.insert(inactivo))
                                .thenMany(repository.findByState("ACTIVE"))
                )
                .expectNextMatches(e -> e.getState().equals("ACTIVE"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar false si el id no existe")
    void debeRetornarFalseSiIdNoExiste() {
        StepVerifier.create(repository.existsById(999L))
                .expectNext(false)
                .verifyComplete();
    }
}
