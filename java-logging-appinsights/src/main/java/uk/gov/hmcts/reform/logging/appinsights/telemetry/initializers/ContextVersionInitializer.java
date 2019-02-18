package uk.gov.hmcts.reform.logging.appinsights.telemetry.initializers;

import com.microsoft.applicationinsights.extensibility.TelemetryInitializer;
import com.microsoft.applicationinsights.telemetry.Telemetry;

/**
 * ContextVersionInitializer  to set version to telemetry.
 *
 * <p>This Telemetry Initializer is used to auto-configure version field as current spring boot starter for
 * app insights is not setting this.
 * This is to deprecate {@link uk.gov.hmcts.reform.logging.appinsights.AbstractAppInsights} </p>
 */
public class ContextVersionInitializer implements TelemetryInitializer {
    @Override
    public void initialize(Telemetry telemetry) {
        telemetry.getContext().getComponent().setVersion(getClass().getPackage().getImplementationVersion());
    }

}
