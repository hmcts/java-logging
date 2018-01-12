package uk.gov.hmcts.reform.logging.provider;

import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.AbstractLoggingTestSuite;
import uk.gov.hmcts.reform.logging.exception.AlertLevel;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class AlertLevelJsonProviderTest extends AbstractLoggingTestSuite {

    private static final Logger log = LoggerFactory.getLogger(AlertLevelJsonProviderTest.class);

    @Before
    public void setUp() {
        setJsonConsoleAppender();
    }

    @Test
    public void testAlertLevel() throws IOException, JoranException {
        captureOutput();

        assertThat(System.getenv("ROOT_APPENDER")).isEqualTo("JSON_CONSOLE");

        String message = "test alert level is present";

        log.error(message, new ProviderException("oh no"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(baos.toString());

        assertThat(AlertLevel.valueOf(node.at("/alertLevel").asText())).isEqualByComparingTo(AlertLevel.P1);
        assertThat(node.at("/message").asText()).isEqualTo(message);
    }

    @Test
    public void testDisableAlertLevel() throws IOException, JoranException {
        environmentVariables.set("LOGBACK_REQUIRE_ALERT_LEVEL", "false");
        captureOutput();

        String message = "test alert level is not present";

        log.error(message, new ProviderException("oh no"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(baos.toString());

        assertThat(node.at("/alertLevel").isMissingNode()).isTrue();
        assertThat(node.at("/message").asText()).isEqualTo(message);
    }
}
