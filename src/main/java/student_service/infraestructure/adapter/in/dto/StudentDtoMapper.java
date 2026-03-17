package student_service.infraestructure.adapter.in.dto;

import org.mapstruct.Mapper;
import student_service.domain.model.Student;
import student_service.domain.model.StudentStatus;

@Mapper(componentModel = "spring")
public interface StudentDtoMapper {

    Student toDomain(CreateStudentRequest request);

    StudentResponse toResponse(Student student);

    default StudentStatus map(String status) {
        return status != null ? StudentStatus.valueOf(status) : null;
    }

    default String map(StudentStatus status) {
        return status != null ? status.name() : null;
    }
}
