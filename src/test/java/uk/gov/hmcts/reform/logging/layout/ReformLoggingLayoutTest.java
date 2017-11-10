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

    private PrintStream old = System.out;
    private ByteArrayOutputStream baos = null;
    private final Logger log = LoggerFactory.getLogger(ReformLoggingLayoutTest.class);

    private static final String LOGBACK_WITH_THREAD = "logback-test-enable-thread.xml";
    private static final String LOGBACK_WITH_CUSTOM_DATE_FORMAT = "logback-test-custom-date-format.xml";
    private static final String LOGBACK_WITHOUT_ALERT_LEVEL = "logback-test-disable-alert-level.xml";

    private class DummyP2Exception extends AbstractLoggingException {
        DummyP2Exception(String message) {
            super(AlertLevel.P2, message);
        }
    }

    private class DummyP3Exception extends AbstractLoggingException {
        DummyP3Exception(String message) {
            super(AlertLevel.P3, message);
        }
    }

    private void configLogback(String config) throws JoranException, IOException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();

        InputStream configStream = getClass().getClassLoader().getResourceAsStream(config);
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
    }

    @Test
    public void testDefaultOutput() throws JoranException, IOException {
        configLogback(LOGBACK_WITH_THREAD);

        log.info("message");

        String timestamp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}(\\+|-)\\d{4}";
        String thread = "\\[" + Thread.currentThread().getName() + "\\] ";
        String logger = this.getClass().getCanonicalName();

        assertThat(baos.toString()).containsPattern(
            timestamp + " INFO  " + thread + logger + ":\\d+: message\n"
        );
    }

    @Test
    public void testDefaultOutputWithP2Exception() throws JoranException, IOException {
        configLogback(LOGBACK_WITH_THREAD);

        log.error("message", new DummyP2Exception("oh no"));

        String timestamp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}(\\+|-)\\d{4}";
        String thread = "\\[" + Thread.currentThread().getName() + "\\] ";
        String logger = this.getClass().getCanonicalName();

        assertThat(baos.toString()).containsPattern(
            timestamp + " ERROR " + thread + logger + ":\\d+: \\[P2\\] message\n"
        );
    }

    @Test
    public void testDefaultOutputWithBadException() throws JoranException, IOException {
        configLogback(LOGBACK_WITH_THREAD);

        log.error("message", new InvalidClassException("oh no"));

        String timestamp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}(\\+|-)\\d{4}";
        String thread = "\\[" + Thread.currentThread().getName() + "\\] ";
        String logger1 = this.getClass().getCanonicalName();
        String logger2 = AbstractLoggingException.class.getCanonicalName();
        String errorClass = InvalidClassException.class.getCanonicalName();
        String message = String.format("Bad implementation of '%s' in use", errorClass);

        String output = baos.toString();

        // there must be original log
        assertThat(output).containsPattern(
            timestamp + " ERROR " + thread + logger1 + ":\\d+: message\n"
        );
        // alongside log about alert level misuse
        assertThat(output).containsPattern(
            timestamp + " ERROR " + thread + logger2 + ":\\d+: \\[P1\\] " + message + "\n"
        );
    }

    @Test
    public void testNoThreadCustomDateFormatOutput() throws JoranException, IOException {
        configLogback(LOGBACK_WITH_CUSTOM_DATE_FORMAT);

        log.info("message");

        String timestamp = "\\d{2}-\\d{2}-\\d{4}";
        String logger = this.getClass().getCanonicalName();

        assertThat(baos.toString()).containsPattern(
            timestamp + " INFO  " + logger + ":\\d+: message\n"
        );
    }

    @Test
    public void testOutputWhenAlertLevelIsDisabled() throws JoranException, IOException {
        configLogback(LOGBACK_WITHOUT_ALERT_LEVEL);

        log.error("message", new DummyP3Exception("oh no"));

        String timestamp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}(\\+|-)\\d{4}";
        String logger = this.getClass().getCanonicalName();

        assertThat(baos.toString()).containsPattern(
            timestamp + " ERROR " + logger + ":\\d+: message\n"
        );
    }
}
