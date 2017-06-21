package uk.gov.hmcts.reform.logging.filters;

import org.junit.Test;
import uk.gov.hmcts.reform.logging.HttpHeaders;
import uk.gov.hmcts.reform.logging.MdcFields;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestIdsSettingFilterTest {

    private static final String GENERATED_REQUEST_ID = "some-generated-request-id";
    private static final ServletResponse ANY_RESPONSE = null;

    private final RequestIdsSettingFilter filter = new RequestIdsSettingFilter(() -> GENERATED_REQUEST_ID);

    @Test
    public void generateAndLogRequestId() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        filter.doFilter(request, ANY_RESPONSE, (req, resp) -> {
            assertThat(MdcFields.getRequestId()).isEqualTo(GENERATED_REQUEST_ID);
        });

        assertThat(MdcFields.getRequestId()).isNull();
    }

    @Test
    public void rootRequestIdShouldBeReusedIfHeaderPresent() throws IOException, ServletException {
        HttpServletRequest request = requestWithRootRequestId("root-request-id");

        filter.doFilter(request, ANY_RESPONSE, (req, resp) -> {
            assertThat(MdcFields.getRootRequestId()).isEqualTo("root-request-id");
        });

        assertThat(MdcFields.getRootRequestId()).isNull();
    }

    @Test
    public void rootRequestIdShouldBeGeneratedIfHeaderNotPresent() throws IOException, ServletException {
        HttpServletRequest request = requestWithRootRequestId(null);

        filter.doFilter(request, ANY_RESPONSE, (req, resp) -> {
            assertThat(MdcFields.getRequestId()).isEqualTo(GENERATED_REQUEST_ID);
            assertThat(MdcFields.getRootRequestId()).isEqualTo(GENERATED_REQUEST_ID);
        });

        assertThat(MdcFields.getRootRequestId()).isNull();
    }

    @Test
    public void originRequestIdShouldBeUsedIfHeaderPresent() throws IOException, ServletException {
        HttpServletRequest request = requestWithOriginRequestId("origin-request-id");

        filter.doFilter(request, ANY_RESPONSE, (req, resp) -> {
            assertThat(MdcFields.getOriginRequestId()).isEqualTo("origin-request-id");
        });

        assertThat(MdcFields.getOriginRequestId()).isNull();
    }

    @Test
    public void originRequestIdShouldNotBeGeneratedIfHeaderNotPresent() throws IOException, ServletException {
        HttpServletRequest request = requestWithOriginRequestId(null);

        filter.doFilter(request, ANY_RESPONSE, (req, resp) -> {
            assertThat(MdcFields.getOriginRequestId()).isEqualTo(null);
        });

        assertThat(MdcFields.getOriginRequestId()).isNull();
    }

    @Test
    public void sessionIdShouldBeLoggedIfTheSessionExists() throws IOException, ServletException {
        HttpServletRequest request = requestWithSessionId("some-session-id");

        filter.doFilter(request, ANY_RESPONSE, (req, resp) -> {
            assertThat(MdcFields.getSessionId()).isEqualTo("some-session-id");
        });

        assertThat(MdcFields.getSessionId()).isNull();
    }

    @Test
    public void sessionIdShouldNotBeLoggedIfSessionDoesNotExist() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        filter.doFilter(request, ANY_RESPONSE, (req, resp) -> {
            assertThat(MdcFields.getSessionId()).isNull();
        });

        assertThat(MdcFields.getSessionId()).isNull();
    }

    private HttpServletRequest requestWithRootRequestId(String requestId) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(HttpHeaders.ROOT_REQUEST_ID)).thenReturn(requestId);
        return request;
    }

    private HttpServletRequest requestWithOriginRequestId(String requestId) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(HttpHeaders.ORIGIN_REQUEST_ID)).thenReturn(requestId);
        return request;
    }

    private HttpServletRequest requestWithSessionId(String sessionId) {
        HttpSession httpSession = mock(HttpSession.class);
        when(httpSession.getId()).thenReturn(sessionId);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession(false)).thenReturn(httpSession);
        return request;
    }
}
