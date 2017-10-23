package uk.gov.hmcts.reform.logging.exception;

class InvalidExceptionImplementation extends AbstractLoggingException {

    InvalidExceptionImplementation(String message, Throwable cause) {
        super(message, cause);
    }
}
