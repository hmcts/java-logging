package uk.gov.hmcts.reform.logging.exception;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LoggingExceptionTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    class DefaultP1Exception extends AbstractLoggingException {
        DefaultP1Exception(String message) {
            super(AlertLevel.P1, message);
        }
    }

    @Test
    public void createDefaultException() {
        exception.expect(DefaultP1Exception.class);
        exception.expectMessage("Some message");

        throw new DefaultP1Exception("Some message");
    }
}
