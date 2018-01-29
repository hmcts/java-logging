package uk.gov.hmcts.reform.logging.httpcomponents;

import ch.qos.logback.classic.Logger;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.assertj.core.data.Index;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.TestAppender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ch.qos.logback.classic.Level.INFO;
import static net.logstash.logback.marker.Markers.appendEntries;
import static org.apache.http.protocol.HttpCoreContext.HTTP_TARGET_HOST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

public class OutboundRequestLoggingInterceptorTest {

    private static final ProtocolVersion ANY_PROTOCOL = new ProtocolVersion("any", 0, 0);

    private final TestAppender testAppender = new TestAppender();

    @Before
    public void addAppender() {
        ((Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME)).addAppender(testAppender);
    }

    @After
    public void removeAppender() {
        ((Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME)).detachAppender(testAppender);
    }

    @Test
    public void logsRequestAndResponseFields() {
        HttpContext context = new BasicHttpContext();
        context.setAttribute(HTTP_TARGET_HOST, "http://www.google.com");

        OutboundRequestLoggingInterceptor interceptor = new OutboundRequestLoggingInterceptor(new FakeClock(20));

        interceptor.process(new BasicHttpRequest("GET", "/something"), context);
        interceptor.process(new BasicHttpResponse(new BasicStatusLine(ANY_PROTOCOL, 200, "any")), context);

        Map<String, Object> fields = new ConcurrentHashMap<>();
        fields.put("requestMethod", "GET");
        fields.put("requestURI", "http://www.google.com/something");
        testAppender.assertEvent(0, INFO, "Outbound request start", appendEntries(fields));

        fields.put("responseTime", 20L);
        fields.put("responseCode", 200);
        testAppender.assertEvent(1, INFO, "Outbound request finish", appendEntries(fields));
    }

    @Test
    public void allowEmptyConstructorToBuildDefaultClock() {
        testAppender.clearEvents();

        HttpContext context = new BasicHttpContext();
        context.setAttribute(HTTP_TARGET_HOST, "http://www.google.com");

        OutboundRequestLoggingInterceptor interceptor = new OutboundRequestLoggingInterceptor();

        interceptor.process(new BasicHttpRequest("GET", "/something"), context);
        interceptor.process(new BasicHttpResponse(new BasicStatusLine(ANY_PROTOCOL, 200, "any")), context);

        assertThat(testAppender.getEvents()).extracting("message")
            .contains("Outbound request start", Index.atIndex(0))
            .contains("Outbound request finish", Index.atIndex(1));
    }
}
