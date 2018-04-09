package uk.gov.hmcts.reform.logging.appinsights.telemetry.initializers;

import com.microsoft.applicationinsights.telemetry.Telemetry;
import com.microsoft.applicationinsights.web.extensibility.initializers.WebTelemetryInitializerBase;

public class CloudRoleNameInitializer extends WebTelemetryInitializerBase {
    @Override
    protected void onInitializeTelemetry(Telemetry telemetry) {
        if (SpringApplicationName.get() == null) {
            throw new IllegalStateException("spring.application.name configuration property is not set");
        } else {
            telemetry.getContext().getDevice().setRoleName(SpringApplicationName.get());
        }
    }
}
