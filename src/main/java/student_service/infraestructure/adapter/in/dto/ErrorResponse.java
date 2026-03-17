package student_service.infraestructure.adapter.in.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String message;
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;

    @Getter(AccessLevel.NONE)
    private final List<String> details;

    @Builder
    private ErrorResponse(String message, LocalDateTime timestamp, int status, String error, List<String> details) {
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.details = details != null ? List.copyOf(details) : null;
    }

    public List<String> getDetails() {
        return details;
    }
}
