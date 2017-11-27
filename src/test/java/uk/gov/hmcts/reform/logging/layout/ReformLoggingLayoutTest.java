package uk.gov.hmcts.reform.logging.layout;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;
import uk.gov.hmcts.reform.logging.exception.AlertLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ReformLoggingLayoutTest {

    private final PrintStream old = System.out;
    private ByteArrayOutputStream baos;
    private static final Logger log = LoggerFactory.getLogger(ReformLoggingLayoutTest.class);

    private static final String LOGBACK = "logback.xml";
    private static final String LOGBACK_WITH_THREAD = "logback-test-enable-thread.xml";
    private static final String LOGBACK_WITH_CUSTOM_DATE_FORMAT = "logback-test-custom-date-format.xml";

    private static final String DEFAULT_DATE_FORMAT = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}(\\+|-)\\d{4}";
    private static final String INFO = " INFO  ";
    private static final String ERROR = " ERROR ";

    private class DummyP2Exception extends AbstractLoggingException {
        DummyP2Exception() {
            super(AlertLevel.P2, "0", "oh no");
        }
    }

    private class DummyP3Exception extends AbstractLoggingException {
        DummyP3Exception() {
            super(AlertLevel.P3, "0", "oh no");
        }
    }

    private void configLogback(String config) throws JoranException, IOException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();

        InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(config);
        configurator.setContext(loggerContext);
        configurator.doConfigure(configStream);
        configStream.close();

        // capture console

        baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
    }

    @After
    public void resetConsole() {
        System.out.flush();
        System.setOut(old);
        System.clearProperty("LOGBACK_REQUIRE_ALERT_LEVEL");
        System.clearProperty("LOGBACK_REQUIRE_ERROR_CODE");
    }

    @Test
    public void testDefaultOutput() throws JoranException, IOException {
        configLogback(LOGBACK_WITH_THREAD);

        String message = "test default output";

        log.info(message);

        assertThat(baos.toString()).containsPattern(
            DEFAULT_DATE_FORMAT + INFO + getThreadName() + getLogger() + message + "\n"
        );
    }

    @Test
    public void testDefaultOutputWithP2Exception() throws JoranException, IOException {
        configLogback(LOGBACK_WITH_THREAD);

        String message = "test output with P2 exception and error code";

        log.error(message, new DummyP2Exception());

        assertThat(baos.toString()).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + getLogger() + "\\[P2\\] 0. " + message + "\n"
        );
    }

    @Test
    public void testDefaultOutputWithBadException() throws JoranException, IOException {
        configLogback(LOGBACK_WITH_THREAD);

        String message = "test output with bad exception";

        log.error(message, new InvalidClassException("oh no"));

        String logger = AbstractLoggingException.class.getCanonicalName();
        String errorClass = InvalidClassException.class.getCanonicalName();
        String message2 = String.format("Bad implementation of '%s' in use", errorClass);

        String output = baos.toString();

        // there must be original log
        assertThat(output).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + getLogger() + message + "\n"
        );
        // alongside log about alert level misuse
        assertThat(output).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + logger + ":\\d+: \\[P1\\] 0. " + message2 + "\n"
        );
    }

    @Test
    public void testNoThreadCustomDateFormatOutput() throws JoranException, IOException {
        configLogback(LOGBACK_WITH_CUSTOM_DATE_FORMAT);

        String message = "test custom date";

        log.info(message);

        String timestamp = "\\d{2}-\\d{2}-\\d{4}";

        assertThat(baos.toString()).containsPattern(
            timestamp + INFO + getLogger() + message + "\n"
        );
    }

    @Test
    public void testOutputWhenAlertLevelIsDisabled() throws JoranException, IOException {
        System.setProperty("LOGBACK_REQUIRE_ALERT_LEVEL", "false");
        configLogback(LOGBACK);

        String message = "test when alert level is disabled";

        log.error(message, new DummyP3Exception());

        String logger = this.getClass().getCanonicalName();

        assertThat(baos.toString()).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + logger + ":\\d+: 0. " + message + "\n"
        );
    }

    @Test
    public void testOutputWhenErrorCodeIsDisabled() throws JoranException, IOException {
        System.setProperty("LOGBACK_REQUIRE_ERROR_CODE", "false");
        configLogback(LOGBACK);

        String message = "test when error code is disabled";

        log.error(message, new DummyP3Exception());

        String logger = this.getClass().getCanonicalName();

        assertThat(baos.toString()).containsPattern(
            DEFAULT_DATE_FORMAT + ERROR + getThreadName() + logger + ":\\d+: \\[P3\\] " + message + "\n"
        );
    }

    @Test
    public void testOutputForOptionalErrorValues() throws JoranException, IOException {
        configLogback(LOGBACK_WITH_CUSTOM_DATE_FORMAT);

        String message = "test alert level and error code for regular log level";

        log.info(message);
        log.info(message, new DummyP3Exception());

        String timestamp = "\\d{2}-\\d{2}-\\d{4}";
        String logger = this.getClass().getCanonicalName();

        String output = baos.toString();

        assertThat(output).containsPattern(
            timestamp + INFO + logger + ":\\d+: " + message + "\n"
        );
        assertThat(output).containsPattern(
            timestamp + INFO + logger + ":\\d+: \\[P3\\] 0. " + message + "\n"
        );
    }

    private String getThreadName() {
        return String.format("\\[%s\\] ", Thread.currentThread().getName());
    }

    private String getLogger() {
        return String.format("%s:\\d+: ", this.getClass().getCanonicalName());
    }
}
