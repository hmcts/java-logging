package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;

import static java.util.Objects.requireNonNull;

public abstract class AbstractAppInsights { // NOPMD

    protected final TelemetryClient telemetry;

    protected AbstractAppInsights(TelemetryClient telemetry, boolean devMode) {
        requireNonNull(
            telemetry.getContext().getInstrumentationKey(),
            "Missing APPLICATION_INSIGHTS_IKEY environment variable"
        );

        this.telemetry = telemetry;

        TelemetryConfiguration.getActive().getChannel().setDeveloperMode(devMode);
    }
}
