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

    private static final Logger log = LoggerFactory.getLogger(ErrorCodeJsonProviderTest.class);

    @Before
    @Override
    public void setUp() {
        super.setUp();
        setJsonConsoleAppender();
    }

    @Test
    public void testErrorCode() throws IOException, JoranException {
        captureOutput();

        assertThat(System.getenv("ROOT_APPENDER")).isEqualTo("JSON_CONSOLE");

        String message = "test error code is present";

        log.error(message, new ProviderException("oh no"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(systemOut.getLog());

        assertThat(node.at("/errorCode").asText()).isEqualTo("0");
        assertThat(node.at("/message").asText()).isEqualTo(message);
    }

    @Test
    public void testDisableErrorCode() throws IOException, JoranException {
        environmentVariables.set("LOGBACK_REQUIRE_ERROR_CODE", "false");
        captureOutput();

        String message = "test error code is not present";

        log.error(message, new ProviderException("oh no"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(systemOut.getLog());

        assertThat(node.at("/errorCode").isMissingNode()).isTrue();
        assertThat(node.at("/message").asText()).isEqualTo(message);
    }
}
