package uk.gov.hmcts.reform.logging.httpcomponents;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.RequestLine;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

import static java.time.Clock.systemUTC;

import static net.logstash.logback.marker.Markers.appendEntries;
import static org.apache.http.protocol.HttpCoreContext.HTTP_TARGET_HOST;

public class OutboundRequestLoggingInterceptor implements HttpRequestInterceptor, HttpResponseInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(OutboundRequestLoggingInterceptor.class);
    private final Clock clock;

    public OutboundRequestLoggingInterceptor() {
        this(systemUTC());
    }

    OutboundRequestLoggingInterceptor(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        RequestLine requestLine = request.getRequestLine();

        context.setAttribute("startedOn", clock.millis());
        context.setAttribute("method", requestLine.getMethod());
        context.setAttribute("url", context.getAttribute(HTTP_TARGET_HOST) + requestLine.getUri());

        LOG.info(appendEntries(requestMarkers(context)), "Outbound request start");
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        LOG.info(appendEntries(responseMarkers(context, response)), "Outbound request finish");
    }

    private Map<String, Object> responseMarkers(HttpContext context, HttpResponse response) {
        Map<String, Object> markers = requestMarkers(context);
        markers.put("responseTime", clock.millis() - (long) context.getAttribute("startedOn"));
        markers.put("responseCode", response.getStatusLine().getStatusCode());
        return markers;
    }

    private Map<String, Object> requestMarkers(HttpContext context) {
        Map<String, Object> markers = new HashMap<>();
        markers.put("requestMethod", context.getAttribute("method"));
        markers.put("requestURI", context.getAttribute("url"));
        return markers;
    }

}
