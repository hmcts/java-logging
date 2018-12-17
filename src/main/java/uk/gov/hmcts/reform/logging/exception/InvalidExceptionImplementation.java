package uk.gov.hmcts.reform.logging.exception;
//Will be removed in future enhancement revision
@Deprecated
public class InvalidExceptionImplementation extends AbstractLoggingException {

    public InvalidExceptionImplementation(String message, Throwable cause) {
        super(AlertLevel.P1, "0", message, cause);
    }
}
