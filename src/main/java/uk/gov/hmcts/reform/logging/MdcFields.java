package uk.gov.hmcts.reform.logging;

import org.slf4j.MDC;

public final class MdcFields {

    private static final String FIELD_SESSION_ID = "sessionId";
    private static final String FIELD_ROOT_REQUEST_ID = "rootRequestId";
    private static final String FIELD_ORIGIN_REQUEST_ID = "originRequestId";
    private static final String FIELD_REQUEST_ID = "requestId";

    private MdcFields() {
    }

    public static void setSessionId(String sessionId) {
        MDC.put(FIELD_SESSION_ID, sessionId);
    }

    public static String getSessionId() {
        return MDC.get(FIELD_SESSION_ID);
    }

    public static void setRootRequestId(String requestId) {
        MDC.put(FIELD_ROOT_REQUEST_ID, requestId);
    }

    public static String getRootRequestId() {
        return MDC.get(FIELD_ROOT_REQUEST_ID);
    }

    public static void setOriginRequestId(String requestId) {
        MDC.put(FIELD_ORIGIN_REQUEST_ID, requestId);
    }

    public static String getOriginRequestId() {
        return MDC.get(FIELD_ORIGIN_REQUEST_ID);
    }

    public static void setRequestId(String requestId) {
        MDC.put(FIELD_REQUEST_ID, requestId);
    }

    public static String getRequestId() {
        return MDC.get(FIELD_REQUEST_ID);
    }

    public static void removeAll() {
        MDC.remove(FIELD_SESSION_ID);
        MDC.remove(FIELD_ROOT_REQUEST_ID);
        MDC.remove(FIELD_ORIGIN_REQUEST_ID);
        MDC.remove(FIELD_REQUEST_ID);
    }
}
