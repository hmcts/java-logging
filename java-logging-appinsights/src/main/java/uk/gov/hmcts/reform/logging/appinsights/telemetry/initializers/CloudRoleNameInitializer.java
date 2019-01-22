package uk.gov.hmcts.reform.logging.appinsights.telemetry.initializers;

import com.microsoft.applicationinsights.extensibility.TelemetryInitializer;
import com.microsoft.applicationinsights.telemetry.Telemetry;

public class CloudRoleNameInitializer implements TelemetryInitializer {

    @Override
    public void initialize(Telemetry telemetry) {
        if (SpringApplicationName.get() == null) {
            throw new IllegalStateException("spring.application.name configuration property is not set");
        } else {
            telemetry.getContext().getDevice().setRoleName(SpringApplicationName.get());
        }
    }
}
