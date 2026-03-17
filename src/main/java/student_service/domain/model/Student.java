package student_service.domain.model;

import lombok.Builder;

@Builder
public record Student(Long id, String name, String lastName, StudentStatus state, Integer age) {

}
