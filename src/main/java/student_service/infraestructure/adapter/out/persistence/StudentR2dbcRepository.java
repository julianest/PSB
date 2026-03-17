package student_service.infraestructure.adapter.out.persistence;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StudentR2dbcRepository extends ReactiveCrudRepository<StudentEntity, Long> {

    @Query("SELECT * FROM students WHERE state = :state")
    Flux<StudentEntity> findByState(String state);
}
