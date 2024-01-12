package uk.gov.hmcts.reform.logging.appinsights.telemetry.initializers;

import com.microsoft.applicationinsights.extensibility.ContextInitializer;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;

/**
 * ContextVersionInitializer  to set version to telemetry.
 *
 * <p>
 * This Telemetry Initializer is used to auto-configure version field as current spring boot starter for
 * app insights is not setting this.
 */
public class ContextVersionInitializer implements ContextInitializer {
    @Override
    public void initialize(TelemetryContext telemetryContext) {
        telemetryContext.getComponent().setVersion(getClass().getPackage().getImplementationVersion());
    }

}
