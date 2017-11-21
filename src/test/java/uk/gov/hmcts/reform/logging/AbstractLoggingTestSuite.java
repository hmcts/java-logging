package uk.gov.hmcts.reform.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.After;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;
import uk.gov.hmcts.reform.logging.exception.AlertLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public abstract class AbstractLoggingTestSuite {

    protected ByteArrayOutputStream baos;

    private final PrintStream old = System.out;

    protected class ProviderException extends AbstractLoggingException {
        public ProviderException(String message) {
            super(AlertLevel.P1, "0", message);
        }
    }

    protected void captureOutput() throws IOException, JoranException {
        System.setProperty("ROOT_APPENDER", "JSON_CONSOLE");

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        JoranConfigurator configurator = new JoranConfigurator();

        InputStream configStream = getClass().getClassLoader().getResourceAsStream("logback.xml");
        configurator.setContext(loggerContext);
        configurator.doConfigure(configStream);
        configStream.close();

        baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
    }

    @After
    public void resetConsole() {
        System.clearProperty("ROOT_APPENDER");
        System.clearProperty("LOGBACK_REQUIRE_ALERT_LEVEL");
        System.clearProperty("LOGBACK_REQUIRE_ERROR_CODE");
        System.out.flush();
        System.setOut(old);
    }

}
