package student_service.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import student_service.domain.exception.StudentAlreadyExistsException;
import student_service.domain.model.Student;
import student_service.domain.port.StudentRepository;

@Service
@RequiredArgsConstructor
public class CreateStudentUseCase {

    private final StudentRepository studentRepository;

    @Transactional
    public Mono<Student> execute(Student student) {
        return studentRepository.existsById(student.id())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new StudentAlreadyExistsException(student.id()));
                    }
                    return studentRepository.save(student);
                });
    }
}
