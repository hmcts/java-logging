package uk.gov.hmcts.reform.logging.exception;

public abstract class AbstractLoggingException extends RuntimeException {

    private AlertLevel alertLevel = AlertLevel.P1;

    protected AbstractLoggingException(Throwable cause) {
        super(cause);
    }

    protected AbstractLoggingException(String message) {
        super(message);
    }

    protected AbstractLoggingException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(AlertLevel alertLevel) {
        this.alertLevel = alertLevel;
    }
}
