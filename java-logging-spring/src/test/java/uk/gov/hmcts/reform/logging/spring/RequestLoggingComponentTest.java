package uk.gov.hmcts.reform.logging.spring;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "logging.config=src/test/resources/logback-test-sender.xml")
public class RequestLoggingComponentTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private List<FilterRegistrationBean> filters;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void listenForEvents() throws JoranException, IOException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        JoranConfigurator configurator = new JoranConfigurator();

        InputStream configStream = ResourceUtils.getURL("classpath:logback-test-receiver.xml").openStream();
        configurator.setContext(loggerContext);
        configurator.doConfigure(configStream);
        configStream.close();
    }

    @Test
    public void requestProcessedMessageShouldBeLoggedForPublicResource() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/public", String.class);

        assertThat(response.getBody()).isEqualTo("OK");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(loggedEvents().stream().filter(
            event -> event.getFormattedMessage().startsWith("Request GET /public processed")
        ).collect(Collectors.toList())).size().isEqualTo(1);
    }

    @Test
    public void requestProcessedMessageShouldBeLoggedForProtectedResource() throws Exception {
        // making sure our Filters are placed outside SecurityFilterChain
        // and get executed no matter request is allowed or not
        ResponseEntity<String> response = restTemplate.getForEntity("/protected", String.class);

        assertThat(response.getBody()).isNotEqualTo("OK");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        assertThat(loggedEvents().stream().filter(
            event -> event.getFormattedMessage().startsWith("Request GET /protected processed")
        ).collect(Collectors.toList())).size().isEqualTo(1);
    }

    @Test
    public void requestFailedMessageShouldBeLoggedForFailingResource() throws Exception {
        // making sure our Filters are not executed twice for failed requests
        ResponseEntity<String> response = restTemplate.getForEntity("/failing", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        assertThat(loggedEvents().stream().filter(
            event -> event.getFormattedMessage().startsWith("Request GET /failing failed")
        ).collect(Collectors.toList())).size().isEqualTo(1);
    }

    @Test
    public void requestNotFoundMessageShouldBeLoggedForDestroyingResource() throws Exception {
        // would be better to close context but fails completing tests
        filters.forEach(filter -> filter.getFilter().destroy());
        ResponseEntity<String> response = restTemplate.getForEntity("/destroying", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        List<ILoggingEvent> events = loggedEvents();

        assertThat(events).extracting("level", "message")
            .containsOnlyOnce(Tuple.tuple(Level.DEBUG, "Status logging destroyed due to timeout or filter exit"));

        assertThat(events.stream().filter(
            event -> event.getFormattedMessage().startsWith("Request GET /destroying processed")
        ).collect(Collectors.toList())).size().isEqualTo(1);
    }

    private List<ILoggingEvent> loggedEvents() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
        TestAppender testAppender = (TestAppender) rootLogger.getAppender("TEST_APPENDER");
        return testAppender.loggedEvents();
    }
}
