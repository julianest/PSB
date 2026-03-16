package student_service.domain.util.logger;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class CanonicalLog {
    private final String id;
    private final String domainNameService;
    private final String domainNameUnity;
    private final String domainNameBusiness;
    private final String operation;
    private final Object data;
    private final String type;
    private final String trace;
    private final ErrorLog error;
    private final Integer responseCode;

    @Getter
    @Setter
    @Builder(toBuilder = true)
    public static class ErrorLog {
        private final Integer code;
        private final String type;
        private final String description;
    }

}

