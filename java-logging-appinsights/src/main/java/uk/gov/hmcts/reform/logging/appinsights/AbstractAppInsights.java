package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;

import static java.util.Objects.requireNonNull;

/**
 * <h1> @deprecated TelemetryClient can be directly used for any custom implementation.</h1>
 * Application Insights class to send custom telemetry to AppInsights.
 *
 * <code>// NO PMD</code> is for pmd rule to skip the check of any abstract methods being present in abstract class.
 *
 */
@Deprecated
public abstract class AbstractAppInsights { // NOPMD

    protected final TelemetryClient telemetry;

    protected AbstractAppInsights(TelemetryClient telemetry) {
        requireNonNull(
            telemetry.getContext().getInstrumentationKey(),
            "Missing APPINSIGHTS_INSTRUMENTATIONKEY environment variable"
        );
        telemetry.getContext().getComponent().setVersion(getClass().getPackage().getImplementationVersion());

        this.telemetry = telemetry;
    }
}
