package student_service.infraestructure.adapter.in.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import student_service.infraestructure.adapter.in.dto.CreateStudentRequest;
import student_service.infraestructure.adapter.in.dto.ErrorResponse;
import student_service.infraestructure.adapter.in.dto.SavedResponse;
import student_service.infraestructure.adapter.in.dto.StudentResponse;

@RequestMapping("v1/api/students")
@Tag(name = "Students", description = "Endpoints para gestión de alumnos")
public interface StudentApiDoc {
    @Operation(
            summary = "Crear alumno",
            description = "Registra un nuevo alumno validando consistencia de datos y unicidad del id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Alumno creado exitosamente",
                    content = @Content(schema = @Schema(implementation = SavedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El id del alumno ya existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<SavedResponse> createStudent(CreateStudentRequest request);


    @Operation(
            summary = "Listar alumnos activos",
            description = "Retorna todos los alumnos con estado ACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de alumnos activos",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = StudentResponse.class))))
    })
    Flux<StudentResponse> getActiveStudents();
}
