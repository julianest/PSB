package student_service.application.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import student_service.domain.exception.StudentAlreadyExistsException;
import student_service.domain.model.Student;
import student_service.domain.model.StudentStatus;
import student_service.domain.port.StudentRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateStudentUseCaseTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private CreateStudentUseCase createStudentUseCase;

    private Student buildStudent(Long id) {
        return Student.builder()
                .id(id)
                .name("Juan")
                .lastName("Perez")
                .state(StudentStatus.ACTIVE)
                .age(20)
                .build();
    }

    @Test
    @DisplayName("Debe crear alumno exitosamente cuando el id no existe")
    void debeCrearAlumnoCuandoIdNoExiste() {
        Student student = buildStudent(1L);
        when(studentRepository.existsById(1L)).thenReturn(Mono.just(false));
        when(studentRepository.save(any(Student.class))).thenReturn(Mono.just(student));

        StepVerifier.create(createStudentUseCase.execute(student))
                .expectNextMatches(s -> s.id().equals(1L) && s.name().equals("Juan"))
                .verifyComplete();

        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Debe rechazar creacion cuando el id ya existe")
    void debeRechazarCuandoIdDuplicado() {
        Student student = buildStudent(1L);
        when(studentRepository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(createStudentUseCase.execute(student))
                .expectError(StudentAlreadyExistsException.class)
                .verify();

        verify(studentRepository, never()).save(any());
    }
}
