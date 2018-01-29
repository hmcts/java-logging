package uk.gov.hmcts.reform.logging.filters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.logging.HttpHeaders;
import uk.gov.hmcts.reform.logging.MdcFields;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestIdsSettingFilterTest {

    private static final String GENERATED_REQUEST_ID = "some-generated-request-id";

    @Mock
    private ServletResponse response;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession httpSession;

    private final RequestIdsSettingFilter filter = new RequestIdsSettingFilter(() -> GENERATED_REQUEST_ID);

    @Before
    public void beforeEach() {
        MdcFields.removeAll();
    }

    @Test
    public void allRequestHeadersShouldBeReusedIfRequestIdHeaderIsProvided() throws IOException, ServletException {
        HttpServletRequest request = requestWithHeaders("id", "originId", "rootId");
        filter.doFilter(request, response, (req, resp) -> {
            assertThat(MdcFields.getRequestId()).isEqualTo("id");
            assertThat(MdcFields.getOriginRequestId()).isEqualTo("originId");
            assertThat(MdcFields.getRootRequestId()).isEqualTo("rootId");
        });
    }

    @Test
    public void originIdShouldNotBeSetWhenItsBlankAndRequestIdIsPresent() throws IOException, ServletException {
        HttpServletRequest request = requestWithHeaders("id", null, null);
        filter.doFilter(request, response, (req, resp) -> {
            assertThat(MdcFields.getRequestId()).isEqualTo("id");
            assertThat(MdcFields.getOriginRequestId()).isNullOrEmpty();
        });
    }

    @Test
    public void rootIdShouldBeSetToRequestIdWhenItsBlankAndRequestIdIsPresent() throws IOException, ServletException {
        HttpServletRequest request = requestWithHeaders("id", null, null);
        filter.doFilter(request, response, (req, resp) -> {
            assertThat(MdcFields.getRequestId()).isEqualTo("id");
            assertThat(MdcFields.getRootRequestId()).isEqualTo(MdcFields.getRequestId());
        });
    }

    @Test
    public void requestAndRootRequestIdsShouldBeSetWhenRequestIdIsNull() throws IOException, ServletException {
        HttpServletRequest request = requestWithHeaders(null, null, null);
        filter.doFilter(request, response, (req, resp) -> {
            assertThat(MdcFields.getRequestId()).isEqualTo(GENERATED_REQUEST_ID);
            assertThat(MdcFields.getRootRequestId()).isEqualTo(MdcFields.getRequestId());
            assertThat(MdcFields.getOriginRequestId()).isNullOrEmpty();
        });
    }

    @Test
    public void requestAndRootRequestIdsShouldBeSetWhenRequestIdIsBlank() throws IOException, ServletException {
        HttpServletRequest request = requestWithHeaders("", null, null);
        filter.doFilter(request, response, (req, resp) -> {
            assertThat(MdcFields.getRequestId()).isEqualTo(GENERATED_REQUEST_ID);
            assertThat(MdcFields.getRootRequestId()).isEqualTo(MdcFields.getRequestId());
            assertThat(MdcFields.getOriginRequestId()).isNullOrEmpty();
        });
    }

    @Test
    public void sessionIdShouldBeLoggedIfTheSessionExists() throws IOException, ServletException {
        HttpServletRequest request = requestWithSessionId("some-session-id");

        filter.doFilter(request, response, (req, resp) -> {
            assertThat(MdcFields.getSessionId()).isEqualTo("some-session-id");
        });

        assertThat(MdcFields.getSessionId()).isNull();
    }

    @Test
    public void sessionIdShouldNotBeLoggedIfSessionDoesNotExist() throws IOException, ServletException {
        filter.doFilter(request, response, (req, resp) -> {
            assertThat(MdcFields.getSessionId()).isNull();
        });

        assertThat(MdcFields.getSessionId()).isNull();
    }

    private HttpServletRequest requestWithHeaders(String requestId, String originRequestId, String rootRequestId) {
        when(request.getHeader(HttpHeaders.REQUEST_ID)).thenReturn(requestId);
        when(request.getHeader(HttpHeaders.ORIGIN_REQUEST_ID)).thenReturn(originRequestId);
        when(request.getHeader(HttpHeaders.ROOT_REQUEST_ID)).thenReturn(rootRequestId);
        return request;
    }

    private HttpServletRequest requestWithSessionId(String sessionId) {
        when(httpSession.getId()).thenReturn(sessionId);

        when(request.getSession(false)).thenReturn(httpSession);
        return request;
    }
}
