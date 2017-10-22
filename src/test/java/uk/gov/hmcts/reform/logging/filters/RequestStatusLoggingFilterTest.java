package uk.gov.hmcts.reform.logging.filters;

import ch.qos.logback.classic.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
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

    private TestAppender testAppender = new TestAppender();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void addAppender() throws Exception {
        ((Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME)).addAppender(testAppender);
    }

    @After
    public void removeAppender() throws Exception {
        ((Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME)).detachAppender(testAppender);
    }

    @Test
    public void logsSuccessfulRequest() throws IOException, ServletException {
        new RequestStatusLoggingFilter(FROZEN_CLOCK).doFilter(
                requestWithMethodAndUri("GET", "/some/path"),
                responseWithStatus(400),
                mock(FilterChain.class)
        );

        Map<String, Object> fields = new HashMap<>();
        fields.put("requestMethod", "GET");
        fields.put("requestUri", "/some/path");
        fields.put("responseTime", 0L);
        fields.put("responseCode", 400);

        testAppender.assertEvent(0, INFO, "Request GET /some/path processed in 0ms", appendEntries(fields));
    }

    @Test
    public void logsFailedRequest() throws IOException, ServletException {
        thrown.expect(RuntimeException.class);

        new RequestStatusLoggingFilter(FROZEN_CLOCK).doFilter(
                requestWithMethodAndUri("GET", "/some/path"),
                responseWithStatus(-1),
                failingFilterChain()
        );

        Map<String, Object> fields = new HashMap<>();
        fields.put("requestMethod", "GET");
        fields.put("requestUri", "/some/path");
        fields.put("responseTime", 0L);

        testAppender.assertEvent(0, ERROR, "Request GET /some/path failed in 0ms", appendEntries(fields));
    }

    private HttpServletRequest requestWithMethodAndUri(String method, String url) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURI()).thenReturn(url);
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
