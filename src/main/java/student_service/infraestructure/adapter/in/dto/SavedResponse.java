package student_service.infraestructure.adapter.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SavedResponse {

    private final int responseCode;
    private final String description;
}
