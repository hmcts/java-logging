package uk.gov.hmcts.reform.logging.provider;

import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.AbstractLoggingTestSuite;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorCodeJsonProviderTest extends AbstractLoggingTestSuite {

    @Before
    public void setUp() throws IOException, JoranException {
        System.setProperty("ROOT_APPENDER", "JSON_CONSOLE");

        captureOutput("logback.xml");
    }

    @Test
    public void testAlertLevel() throws IOException {
        assertThat(System.getProperty("ROOT_APPENDER")).isEqualTo("JSON_CONSOLE");

        Logger log = LoggerFactory.getLogger(ErrorCodeJsonProviderTest.class);

        log.error("test", new ProviderException("oh no"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(baos.toString());

        assertThat(node.at("/errorCode").asText()).isEqualTo("0");
        assertThat(node.at("/message").asText()).isEqualTo("test");
    }
}
