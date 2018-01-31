package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;

import static java.util.Objects.requireNonNull;

/**
 * Application Insights class to send custom telemetry to AppInsights.
 *
 * <code>// NO PMD</code> is for pmd rule to skip the check of any abstract methods being present in abstract class.
 */
public abstract class AbstractAppInsights { // NOPMD

    protected final TelemetryClient telemetry;

    protected AbstractAppInsights(TelemetryClient telemetry) {
        requireNonNull(
            telemetry.getContext().getInstrumentationKey(),
            "Missing APPLICATION_INSIGHTS_IKEY environment variable"
        );

        this.telemetry = telemetry;
    }
}
