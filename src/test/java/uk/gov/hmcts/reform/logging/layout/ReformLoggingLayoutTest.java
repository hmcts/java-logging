package uk.gov.hmcts.reform.logging.layout;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;
import uk.gov.hmcts.reform.logging.exception.AlertLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.PrintStream;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ReformLoggingLayoutTest {

    private PrintStream old = System.out;
    private ByteArrayOutputStream baos = null;

    private boolean withThread;
    private String dateFormat;
    private AlertLevel priority;
    private boolean withAlertLevel;

    private class DummyP2Exception extends AbstractLoggingException {
        DummyP2Exception(String message) {
            super(message);

            setAlertLevel(AlertLevel.P2);
        }
    }

    private class DummyP3Exception extends AbstractLoggingException {
        DummyP3Exception(String message) {
            super(message);

            setAlertLevel(AlertLevel.P3);
        }
    }

    @Parameterized.Parameters(name = "Parsing logback configuration \"{0}\"")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "logback-test-enable-thread.xml", true, null, null, true },
            { "logback-test-enable-thread.xml", true, null, AlertLevel.P2, true},
            { "logback-test-enable-thread.xml", true, null, AlertLevel.P4, true },// will use some other ex
            { "logback-test-custom-date-format.xml", false, "\\d{2}-\\d{2}-\\d{4}", null, true},
            { "logback-test-disable-alert-level.xml", false, null, AlertLevel.P3, false }
        });
    }

    public ReformLoggingLayoutTest(String resource,
                                   boolean withThread,
                                   String dateFormat,
                                   AlertLevel priority,
                                   boolean withAlertLevel) throws JoranException, IOException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();

        InputStream configStream = getClass().getClassLoader().getResourceAsStream(resource);
        configurator.setContext(loggerContext);
        configurator.doConfigure(configStream);
        configStream.close();

        // capture console

        baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);

        // set logger parameters

        this.withThread = withThread;
        this.dateFormat = dateFormat;
        this.priority = priority;
        this.withAlertLevel = withAlertLevel;
    }

    @After
    public void resetConsole() {
        System.out.flush();
        System.setOut(old);
    }

    @Test
    public void testMessagePatterns() {
        Logger log = LoggerFactory.getLogger(ReformLoggingLayoutTest.class);

        String message = "message";
        String level = "INFO ";

        if (withAlertLevel && priority != null) {
            message = "\\[" + priority.name() + "\\] error";
            level = "ERROR";

            if (priority.equals(AlertLevel.P4)) {
                message = "\\[" + AlertLevel.P1.name() + "\\] Bad implementation of '"
                    + InvalidClassException.class.getCanonicalName() + "' in use";

                log.error("error", new InvalidClassException("oh no"));
            } else {
                log.error("error", new DummyP2Exception("oh no"));
            }
        } else if (priority != null) {
            level = "ERROR";

            log.error(message, new DummyP3Exception("oh no"));
        } else {
            log.info(message);
        }

        String timestamp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}";
        String thread = "\\[" + Thread.currentThread().getName() + "\\] ";
        String logger = this.getClass().getCanonicalName();

        if (!withThread) {
            thread = "";
        }

        if (dateFormat != null) {
            timestamp = dateFormat;
        }

        if (priority != null && priority.equals(AlertLevel.P4)) {
            logger = ReformLoggingLayout.class.getCanonicalName();
        }

        assertThat(baos.toString()).containsPattern(
            timestamp + " " + level + " " + thread + logger + ":\\d+: " + message + "\n"
        );
    }
}
