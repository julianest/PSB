package student_service.infraestructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import student_service.domain.model.Student;
import student_service.domain.port.StudentRepository;

@Repository
@RequiredArgsConstructor
public class StudentRepositoryAdapter implements StudentRepository {

    private final StudentR2dbcRepository r2dbcRepository;
    private final StudentMapper studentMapper;
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Student> save(Student student) {
        StudentEntity entity = studentMapper.toEntity(student);
        return template.insert(entity)
                .map(studentMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return r2dbcRepository.existsById(id);
    }

    @Override
    public Flux<Student> findByState(String state) {
        return r2dbcRepository.findByState(state)
                .map(studentMapper::toDomain);
    }
}
