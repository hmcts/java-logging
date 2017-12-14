package uk.gov.hmcts.reform.logging.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.HttpHeaders;
import uk.gov.hmcts.reform.logging.MdcFields;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RequestIdsSettingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestIdsSettingFilter.class);

    private final Supplier<String> requestIdGenerator;

    public RequestIdsSettingFilter() {
        this(() -> UUID.randomUUID().toString());
    }

    public RequestIdsSettingFilter(Supplier<String> requestIdGenerator) {
        this.requestIdGenerator = requestIdGenerator;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(FilterConfig filterConfig) {
        // Nothing to do
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String rootRequestId = httpServletRequest.getHeader(HttpHeaders.ROOT_REQUEST_ID);

            MdcFields.setSessionId(getSessionId(httpServletRequest));
            MdcFields.setRequestId(requestIdGenerator.get());
            MdcFields.setRootRequestId(rootRequestId == null ? MdcFields.getRequestId() : rootRequestId);
            MdcFields.setOriginRequestId(httpServletRequest.getHeader(HttpHeaders.ORIGIN_REQUEST_ID));

            chain.doFilter(request, response);
        } finally {
            MdcFields.removeAll();
        }
    }

    private String getSessionId(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        return session == null ? null : session.getId();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void destroy() {
        LOG.debug("Settings logging destroyed due to timeout or filter exit");
    }
}
