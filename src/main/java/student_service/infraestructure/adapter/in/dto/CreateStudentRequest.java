package student_service.infraestructure.adapter.in.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentRequest {

    @NotNull(message = "El id es obligatorio.")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    private String name;

    @NotBlank(message = "El apellido es obligatorio.")
    private String lastName;

    @NotNull(message = "El estado es obligatorio.")
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "El estado debe ser ACTIVE o INACTIVE.")
    private String state;

    @NotNull(message = "La edad es obligatoria.")
    @Min(value = 1, message = "La edad debe ser mayor a 0.")
    @Max(value = 150, message = "La edad debe ser menor o igual a 150.")
    private Integer age;
}
