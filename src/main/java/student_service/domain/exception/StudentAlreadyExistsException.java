package student_service.domain.exception;

public class StudentAlreadyExistsException extends RuntimeException {

    public StudentAlreadyExistsException(Long id) {
        super("No se pudo realizar la grabacion: el alumno con id " + id + " ya existe.");
    }
}
