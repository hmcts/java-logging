package uk.gov.hmcts.reform.logging.provider;

import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;
import uk.gov.hmcts.reform.logging.exception.AlertLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

public class AlertLevelJsonProviderTest {

    private PrintStream old = System.out;
    private ByteArrayOutputStream baos = null;

    class AlertLevelException extends AbstractLoggingException {
        AlertLevelException(String message) {
            super(AlertLevel.P1, message);
        }
    }

    @Before
    public void captureOutput() throws IOException, JoranException {
        System.setProperty("ROOT_APPENDER", "JSON_CONSOLE");

        baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
    }

    @After
    public void resetConsole() {
        System.clearProperty("ROOT_APPENDER");
        System.out.flush();
        System.setOut(old);
    }

    @Test
    public void testAlertLevel() throws IOException {
        assertThat(System.getProperty("ROOT_APPENDER")).isEqualTo("JSON_CONSOLE");

        Logger log = LoggerFactory.getLogger(AlertLevelJsonProviderTest.class);

        log.error("test", new AlertLevelException("oh no"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(baos.toString());

        assertThat(node.at("/alertLevel").asText()).isEqualTo(AlertLevel.P1.name());
        assertThat(node.at("/message").asText()).isEqualTo("test");
    }
}
