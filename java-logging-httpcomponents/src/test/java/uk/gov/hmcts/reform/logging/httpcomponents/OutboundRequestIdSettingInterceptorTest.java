package uk.gov.hmcts.reform.logging.httpcomponents;

import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.logging.HttpHeaders;
import uk.gov.hmcts.reform.logging.MdcFields;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class OutboundRequestIdSettingInterceptorTest {

    private static final String ANY = "any";

    private OutboundRequestIdSettingInterceptor interceptor;

    @Mock
    private HttpContext context;

    private BasicHttpRequest request;

    @Before
    public void beforeEachTest() {
        interceptor = new OutboundRequestIdSettingInterceptor();
        request = new BasicHttpRequest(ANY, ANY);
    }

    @Test
    public void requestIdShouldBeGenerated() {
        interceptor = new OutboundRequestIdSettingInterceptor(() -> "test-id");
        interceptor.process(request, context);
        assertThat(request.getFirstHeader(HttpHeaders.REQUEST_ID).getValue()).isEqualTo("test-id");
    }

    @Test
    public void rootRequestIdShouldBeSetFromRootRequest() {
        MdcFields.setRootRequestId("ROOT_REQUEST_ID");

        interceptor.process(request, context);

        assertThat(request.getHeaders(HttpHeaders.ROOT_REQUEST_ID)).hasSize(1);
        assertThat(request.getFirstHeader(HttpHeaders.ROOT_REQUEST_ID).getValue()).isEqualTo("ROOT_REQUEST_ID");
    }

    @Test
    public void originRequestIdShouldBeSetFromCurrentRequest() {
        MdcFields.setRequestId("CURRENT_REQUEST_ID");

        interceptor.process(request, context);

        assertThat(request.getHeaders(HttpHeaders.ORIGIN_REQUEST_ID)).hasSize(1);
        assertThat(request.getFirstHeader(HttpHeaders.ORIGIN_REQUEST_ID).getValue()).isEqualTo("CURRENT_REQUEST_ID");
    }

}
