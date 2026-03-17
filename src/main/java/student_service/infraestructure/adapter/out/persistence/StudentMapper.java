package student_service.infraestructure.adapter.out.persistence;

import org.mapstruct.Mapper;
import student_service.domain.model.Student;
import student_service.domain.model.StudentStatus;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentEntity toEntity(Student student);

    Student toDomain(StudentEntity entity);

    default String map(StudentStatus status) {
        return status != null ? status.name() : null;
    }

    default StudentStatus map(String status) {
        return status != null ? StudentStatus.valueOf(status) : null;
    }
}
