package uk.gov.hmcts.reform.logging.httpcomponents;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import uk.gov.hmcts.reform.logging.HttpHeaders;
import uk.gov.hmcts.reform.logging.MdcFields;

public class OutboundRequestIdSettingInterceptor implements HttpRequestInterceptor {

    @Override
    public void process(HttpRequest request, HttpContext context) {
        request.setHeader(HttpHeaders.ROOT_REQUEST_ID, MdcFields.getRootRequestId());
        request.setHeader(HttpHeaders.ORIGIN_REQUEST_ID, MdcFields.getRequestId());
    }
}
