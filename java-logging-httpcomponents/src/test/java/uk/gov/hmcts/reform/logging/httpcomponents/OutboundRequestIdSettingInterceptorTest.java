package uk.gov.hmcts.reform.logging.httpcomponents;

import org.apache.http.message.BasicHttpRequest;
import org.junit.Test;
import uk.gov.hmcts.reform.logging.HttpHeaders;
import uk.gov.hmcts.reform.logging.MdcFields;

import static org.assertj.core.api.Assertions.assertThat;

public class OutboundRequestIdSettingInterceptorTest {

    private final OutboundRequestIdSettingInterceptor interceptor = new OutboundRequestIdSettingInterceptor();

    private static final String ANY = "any";

    @Test
    public void rootRequestIdShouldBeSetFromRootRequest() {
        MdcFields.setRootRequestId("ROOT_REQUEST_ID");

        BasicHttpRequest request = new BasicHttpRequest(ANY, ANY);
        interceptor.process(request, null);

        assertThat(request.getHeaders(HttpHeaders.ROOT_REQUEST_ID)).hasSize(1);
        assertThat(request.getFirstHeader(HttpHeaders.ROOT_REQUEST_ID).getValue()).isEqualTo("ROOT_REQUEST_ID");
    }

    @Test
    public void originRequestIdShouldBeSetFromCurrentRequest() {
        MdcFields.setRequestId("CURRENT_REQUEST_ID");

        BasicHttpRequest request = new BasicHttpRequest(ANY, ANY);
        interceptor.process(request, null);

        assertThat(request.getHeaders(HttpHeaders.ORIGIN_REQUEST_ID)).hasSize(1);
        assertThat(request.getFirstHeader(HttpHeaders.ORIGIN_REQUEST_ID).getValue()).isEqualTo("CURRENT_REQUEST_ID");
    }
}
