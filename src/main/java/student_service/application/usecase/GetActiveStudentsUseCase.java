package student_service.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import student_service.domain.model.Student;
import student_service.domain.model.StudentStatus;
import student_service.domain.port.StudentRepository;

@Service
@RequiredArgsConstructor
public class GetActiveStudentsUseCase {

    private final StudentRepository studentRepository;

    public Flux<Student> execute() {
        return studentRepository.findByState(StudentStatus.ACTIVE.name());
    }
}
