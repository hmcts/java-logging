package uk.gov.hmcts.reform.logging.filters;

import ch.qos.logback.classic.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.TestAppender;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static ch.qos.logback.classic.Level.ERROR;
import static ch.qos.logback.classic.Level.INFO;
import static net.logstash.logback.marker.Markers.appendEntries;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

public class RequestStatusLoggingFilterTest {
    private static final Clock FROZEN_CLOCK = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());
    private static final String GET = "GET";
    private static final String SOME_PATH = "/some/path";

    private final TestAppender testAppender = new TestAppender();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void addAppender() {
        ((Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME)).addAppender(testAppender);
    }

    @After
    public void removeAppender() {
        ((Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME)).detachAppender(testAppender);
    }

    @Test
    public void logsSuccessfulRequest() throws IOException, ServletException {
        new RequestStatusLoggingFilter(FROZEN_CLOCK).doFilter(
                requestWithMethodAndUri(),
                responseWithStatus(400),
                mock(FilterChain.class)
        );

        Map<String, Object> fields = new ConcurrentHashMap<>();
        fields.put("requestMethod", GET);
        fields.put("requestUri", SOME_PATH);
        fields.put("responseTime", 0L);
        fields.put("responseCode", 400);

        String message = "Request " + GET + " " + SOME_PATH + " processed in 0ms";

        testAppender.assertEvent(0, INFO, message, appendEntries(fields));
    }

    @Test
    public void logsFailedRequest() throws IOException, ServletException {
        thrown.expect(RuntimeException.class);

        new RequestStatusLoggingFilter(FROZEN_CLOCK).doFilter(
                requestWithMethodAndUri(),
                responseWithStatus(-1),
                failingFilterChain()
        );

        Map<String, Object> fields = new ConcurrentHashMap<>();
        fields.put("requestMethod", GET);
        fields.put("requestUri", SOME_PATH);
        fields.put("responseTime", 0L);

        String message = "Request " + GET + " " + SOME_PATH + " failed in 0ms";

        testAppender.assertEvent(0, ERROR, message, appendEntries(fields));
    }

    private HttpServletRequest requestWithMethodAndUri() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(GET);
        when(request.getRequestURI()).thenReturn(SOME_PATH);
        return request;
    }

    private HttpServletResponse responseWithStatus(int statusCode) {
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getStatus()).thenReturn(statusCode);
        return response;
    }

    private FilterChain failingFilterChain() throws IOException, ServletException {
        FilterChain chain = mock(FilterChain.class);
        doThrow(new RuntimeException("something failed")).when(chain).doFilter(any(), any());
        return chain;
    }
}
