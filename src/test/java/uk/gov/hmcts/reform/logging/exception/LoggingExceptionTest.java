package uk.gov.hmcts.reform.logging.exception;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LoggingExceptionTest {

    class DefaultP1Exception extends AbstractLoggingException {
        DefaultP1Exception(String message) {
            super(message);
        }
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createDefaultException() {
        exception.expect(DefaultP1Exception.class);
        exception.expectMessage("Some message");

        throw new DefaultP1Exception("Some message");
    }
}
