package uk.gov.hmcts.reform.logging.httpcomponents;

import org.apache.http.HttpException;
import org.apache.http.message.BasicHttpRequest;
import org.junit.Test;
import uk.gov.hmcts.reform.logging.HttpHeaders;
import uk.gov.hmcts.reform.logging.MdcFields;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class OutboundRequestIdSettingInterceptorTest {

    private final OutboundRequestIdSettingInterceptor interceptor = new OutboundRequestIdSettingInterceptor();

    private static final String ANY = "any";

    @Test
    public void rootRequestIdShouldBeSet() throws IOException, HttpException {
        MdcFields.setRootRequestId("ROOT_REQUEST_ID");

        BasicHttpRequest request = new BasicHttpRequest(ANY, ANY);
        interceptor.process(request, null);

        assertThat(request.getHeaders(HttpHeaders.ROOT_REQUEST_ID)).hasSize(1);
        assertThat(request.getFirstHeader(HttpHeaders.ROOT_REQUEST_ID).getValue()).isEqualTo("ROOT_REQUEST_ID");
    }

    @Test
    public void originRequestIdShouldBeSet() throws IOException, HttpException {
        MdcFields.setRequestId("CURRENT_REQUEST_ID");

        BasicHttpRequest request = new BasicHttpRequest(ANY, ANY);
        interceptor.process(request, null);

        assertThat(request.getHeaders(HttpHeaders.ORIGIN_REQUEST_ID)).hasSize(1);
        assertThat(request.getFirstHeader(HttpHeaders.ORIGIN_REQUEST_ID).getValue()).isEqualTo("CURRENT_REQUEST_ID");
    }
}
