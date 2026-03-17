package student_service.infraestructure.adapter.in.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import student_service.application.usecase.CreateStudentUseCase;
import student_service.application.usecase.GetActiveStudentsUseCase;
import student_service.domain.exception.StudentAlreadyExistsException;
import student_service.domain.model.Student;
import student_service.domain.model.StudentStatus;
import student_service.infraestructure.adapter.in.dto.StudentDtoMapperImpl;
import student_service.infraestructure.adapter.in.handler.GlobalExceptionHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(StudentController.class)
@Import({GlobalExceptionHandler.class, StudentDtoMapperImpl.class})
class StudentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CreateStudentUseCase createStudentUseCase;

    @Autowired
    private GetActiveStudentsUseCase getActiveStudentsUseCase;

    @TestConfiguration
    static class TestConfig {

        @Bean
        CreateStudentUseCase createStudentUseCase() {
            return Mockito.mock(CreateStudentUseCase.class);
        }

        @Bean
        GetActiveStudentsUseCase getActiveStudentsUseCase() {
            return Mockito.mock(GetActiveStudentsUseCase.class);
        }
    }

    @Test
    @DisplayName("Debe crear alumno y responder 201 cuando datos validos")
    void debeCrearAlumnoYResponder201() {
        Student saved = Student.builder()
                .id(1L).name("Juan").lastName("Perez")
                .state(StudentStatus.ACTIVE).age(20).build();

        when(createStudentUseCase.execute(any(Student.class))).thenReturn(Mono.just(saved));

        webTestClient.post().uri("/v1/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "id": 1,
                            "name": "Juan",
                            "lastName": "Perez",
                            "state": "ACTIVE",
                            "age": 20
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.responseCode").isEqualTo(201)
                .jsonPath("$.description").isNotEmpty();
    }

    @Test
    @DisplayName("Debe responder 409 cuando el id ya existe")
    void debeResponder409CuandoIdDuplicado() {
        when(createStudentUseCase.execute(any(Student.class)))
                .thenReturn(Mono.error(new StudentAlreadyExistsException(1L)));

        webTestClient.post().uri("/v1/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "id": 1,
                            "name": "Juan",
                            "lastName": "Perez",
                            "state": "ACTIVE",
                            "age": 20
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.status").isEqualTo(409);
    }

    @Test
    @DisplayName("Debe responder 400 cuando datos invalidos")
    void debeResponder400CuandoDatosInvalidos() {
        webTestClient.post().uri("/v1/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "id": null,
                            "name": "",
                            "lastName": "",
                            "state": "INVALIDO",
                            "age": -1
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.details").isArray();
    }

    @Test
    @DisplayName("Debe listar solo alumnos activos y responder 200")
    void debeListarAlumnosActivosYResponder200() {
        Student activo = Student.builder()
                .id(1L).name("Ana").lastName("Lopez")
                .state(StudentStatus.ACTIVE).age(22).build();

        when(getActiveStudentsUseCase.execute()).thenReturn(Flux.just(activo));

        webTestClient.get().uri("/v1/api/students/active")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].state").isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Debe retornar lista vacia cuando no hay activos")
    void debeRetornarListaVaciaCuandoNoHayActivos() {
        when(getActiveStudentsUseCase.execute()).thenReturn(Flux.empty());

        webTestClient.get().uri("/v1/api/students/active")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    @DisplayName("Debe responder 400 cuando se lanza IllegalArgumentException desde el caso de uso")
    void debeResponder400CuandoIllegalArgumentException() {
        when(createStudentUseCase.execute(any(Student.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Argumento invalido")));

        webTestClient.post().uri("/v1/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "id": 1,
                            "name": "Juan",
                            "lastName": "Perez",
                            "state": "ACTIVE",
                            "age": 20
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Datos inválidos");
    }

    @Test
    @DisplayName("Debe responder 500 controlado cuando ocurre RuntimeException no contemplada")
    void debeResponder500CuandoRuntimeExceptionNoControlada() {
        when(createStudentUseCase.execute(any(Student.class)))
                .thenReturn(Mono.error(new RuntimeException("Fallo inesperado")));

        webTestClient.post().uri("/v1/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "id": 1,
                            "name": "Juan",
                            "lastName": "Perez",
                            "state": "ACTIVE",
                            "age": 20
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.error").isEqualTo("Error interno")
                .jsonPath("$.message").isEqualTo("Error inesperado. Intenta nuevamente o contacta al administrador.");
    }
}
