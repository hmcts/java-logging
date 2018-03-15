package uk.gov.hmcts.reform.logging.exception;

import static uk.gov.hmcts.reform.logging.exception.ErrorCode.UNKNOWN;

public class UnknownErrorCodeException extends AbstractLoggingException {

    protected UnknownErrorCodeException(AlertLevel alertLevel, Throwable cause) {
        super(alertLevel, UNKNOWN, cause);
    }

    protected UnknownErrorCodeException(AlertLevel alertLevel, String message) {
        super(alertLevel, UNKNOWN, message);
    }

    protected UnknownErrorCodeException(AlertLevel alertLevel, String message, Throwable cause) {
        super(alertLevel, UNKNOWN, message, cause);
    }
}
