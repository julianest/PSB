package student_service.infraestructure.adapter.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudentResponse {

    private final Long id;
    private final String name;
    private final String lastName;
    private final String state;
    private final Integer age;
}
