package uk.gov.hmcts.reform.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.google.code.tempusfugit.temporal.Duration;
import com.google.code.tempusfugit.temporal.Timeout;
import com.google.code.tempusfugit.temporal.WaitFor;
import org.junit.After;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;
import uk.gov.hmcts.reform.logging.exception.AlertLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractLoggingTestSuite {

    protected ByteArrayOutputStream baos;

    private final PrintStream old = System.out;

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private static final String DEFAULT_LOGBACK_CONFIG = "logback.xml";

    protected class ProviderException extends AbstractLoggingException {
        public ProviderException(String message) {
            super(AlertLevel.P1, "0", message);
        }
    }

    protected void setDefaultConsoleAppender() {
        environmentVariables.set("ROOT_APPENDER", "CONSOLE");
    }

    protected void setJsonConsoleAppender() {
        environmentVariables.set("ROOT_APPENDER", "JSON_CONSOLE");
    }

    /**
     * Configure logback with provided resource name.
     *
     * @param resource Name of the resource
     *
     * @throws IOException Failure to close resource stream
     * @throws JoranException Failure to configure resource
     */
    protected void captureOutput(String resource) throws IOException, JoranException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        if (System.getenv("ROOT_APPENDER").equals("JSON_CONSOLE")) {
            loggerContext.reset();
        }

        JoranConfigurator configurator = new JoranConfigurator();

        InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        configurator.setContext(loggerContext);
        configurator.doConfigure(configStream);
        configStream.close();

        baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
    }

    /**
     * Configure logback with default resource.
     *
     * @throws IOException Failure to close resource stream
     * @throws JoranException Failure to configure resource
     */
    protected void captureOutput() throws IOException, JoranException {
        captureOutput(DEFAULT_LOGBACK_CONFIG);
    }

    protected void awaitForOutputPattern(String regex) throws TimeoutException, InterruptedException {
        WaitFor.waitOrTimeout(() -> {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(baos.toString());
            return matcher.find();
        }, Timeout.timeout(Duration.seconds(5)));
    }

    @After
    public void resetConsole() {
        System.out.flush();
        System.setOut(old);
    }

}
