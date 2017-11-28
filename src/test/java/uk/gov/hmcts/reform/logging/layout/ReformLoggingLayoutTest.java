package uk.gov.hmcts.reform.logging.layout;

import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.AbstractLoggingTestSuite;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;
import uk.gov.hmcts.reform.logging.exception.AlertLevel;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public class ReformLoggingLayoutTest extends AbstractLoggingTestSuite {

    private static final Logger log = LoggerFactory.getLogger(ReformLoggingLayoutTest.class);

    private static final String LOGBACK_WITH_THREAD = "logback-test-enable-thread.xml";
    private static final String LOGBACK_WITH_CUSTOM_DATE_FORMAT = "logback-test-custom-date-format.xml";

    private static final String DEFAULT_DATE_FORMAT = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}(\\+|-)\\d{4}";
    private static final String INFO = " INFO  ";
    private static final String ERROR = " ERROR ";

    private static final String CURRENT_CLASS_LOGGER = String.format(
        "%s:\\d+: ", ReformLoggingLayoutTest.class.getCanonicalName()
    );
    private static final String ERROR_CLASS_LOGGER = String.format(
        "%s:\\d+: ", AbstractLoggingException.class.getCanonicalName()
    );

    private class DummyP2Exception extends AbstractLoggingException {
        DummyP2Exception() {
            super(AlertLevel.P2, "0", "oh no");
        }

        DummyP2Exception(Throwable cause) {
            super(AlertLevel.P2, "0", "oh no", cause);
        }
    }

    private class DummyP3Exception extends AbstractLoggingException {
        DummyP3Exception() {
            super(AlertLevel.P3, "0", "oh no");
        }
    }

    @Before
    public void setUp() {
        setDefaultConsoleAppender();
    }

    @Test
    public void testDefaultOutput() throws JoranException, IOException {
        captureOutput(LOGBACK_WITH_THREAD);

        String message = "test default output";

        log.info(message);

        assertThat(baos.toString()).containsPattern(
            DEFAULT_DATE_FORMAT + INFO + getThreadName() + CURRENT_CLASS_LOGGER + message + "\n"
        );
    }

    @Test
    public void testDefaultOutputWithP2Exception() throws JoranException, IOException {
        captureOutput(LOGBACK_WITH_THREAD);

        String message = "test output with P2 exception and error code";

        log.error(message, new DummyP2Exception());

        assertThat(baos.toString()).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + CURRENT_CLASS_LOGGER + "\\[P2\\] 0. " + message + "\n"
        );
    }

    @Test
    public void testDefaultOutputWithBadException()
        throws JoranException, IOException, InterruptedException, TimeoutException {

        captureOutput(LOGBACK_WITH_THREAD);

        String message = "test output with bad exception";

        log.error(message, new InvalidClassException("Class null is invalid"));

        String errorClass = InvalidClassException.class.getCanonicalName();
        String message2 = String.format("Bad implementation of '%s' in use", errorClass);

        // there must be original log
        assertThat(baos.toString()).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + CURRENT_CLASS_LOGGER + message + "\n"
        );
        // alongside log about alert level misuse
        awaitForOutputPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + ERROR_CLASS_LOGGER + "\\[P1\\] 0. " + message2 + "\n"
        );
    }

    @Test
    public void testNoThreadCustomDateFormatOutput() throws JoranException, IOException {
        captureOutput(LOGBACK_WITH_CUSTOM_DATE_FORMAT);

        String message = "test custom date";

        log.info(message);

        String timestamp = "\\d{2}-\\d{2}-\\d{4}";

        assertThat(baos.toString()).containsPattern(
            timestamp + INFO + CURRENT_CLASS_LOGGER + message + "\n"
        );
    }

    @Test
    public void testOutputWhenAlertLevelIsDisabled() throws JoranException, IOException {
        environmentVariables.set("LOGBACK_REQUIRE_ALERT_LEVEL", "false");
        captureOutput();

        String message = "test when alert level is disabled";

        log.error(message, new DummyP3Exception());

        assertThat(baos.toString()).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + CURRENT_CLASS_LOGGER + "0. " + message + "\n"
        );
    }

    @Test
    public void testOutputWhenErrorCodeIsDisabled() throws JoranException, IOException {
        environmentVariables.set("LOGBACK_REQUIRE_ERROR_CODE", "false");
        captureOutput();

        String message = "test when error code is disabled";

        log.error(message, new DummyP3Exception());

        assertThat(baos.toString()).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + CURRENT_CLASS_LOGGER + "\\[P3\\] " + message + "\n"
        );
    }

    @Test
    public void testOutputForOptionalErrorValues() throws JoranException, IOException {
        captureOutput(LOGBACK_WITH_CUSTOM_DATE_FORMAT);

        String message = "test alert level and error code for regular log level";

        log.info(message);
        log.info(message, new DummyP3Exception());

        String timestamp = "\\d{2}-\\d{2}-\\d{4}";

        String output = baos.toString();

        assertThat(output).containsPattern(
            timestamp + INFO + CURRENT_CLASS_LOGGER + message + "\n"
        );
        assertThat(output).containsPattern(
            timestamp + INFO + CURRENT_CLASS_LOGGER + "\\[P3\\] 0. " + message + "\n"
        );
    }

    @Test
    public void testExtraLogForEmptyCause() throws JoranException, IOException, InterruptedException, TimeoutException {
        captureOutput();

        String message = "test exception not found log";

        log.error(message);

        // there must be original log
        assertThat(baos.toString()).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + CURRENT_CLASS_LOGGER + message + "\n"
        );
        // alongside log about alert level misuse
        awaitForOutputPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + ERROR_CLASS_LOGGER + "\\[P1\\] 0. Exception not found\n"
        );
    }

    @Test
    public void testStacktraceExistsAfterTheLogEntry() throws JoranException, IOException {
        captureOutput();

        String message = "test stacktrace";

        log.error(message, new DummyP2Exception());

        assertThat(baos.toString()).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + CURRENT_CLASS_LOGGER + "\\[P2\\] 0. " + message + "\n"
                + "\tat " + this.getClass().getCanonicalName() + ".testStacktraceExistsAfterTheLogEntry(.*"
                + this.getClass().getSimpleName() + ".java:\\d+.*)\n"
        );

        log.error(message, new DummyP2Exception(new ArithmeticException("There is no such operation ':'")));

        assertThat(baos.toString()).containsPattern(
            "Caused by: " + ArithmeticException.class.getCanonicalName() + ": There is no such operation ':'\n"
        );
    }

    private String getThreadName() {
        return String.format("\\[%s\\] ", Thread.currentThread().getName());
    }
}
