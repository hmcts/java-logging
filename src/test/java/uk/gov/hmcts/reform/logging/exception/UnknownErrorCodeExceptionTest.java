package uk.gov.hmcts.reform.logging.exception;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.logging.exception.ErrorCode.UNKNOWN;

public class UnknownErrorCodeExceptionTest {

    @Test
    public void allConstructorsMustHaveUnknownErrorCode() {
        AbstractLoggingException exception1 = new UnknownErrorCodeException(AlertLevel.P4, (Throwable) null);
        AbstractLoggingException exception2 = new UnknownErrorCodeException(AlertLevel.P4, "message");
        AbstractLoggingException exception3 = new UnknownErrorCodeException(AlertLevel.P4, "message", null);

        assertThat(exception1.getErrorCode()).isEqualTo(UNKNOWN);
        assertThat(exception2.getErrorCode()).isEqualTo(UNKNOWN);
        assertThat(exception3.getErrorCode()).isEqualTo(UNKNOWN);
    }
}
