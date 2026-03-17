package student_service.infraestructure.adapter.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import student_service.domain.model.Student;
import student_service.domain.model.StudentStatus;

import static org.assertj.core.api.Assertions.assertThat;

class StudentMapperTest {

    private final StudentMapper mapper = new StudentMapperImpl();

    @Test
    @DisplayName("Debe mapear Student de dominio a StudentEntity correctamente")
    void debeMapearStudentAEntity() {
        Student student = Student.builder()
                .id(1L).name("Juan").lastName("Perez")
                .state(StudentStatus.ACTIVE).age(20).build();

        StudentEntity entity = mapper.toEntity(student);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Juan");
        assertThat(entity.getLastName()).isEqualTo("Perez");
        assertThat(entity.getState()).isEqualTo("ACTIVE");
        assertThat(entity.getAge()).isEqualTo(20);
    }

    @Test
    @DisplayName("Debe mapear StudentEntity a Student de dominio correctamente")
    void debeMapearEntityAStudent() {
        StudentEntity entity = StudentEntity.builder()
                .id(2L).name("Ana").lastName("Lopez")
                .state("INACTIVE").age(25).build();

        Student student = mapper.toDomain(entity);

        assertThat(student.id()).isEqualTo(2L);
        assertThat(student.name()).isEqualTo("Ana");
        assertThat(student.lastName()).isEqualTo("Lopez");
        assertThat(student.state()).isEqualTo(StudentStatus.INACTIVE);
        assertThat(student.age()).isEqualTo(25);
    }

    @Test
    @DisplayName("Debe mapear estado null de entidad a null en dominio")
    void debeMapearEstadoNullDeEntidadANull() {
        StudentEntity entity = StudentEntity.builder()
                .id(3L).name("Test").lastName("User")
                .state(null).age(18).build();

        Student student = mapper.toDomain(entity);

        assertThat(student.state()).isNull();
    }

    @Test
    @DisplayName("Debe mapear estado null de dominio a null en entidad")
    void debeMapearEstadoNullDeDominioANull() {
        Student student = Student.builder()
                .id(4L).name("Test").lastName("User")
                .state(null).age(18).build();

        StudentEntity entity = mapper.toEntity(student);

        assertThat(entity.getState()).isNull();
    }
}
