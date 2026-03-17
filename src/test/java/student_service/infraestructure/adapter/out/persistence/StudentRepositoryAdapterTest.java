package student_service.infraestructure.adapter.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import student_service.domain.model.Student;
import student_service.domain.model.StudentStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentRepositoryAdapterTest {

    @Mock
    private StudentR2dbcRepository r2dbcRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private R2dbcEntityTemplate template;

    @InjectMocks
    private StudentRepositoryAdapter adapter;

    private Student buildStudent() {
        return Student.builder()
                .id(1L).name("Juan").lastName("Perez")
                .state(StudentStatus.ACTIVE).age(20).build();
    }

    private StudentEntity buildEntity() {
        return StudentEntity.builder()
                .id(1L).name("Juan").lastName("Perez")
                .state("ACTIVE").age(20).build();
    }

    @Test
    @DisplayName("Debe persistir alumno usando template y retornar dominio mapeado")
    void debePersistirAlumnoYRetornarDominio() {
        Student student = buildStudent();
        StudentEntity entity = buildEntity();

        when(studentMapper.toEntity(student)).thenReturn(entity);
        when(template.insert(any(StudentEntity.class))).thenReturn(Mono.just(entity));
        when(studentMapper.toDomain(entity)).thenReturn(student);

        StepVerifier.create(adapter.save(student))
                .expectNextMatches(s -> s.id().equals(1L) && s.name().equals("Juan"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar true cuando el alumno existe por id")
    void debeRetornarTrueCuandoAlumnoExiste() {
        when(r2dbcRepository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsById(1L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar false cuando el alumno no existe por id")
    void debeRetornarFalseCuandoAlumnoNoExiste() {
        when(r2dbcRepository.existsById(99L)).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsById(99L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe filtrar alumnos por estado y mapear a dominio")
    void debeFiltrarAlumnosPorEstado() {
        Student student = buildStudent();
        StudentEntity entity = buildEntity();

        when(r2dbcRepository.findByState("ACTIVE")).thenReturn(Flux.just(entity));
        when(studentMapper.toDomain(entity)).thenReturn(student);

        StepVerifier.create(adapter.findByState("ACTIVE"))
                .expectNextMatches(s -> s.state() == StudentStatus.ACTIVE)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar flux vacio cuando no hay alumnos con el estado dado")
    void debeRetornarVacioCuandoNoHayAlumnosConEstado() {
        when(r2dbcRepository.findByState("INACTIVE")).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findByState("INACTIVE"))
                .verifyComplete();
    }
}
