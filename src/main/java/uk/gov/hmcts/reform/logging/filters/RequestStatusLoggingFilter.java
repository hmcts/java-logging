package uk.gov.hmcts.reform.logging.filters;

import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static net.logstash.logback.marker.Markers.appendEntries;


public class RequestStatusLoggingFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(RequestStatusLoggingFilter.class);

    private final Clock clock;

    public RequestStatusLoggingFilter() {
        this(Clock.systemDefaultZone());
    }

    public RequestStatusLoggingFilter(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long startTime = clock.millis();

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String method = httpServletRequest.getMethod();
        String uri = httpServletRequest.getRequestURI();

        // we use Marker instead of StructuredArgument because we want all
        // these fields to appear at the top level of the JSON
        try {
            chain.doFilter(request, response);
            long processed = clock.millis() - startTime;
            LOG.info(markersFor(method, response), buildLogMessage(method, uri, processed, true));
        } catch (Exception e) {
            long processed = clock.millis() - startTime;
            LOG.error(markersFor(method, null), buildLogMessage(method, uri, processed, false), e);
            throw e;
        }
    }

    private String buildLogMessage(String requestMethod, String requestUri, long period, boolean isSuccess) {
        String status = isSuccess ? "processed" : "failed";

        return String.format("Request %s %s %s in %dms", requestMethod, requestUri, status, period);
    }

    private LogstashMarker markersFor(String requestMethod, ServletResponse response) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("requestMethod", requestMethod);

        if (response != null) {
            fields.put("responseCode", ((HttpServletResponse) response).getStatus());
        }

        return appendEntries(fields);
    }

    @Override
    public void destroy() {
        LOG.debug("Status logging destroyed due to timeout or filter exit");
    }
}
