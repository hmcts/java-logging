package uk.gov.hmcts.reform.logging.exception;

abstract class AbstractLoggingException extends RuntimeException {

    public AlertLevel alertLevel = AlertLevel.P1;

    protected AbstractLoggingException(Throwable cause) {
        super(cause);
    }

    protected AbstractLoggingException(String message) {
        super(message);
    }

    protected AbstractLoggingException(String message, Throwable cause) {
        super(message, cause);
    }
}
