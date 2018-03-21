package uk.gov.hmcts.reform.logging.layout;

import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.AbstractLoggingTestSuite;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ReformLoggingLayoutTestDifferentLogger extends AbstractLoggingTestSuite {

    private static final Logger log = LoggerFactory.getLogger("some.logger");

    private static final String DEFAULT_DATE_FORMAT = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}(\\+|-)\\d{4}";
    private static final String ERROR = " ERROR ";

    private static final String ERROR_CLASS_LOGGER = String.format(
        "%s:\\d+: ", AbstractLoggingException.class.getCanonicalName()
    );

    @Before
    @Override
    public void setUp() {
        super.setUp();
        setDefaultConsoleAppender();
    }

    @Test
    public void testNoExtraLogForNonHmctsLogger() throws JoranException, IOException {
        captureOutput();

        String message = "test custom logger exception";

        log.error(message);

        assertThat(systemOut.getLog()).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + "some.logger:41: " + message + "\n"
        );

        Throwable exception = catchThrowable(() -> awaitForOutputPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + ERROR_CLASS_LOGGER + "\\[P1\\] 0. Exception not found\n"
        ));

        assertThat(exception).isInstanceOf(TimeoutException.class);
    }

    private String getThreadName() {
        return String.format("\\[%s\\] ", Thread.currentThread().getName());
    }
}
