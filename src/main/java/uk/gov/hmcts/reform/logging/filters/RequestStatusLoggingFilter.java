package uk.gov.hmcts.reform.logging.filters;

import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(FilterConfig filterConfig) {
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        long startTime = clock.millis();

        // we use Marker instead of StructuredArgument because we want all
        // these fields to appear at the top level of the JSON
        try {
            chain.doFilter(request, response);

            logMessage(request, response, startTime, true, null);
        } catch (Exception e) {
            logMessage(request, null, startTime, false, e);

            throw e;
        }
    }

    private void logMessage(ServletRequest request,
                            ServletResponse response,
                            long startTime,
                            boolean isSuccess,
                            Throwable cause) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestMethod = httpServletRequest.getMethod();
        String requestUri = httpServletRequest.getRequestURI();
        long responseTime = clock.millis() - startTime;

        // collect markers
        Map<String, Object> fields = new ConcurrentHashMap<>();

        fields.put("requestMethod", requestMethod);
        fields.put("requestUri", requestUri);
        fields.put("responseTime", responseTime);

        if (response != null) {
            fields.put("responseCode", ((HttpServletResponse) response).getStatus());
        }

        LogstashMarker marker = appendEntries(fields);

        // format the message
        String status = isSuccess ? "processed" : "failed";
        String message = String.format("Request %s %s %s in %dms", requestMethod, requestUri, status, responseTime);

        // log the event
        if (isSuccess) {
            LOG.info(marker, message);
        } else {
            LOG.error(marker, message, cause);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void destroy() {
        LOG.debug("Status logging destroyed due to timeout or filter exit");
    }
}
