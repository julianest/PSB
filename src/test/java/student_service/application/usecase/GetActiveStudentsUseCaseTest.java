package student_service.application.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import student_service.domain.model.Student;
import student_service.domain.model.StudentStatus;
import student_service.domain.port.StudentRepository;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetActiveStudentsUseCaseTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private GetActiveStudentsUseCase getActiveStudentsUseCase;

    @Test
    @DisplayName("Debe retornar solo alumnos con estado ACTIVE")
    void debeRetornarSoloAlumnosActivos() {
        Student activo = Student.builder()
                .id(1L).name("Ana").lastName("Lopez")
                .state(StudentStatus.ACTIVE).age(22).build();

        when(studentRepository.findByState("ACTIVE")).thenReturn(Flux.just(activo));

        StepVerifier.create(getActiveStudentsUseCase.execute())
                .expectNextMatches(s -> s.state() == StudentStatus.ACTIVE)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar vacio cuando no hay alumnos activos")
    void debeRetornarVacioCuandoNoHayActivos() {
        when(studentRepository.findByState("ACTIVE")).thenReturn(Flux.empty());

        StepVerifier.create(getActiveStudentsUseCase.execute())
                .verifyComplete();
    }
}
