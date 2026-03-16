package student_service.domain.port;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import student_service.domain.model.Student;

public interface StudentRepository {

    Mono<Student> save(Student student);

    Mono<Boolean> existsById(Long id);

    Flux<Student> findByState(String state);
}
