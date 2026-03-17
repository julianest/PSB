package student_service.infraestructure.adapter.in.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import student_service.application.usecase.CreateStudentUseCase;
import student_service.application.usecase.GetActiveStudentsUseCase;
import student_service.infraestructure.adapter.in.dto.CreateStudentRequest;
import student_service.infraestructure.adapter.in.dto.SavedResponse;
import student_service.infraestructure.adapter.in.dto.StudentDtoMapper;
import student_service.infraestructure.adapter.in.dto.StudentResponse;

@RestController
@RequiredArgsConstructor
public class StudentController implements StudentApiDoc {

    private final CreateStudentUseCase createStudentUseCase;
    private final GetActiveStudentsUseCase getActiveStudentsUseCase;
    private final StudentDtoMapper studentDtoMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SavedResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return createStudentUseCase.execute(studentDtoMapper.toDomain(request))
                .map(saved -> SavedResponse.builder()
                        .responseCode(HttpStatus.CREATED.value())
                        .description("Alumno creado exitosamente.")
                        .build());
    }

    @Override
    @GetMapping("/active")
    public Flux<StudentResponse> getActiveStudents() {
        return getActiveStudentsUseCase.execute()
                .map(studentDtoMapper::toResponse);
    }
}
