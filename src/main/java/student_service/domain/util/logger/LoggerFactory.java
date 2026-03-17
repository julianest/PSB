package student_service.domain.util.logger;

public interface LoggerFactory {

    default CanonicalLog createLog(String id, String operation, Object data, String typeMessage) {
        return CanonicalLog.builder()
                .id(id)
                .domainNameService(ConstantsLogs.DOMAIN_SERVICE_NAME)
                .domainNameUnity(ConstantsLogs.DOMAIN_NAME_UNITY)
                .domainNameBusiness(ConstantsLogs.DOMAIN_NAME_BUSINESS)
                .operation(operation)
                .data(data)
                .type(typeMessage)
                .trace(ConstantsLogs.SUCCESS)
                .responseCode(200)
                .build();
    }

    default CanonicalLog createLog(String id, String operation, Object data, String typeMessage,
                                   Integer errorCode, String typeError, String errorDescription) {
        return CanonicalLog.builder()
                .id(id)
                .domainNameService(ConstantsLogs.DOMAIN_SERVICE_NAME)
                .domainNameUnity(ConstantsLogs.DOMAIN_NAME_UNITY)
                .domainNameBusiness(ConstantsLogs.DOMAIN_NAME_BUSINESS)
                .operation(operation)
                .data(data)
                .type(typeMessage)
                .trace(ConstantsLogs.ERROR)
                .error(CanonicalLog.ErrorLog.builder()
                        .code(errorCode)
                        .type(typeError)
                        .description(errorDescription)
                        .build())
                .responseCode(errorCode)
                .build();
    }

}
