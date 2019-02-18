package uk.gov.hmcts.reform.logging.appinsights;

/**
 * Utility class to provide headers used in WebSyntheticRequestTelemetryInitializer.
 * It has package private access to those fields - making it public here for re-usability.
 * This needs to deprecated upon closing of https://github.com/Microsoft/ApplicationInsights-Java/pull/813
 */
public final class SyntheticHeaders {

    public static final String SYNTHETIC_TEST_RUN_ID = "SyntheticTest-RunId";
    public static final String SYNTHETIC_TEST_LOCATION = "SyntheticTest-Location";
    public static final String SYNTHETIC_TEST_SOURCE = "SyntheticTest-Source";
    public static final String SYNTHETIC_TEST_TEST_NAME = "SyntheticTest-TestName";
    public static final String SYNTHETIC_TEST_SESSION_ID = "SyntheticTest-SessionId";
    public static final String SYNTHETIC_TEST_USER_ID = "SyntheticTest-UserId";
    public static final String SYNTHETIC_TEST_OPERATION_ID = "SyntheticTest-OperationId";

    private SyntheticHeaders() {
        // utility class constructor
    }
}
