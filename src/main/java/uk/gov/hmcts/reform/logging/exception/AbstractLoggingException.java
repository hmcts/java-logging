package uk.gov.hmcts.reform.logging.exception;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLoggingException extends RuntimeException {

    private final AlertLevel alertLevel;
    private final String errorCode;

    private static Logger log = LoggerFactory.getLogger(AbstractLoggingException.class);

    protected AbstractLoggingException(AlertLevel alertLevel, String errorCode, Throwable cause) {
        super(cause);

        this.alertLevel = alertLevel;
        this.errorCode = errorCode;
    }

    protected AbstractLoggingException(AlertLevel alertLevel, String errorCode, String message) {
        super(message);

        this.alertLevel = alertLevel;
        this.errorCode = errorCode;
    }

    protected AbstractLoggingException(AlertLevel alertLevel, String errorCode, String message, Throwable cause) {
        super(message, cause);

        this.alertLevel = alertLevel;
        this.errorCode = errorCode;
    }

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public String getErrorCode() {
        return errorCode;
    }

    private static void triggerBadImplementationLog(Throwable cause) {
        Throwable invalid = new InvalidExceptionImplementation("AlertLevel is mandatory as per configuration", cause);

        log.error("Bad implementation of '" + cause.getClass().getCanonicalName() + "' in use", invalid);
    }

    public static AbstractLoggingException getFromLogEvent(ILoggingEvent event) {
        Throwable eventException = ((ThrowableProxy) event.getThrowableProxy()).getThrowable();

        if (eventException instanceof AbstractLoggingException) {
            return (AbstractLoggingException) eventException;
        } else if (eventException.getCause() instanceof AbstractLoggingException) {
            // for spring boot projects there's a generic exception wrapper
            // let's try to cast the cause instead
            return (AbstractLoggingException) eventException.getCause();
        } else {
            triggerBadImplementationLog(eventException);

            return null;
        }
    }
}
