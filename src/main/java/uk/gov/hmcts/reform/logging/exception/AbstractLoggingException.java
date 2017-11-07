package uk.gov.hmcts.reform.logging.exception;

public abstract class AbstractLoggingException extends RuntimeException {

    private final AlertLevel alertLevel;

    protected AbstractLoggingException(AlertLevel alertLevel, Throwable cause) {
        super(cause);

        this.alertLevel = alertLevel;
    }

    protected AbstractLoggingException(AlertLevel alertLevel, String message) {
        super(message);

        this.alertLevel = alertLevel;
    }

    protected AbstractLoggingException(AlertLevel alertLevel, String message, Throwable cause) {
        super(message, cause);

        this.alertLevel = alertLevel;
    }

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }
}
